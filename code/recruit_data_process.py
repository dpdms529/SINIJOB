import pymysql
import pandas as pd
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

    db.close()