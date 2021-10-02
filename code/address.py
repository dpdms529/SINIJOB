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

def getXY(street_code, main_no, additional_no, address, index, del_list):
    result = []

    url = 'https://dapi.kakao.com/v2/local/search/address.json?query=' + address
    header = {'Authorization': 'KakaoAK ' + kakaoRESTAPI}

    r = requests.get(url, headers=header)

    if r.status_code == 200:
        if r.json()["documents"]:
            result_address = r.json()["documents"][0]["address"]
            if not result_address:
                result_address = r.json()["documents"][0]["road_address"]
            result.append(result_address["x"])
            result.append(result_address["y"])
            result.append(street_code)
            result.append(main_no)
            result.append(additional_no)
        else:   # 응답은 정상적이나, 응답 값이 없는 경우 -> 제외
            del_list.append(index)
    else:
        sys.exit("kakao API ERROR[" + str(r.status_code) + "]")

    return result

if __name__ == '__main__':
   db = db_connection

