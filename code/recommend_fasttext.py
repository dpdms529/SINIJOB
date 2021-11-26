import pandas as pd
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import pymysql
import sys
import fasttext
from keys import dbhost, dbpw


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


def db_select_keywords(userId):  # 선호 키워드 불러오기
    cursor = db.cursor(pymysql.cursors.DictCursor)

    try:
        sql = """select keyword from users where user_id = %s;"""
        cursor.execute(sql, userId)
        results = cursor.fetchall()
        result = results[0]['keyword'].split("|")
        print(result) # test log
    except pymysql.err.internalError as e:
        code, msg = e.args
    finally:
        cursor.close()
        print("db_select_keywords()")
        return result


def db_select_userId():  # 유저 id 불러오기
    cursor = db.cursor(pymysql.cursors.DictCursor)

    try:
        sql = """select user_id from users;"""
        cursor.execute(sql)
        results = cursor.fetchall()
        result = []
        for i in results:
            result.append(i['user_id'])
    except pymysql.err.internalError as e:
        code, msg = e.args
    finally:
        cursor.close()
        print("db_select_userId()")
        return result


def recommend():
    # 전체 공고 벡터
    fasttext_vect = np.load("/home/ubuntu/workspace/fasttextVect.npy")

    # fasttext 모델 불러오기
    fasttext_model = fasttext.load_model("/home/ubuntu/workspace/fasttext.bin")

    # 사용자 선호 키워드 벡터
    my_vect = []
    for i in my_keywords:
        my_vect.append(fasttext_model[i])

    # 사용자 선호 키워드 벡터들의 평균 벡터
    m = np.mean(my_vect, axis=0)
    fasttext_vect = list(fasttext_vect)
    fasttext_vect.append(m)
    user_idx = len(fasttext_vect) - 1

    # 코사인 유사도 계산
    cosine_similarities = cosine_similarity(fasttext_vect, fasttext_vect)

    # array 내림차순으로 정렬한 후 인덱스 반환
    similarity = list(enumerate(cosine_similarities[user_idx]))
    similarity = sorted(similarity, key=lambda x: x[1], reverse=True)
    del similarity[0]
    sorted_idx = [i[0] for i in similarity]
    sorted_val = [i[1] for i in similarity]

    # 이렇게 되면 비교문서와 가장 유사한 순으로 '해당문서의index-유사도값' 으로 동일한 위치가 매핑된 두 개의 array 생성됨
    # 위에서 구한 array 그대로 데이터프레임의 각 칼럼으로 넣어 유사도 순으로 정렬된 공고 데이터프레임 생성
    total_sim_df = pd.DataFrame()
    total_sim_df['recruit_id'] = df.iloc[sorted_idx]['recruit_id']
    total_sim_df['similarity'] = sorted_val
    total_sim_df = total_sim_df[total_sim_df['similarity'] > 0.1]
    print("recommend()")
    return total_sim_df.values.tolist()


def db_insert(userId):
    cursor = db.cursor(pymysql.cursors.DictCursor)

    try:
        # recommendation list DELETE
        sql = """DELETE FROM `recommendation_list_ft` WHERE user_id = %s"""
        cursor.execute(sql, userId)

        # recommendation list INSERT
        sql = """INSERT INTO `recommendation_list_ft`(user_id, recruit_id, similarity) 
              VALUES (%s, %s, %s);"""
        cursor.executemany(sql, rec_result)
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
        sql = """DELETE FROM `recommendation_list_ft` WHERE user_id = %s"""
        cursor.execute(sql, userId)
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
    df = pd.read_csv("/home/ubuntu/workspace/recruitData.csv", index_col=0)

    if userId == "a":
        userIds = db_select_userId()
        for id in userIds:
            my_keywords = db_select_keywords(id)
            if my_keywords[0]:
                rec_result = recommend()
                for i in rec_result:
                    i.insert(0, id)
                db_insert(id)
            else:
                db_delete(id)

    else:
        my_keywords = db_select_keywords(userId)
        if my_keywords[0]:
            rec_result = recommend()
            for i in rec_result:
                i.insert(0, userId)
            db_insert(userId)
        else:
            db_delete(userId)

    print("db close")
    db.close()

