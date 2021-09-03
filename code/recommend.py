import pandas as pd
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import pymysql
import sys
from scipy.sparse import csr_matrix
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

def db_select_userId(): # 선호 공고 데이터 불러오기
    cursor = db.cursor(pymysql.cursors.DictCursor)
    
    try:
        sql = """select user_id from temp_favorite group by user_id;"""
        cursor.execute(sql)
        results = cursor.fetchall()
        result = []
        for i in results:
            result.append(i['user_id'])
    except pymysql.err.internalError as e:
        code,msg = e.args
    finally:
        cursor.close()
        print("db_select_userId()")
        return result

def recommend(): 
  #전체 공고 벡터
  ftr_vect = np.load("/home/ubuntu/workspace/ftrVect.npy")
  ftr_vect = csr_matrix(ftr_vect) 

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
  return total_sim_df.values.tolist()

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

def db_delete(userId):
  cursor = db.cursor(pymysql.cursors.DictCursor)

  try:
      # recommendation list DELETE
      sql = """DELETE FROM `recommendation_list` WHERE user_id = %s"""
      cursor.execute(sql,userId)
      db.commit()

  except pymysql.err.InternalError as e:
      code, msg = e.args

  finally:
      print("db deleted")
      cursor.close()

if __name__ == '__main__':
  db = db_connection()
  userId = sys.argv[1]
  print("userId : " + userId)
  df = pd.read_csv("/home/ubuntu/workspace/recruitData.csv",index_col=0)

  if userId == "a":
    userIds = db_select_userId()
    for id in userIds:
      favorite = db_select_favorite(id)
      my_data = []
      my_idx = []
      for i in favorite:
        my_data.append(df[df['recruit_id']==i].values[0])
        my_idx.append(df[df['recruit_id']==i].index[0])
      my_df = pd.DataFrame(my_data,columns=df.columns)
      print(my_df['recruit_id'].values)
      if not my_df.empty:
        rec_result = recommend()
        for i in rec_result:
          i.insert(0,id)
        db_insert(id)
      else:
        db_delete(id)

  else:
    favorite = db_select_favorite(userId)
    my_data = []
    my_idx = []
    for i in favorite:
      my_data.append(df[df['recruit_id']==i].values[0])
      my_idx.append(df[df['recruit_id']==i].index[0])
    my_df = pd.DataFrame(my_data,columns=df.columns)
    print(my_df['recruit_id'].values)
    if not my_df.empty:
      rec_result = recommend()
      for i in rec_result:
        i.insert(0,userId)
      db_insert(userId)
    else:
      db_delete(userId)

  print("db close")
  db.close()

  

