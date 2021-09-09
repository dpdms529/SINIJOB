import pymysql
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from konlpy.tag import Okt
import numpy as np
from keys import dbhost, dbpw
import subprocess

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

def db_select_recruit(): # 공고 데이터 불러오기
    cursor = db.cursor(pymysql.cursors.DictCursor)
    
    try:
        sql = """select recruit_id from recruit where deleted = 0;"""
        cursor.execute(sql)
        results = cursor.fetchall()
        result = []
        for i in results:
            result.append(i['recruit_id'])
    except pymysql.err.internalError as e:
        code,msg = e.args
    finally:
        cursor.close()
        print("db_select_recruit()")
        return result

def db_select_detail(): #공고 상세정보 뽑아오기(공고 id, 공고 제목, 내용, 직종 이름, 요구 자격증)
    cursor = db.cursor(pymysql.cursors.DictCursor)
    data = []
    
    try:
        for result_list in result_devided:
            placeholders = ", ".join(["%s"] * len(result_list))
            sql = """select recruit.recruit_id, recruit.title, recruit.content, job_category.category_name, certificate.certificate_name from recruit inner join job_category
    on recruit.job_code=job_category.category_code left outer join recruit_certificate
    on recruit.recruit_id=recruit_certificate.recruit_id left outer join certificate
    on recruit_certificate.certificate_id=certificate.certificate_id
    where recruit.recruit_id in ({})""".format(placeholders)
            cursor.execute(sql, result_list)
            results = list(cursor)
            for j in results:
                data.append(j)
    except pymysql.err.InternalError as e:
        code,msg = e.args
    finally:
        cursor.close()
        print("db_select_detail()")
        return data

def add_certificate(): # 자격증 여러 개인 데이터 한 행으로 합치기
    for index1, row1 in certificate_duplicate.iterrows():
        for index2, row2 in recruit_data.iterrows():
            if(row1['recruit_id'] == row2['recruit_id']):
                certificate = row2['certificate_name'] + ', ' + row1['certificate_name']
                recruit_data.loc[index2, 'certificate_name'] = certificate
    print("add_certificate()")

def vectorize():
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
    if df['certificate_name'][i] == None :
      df['certificate_name'][i] = ""
    texts.append(df['title'][i] + "\n" + df['content'][i] + "\n" + df['job_name'][i] + "\n" + df['certificate_name'][i])
  texts[0]

  #문서 정규화
  content = [okt.normalize(texts[i]) for i in range(df.index.size)]  

  #전체 공고 벡터
  ftr_vect = tfidfv.fit_transform(content)   
  ftr_arr = ftr_vect.toarray()
  np.save("/home/ubuntu/workspace/ftrVect.npy", ftr_arr)


if __name__ == '__main__' :
    db = db_connection()
    result = db_select_recruit()
    
    result_devided = []
    length = len(result)
    devided = length // 1000 + 1
    start = 0
    end = 999
    for i in range(0, devided):
        temp = result[start:end]
        result_devided.append(temp)
        start = end
        end += 999
    print("result_devided")

    data = db_select_detail()

    recruit_data = pd.DataFrame(data)
    recruit_data.rename(columns={"category_name":"job_name"}, inplace=True)

    certificate_duplicate = recruit_data[recruit_data.duplicated('recruit_id')] # 공고 id 중복(자격증 여러 개)된 애들만 추출(처음 데이터 제외)

    recruit_data = recruit_data.drop(certificate_duplicate.index.tolist()) # 공고 id 중복 행(certificate_duplicate) 제거

    add_certificate()

    recruit_data_final = recruit_data.reset_index(drop=True)

    recruit_data_final.to_csv("/home/ubuntu/workspace/recruitData.csv", mode="w")
    print("to_csv")

    df = recruit_data_final
    vectorize()

    db.close()

    subprocess.call(["python3","/home/ubuntu/workspace/recommend.py","a"])
