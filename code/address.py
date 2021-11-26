import sys
import requests
from urllib.parse import urlencode, quote_plus, unquote
import pymysql

from keys import kakaoRESTAPI, dbpw, dbhost

def db_connection():
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

def getXY(street_code, main_no, additional_no, address):
    result = []

    url = 'https://dapi.kakao.com/v2/local/search/address.json?query=' + address
    header = {'Authorization': 'KakaoAK ' + kakaoRESTAPI}

    r = requests.get(url, headers=header)

    if r.status_code == 200:
        if r.json()["documents"]:
            result_address = r.json()["documents"][0]["road_address"]
            if result_address["main_building_no"]!="":
                main_no = result_address["main_building_no"]
                if result_address["sub_building_no"]!="":
                    additional_no = result_address["sub_building_no"]

            result.append(result_address["x"])
            result.append(result_address["y"])
            result.append(street_code)
            result.append(main_no)
            result.append(additional_no)

    else:
        sys.exit("kakao API ERROR[" + str(r.status_code) + "]")

    return result


def db_update():
    cursor = db.cursor(pymysql.cursors.DictCursor)

    try:
        # address(x,y) UPDATE
        sql = """UPDATE `address`
                SET x = %s, y = %s
                WHERE street_code = %s and main_no = %s and additional_no = %s;"""
        cursor.execute(sql, xy)
        db.commit()

    except pymysql.err.InternalError as e:
        code, msg = e.args

    finally:
        print("db updated")
        cursor.close()

if __name__ == '__main__':
    street_code = sys.argv[1]
    address = sys.argv[2]
    main_no = 0
    additional_no = 0

    xy = []
    
    db = db_connection()
    
    xy = getXY(street_code, main_no, additional_no, address)
    
    if len(xy)!=0:
        db_update()
        for i in xy:
            print(i)
    else:
        print("no result")

    db.close()
    print("db closed")
    


