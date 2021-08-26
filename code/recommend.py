import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from konlpy.tag import Okt
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import pymysql
import sys
from keys import dbhost,dbpw

def db_connection():  # 데이터베이스 연결
    db = pymysql.connect(
        user='hanium',
        passwd=dbpw,
        host=dbhost,
        port=3306,
        db='hanium',
        charset='utf8'
    )
    print("db connected")
    return db

def db_select_favorite(userId): # 선호 공고 데이터 불러오기
    cursor = db.cursor(pymysql.cursors.DictCursor)
    
    try:
        sql = """select recruit_id from temp_favorite where user_id = %s;"""
        cursor.execute(sql,userId)
        results = cursor.fetchall()
        result = []
        for i in results:
            result.append(i['recruit_id'])
    except pymysql.err.internalError as e:
        code,msg = e.args
    finally:
        cursor.close()
        print("db_select_favorite()")
        return result

def recommend():
  #불용어
  stopwords = ["모집","급구","요건","임금","다음","업무","지원","및","근무","채용","우대","등","구인","고용","사항","직무","근무시간","직종","기타","형태","내용","조건","시간","기간","마감","관련","기준","담당","워크넷","가능","불가능","월급","로","문의"]

  #형태소 분석기 생성
  okt = Okt() 

  #명사만 추출 후 한 글자인 단어 제거
  def token(phrase): 
    nouns = okt.nouns(phrase)
    result = []
    for i in nouns:
      if len(i)>1:
        result.append(i)
    return result

  #TfidfVectorizer 생성
  tfidfv = TfidfVectorizer(tokenizer=token,min_df=3, max_df=0.9, stop_words=stopwords)    

  #공고 제목, 내용, 직종명, 요구 자격증명 합쳐서 문서 생성
  texts = []  
  for i in range(df.index.size):
    if type(df['certificate_name'][i]) == float :
      df['certificate_name'][i] = ""
    texts.append(df['title'][i] + "\n" + df['content'][i] + "\n" + df['job_name'][i] + "\n" + df['certificate_name'][i])
  texts[0]

  #문서 정규화
  content = [okt.normalize(texts[i]) for i in range(df.index.size)]  

  #전체 공고 벡터
  ftr_vect = tfidfv.fit_transform(content)   

  #사용자 선호 공고 벡터
  my_vect = ftr_vect[my_idx,] 

  #사용자 선호 공고 벡터들의 평균 벡터
  m = np.mean(my_vect,axis=0)

  total_idx = df.index

  #코사인 유사도 계산
  similarity = cosine_similarity(m,ftr_vect[total_idx])

  # array 내림차순으로 정렬한 후 인덱스 반환 [:,::-1] 모든행에 대해서 열을 내림차순으로!
  sorted_idx = similarity.argsort()[:,::-1]

  # 유사도가 큰 순으로 total_idx에서 재 정렬 
  # index로 넣으려면 1차원으로 reshape해주기!
  total_sorted_idx = total_idx[sorted_idx.reshape(-1,)]
  # 유사도 행렬값들을 유사도가 큰 순으로 재정렬
  total_sim_values = np.sort(similarity.reshape(-1,))[::-1]
  # 이렇게 되면 비교문서와 가장 유사한 순으로 '해당문서의index-유사도값' 으로 동일한 위치가 매핑된 두 개의 array 생성됨
  # 위에서 구한 array 그대로 데이터프레임의 각 칼럼으로 넣어 유사도 순으로 정렬된 공고 데이터프레임 생성
  total_sim_df = pd.DataFrame()
  total_sim_df['recruit_id'] = df.iloc[total_sorted_idx]['recruit_id']
  total_sim_df['similarity'] = total_sim_values
  total_sim_df = total_sim_df.drop(my_idx)
  print("recommend()")  # 상위 100개만 추출
  return total_sim_df[:100].values.tolist()

def db_insert(userId):
  cursor = db.cursor(pymysql.cursors.DictCursor)

  try:
      # recommendation list DELETE
      sql = """DELETE FROM `recommendation_list` WHERE user_id = %s"""
      cursor.execute(sql,userId)

      # recommendation list INSERT
      sql = """INSERT INTO `recommendation_list`(user_id, recruit_id, similarity) 
              VALUES (%s, %s, %s);"""
      cursor.executemany(sql,rec_result)
      db.commit()

  except pymysql.err.InternalError as e:
      code, msg = e.args

  finally:
      print("db inserted")
      cursor.close()

if __name__ == '__main__':
  db = db_connection()
  userId = sys.argv[1]
  print("userId : " + userId)
  favorite = db_select_favorite(userId)
  df = pd.read_csv("/home/ubuntu/workspace/recruitData.csv",index_col=0)
  my_data = []
  my_idx = []
  for i in favorite:
    my_data.append(df[df['recruit_id']==i].values[0])
    my_idx.append(df[df['recruit_id']==i].index[0])
  my_df = pd.DataFrame(my_data,columns=df.columns)
  print(my_df['recruit_id'].values)
  rec_result = recommend()
  for i in rec_result:
    i.insert(0,userId)
  db_insert(userId)
  db.close()

  

