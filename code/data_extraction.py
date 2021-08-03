import sys
import html
import re
import requests, bs4
import pandas as pd
from urllib.parse import urlencode, quote_plus, unquote
import pymysql

from keys import worknetKey, kakaoRESTAPI, dbpw, dbhost

# api url and key
url = 'http://openapi.work.go.kr/opi/opi/opia/wantedApi.do'
authKey = unquote(worknetKey)


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


def check_total():  # 가져올 데이터의 개수 확인
    queryParams = '?' + urlencode(
        {
            quote_plus('authKey') : authKey,
            quote_plus('callTp'): 'L',
            quote_plus('returnType'): 'XML',
            quote_plus('startPage'): '1',
            quote_plus('display'): '1',
            quote_plus('pfPreferential'): 'B'   # 시니어 공고: B
            # , quote_plus('regDate'): 'D-0' # 첫 수집 이후 반복 시 주석 제거 후 사용(오늘 업로드 된 데이터만 가져오도록 함)
        }
    )
    response = requests.get(url + queryParams).text.encode('utf-8')
    xmlobj = bs4.BeautifulSoup(response, 'lxml-xml')
    total = xmlobj.find('total')
    print("total data from api: " + total.text)
    return int(total.text)


def recruit_list(pageNum):  # 공고 목록 불러와 리스트에 저장
    queryParams = '?' + urlencode(
        {
            quote_plus('authKey') : authKey,
            quote_plus('callTp'): 'L',
            quote_plus('returnType'): 'XML',
            quote_plus('startPage'): str(pageNum),
            quote_plus('display'): '100',
            quote_plus('pfPreferential'): 'B'   # 시니어 공고: B
         }
    )

    response = requests.get(url + queryParams).text.encode('utf-8')
    xmlobj = bs4.BeautifulSoup(response, 'lxml-xml')
    rows = xmlobj.findAll('wanted')

    columnList = []

    rowsLen = len(rows)
    for i in range(0, rowsLen):
        columns = rows[i].find_all()
        columnsLen = len(columns)
        for j in range(0, columnsLen):
            name = columns[j].name
            # 컬럼 값은 모든 행의 값을 저장
            if name in [
                'wantedAuthNo', 'company', 'sal', 'holidayTpNm', 'regDt', 'closeDt',
                'wantedMobileInfoUrl', 'strtnmCd', 'basicAddr', 'detailAddr', 'jobsCd'
            ]:
                eachColumn = columns[j].text
                # eachColumn_decoded = html.unescape(eachColumn)  # html 디코딩
                # columnList.append(eachColumn_decoded)
                columnList.append(eachColumn)
        rowList.append(columnList)
        columnList = []  # 다음 row 값을 넣기 위해 비워준다.


def check_duplicates():     # 중복 데이터의 유무 확인, 새로운 데이터만 가져오도록 범위 설정, 오래된 데이터는 id만 저장
    cursor = db.cursor()
    # DB의 데이터 중 가장 최근 날짜에 업로드 된 공고들의 id를 가져온다.
    try:
        sql = """select recruit_id from recruit 
            where register_date = (select MAX(register_date) from recruit)"""
        cursor.execute(sql)
        results = cursor.fetchall()
        recent_id = []
        for result in results:
            recent_id.append(result[0])
        for i in range(0, len(rowList)):
            # DB의 공고 id와 api에서 새로 받아온 공고 id를 비교. 같은 id를 발견하면 이후의 데이터는 중복 데이터로 간주.
            # 해당 id 이후의 공고 목록(이미 저장된 데이터)은 id만 저장하고 rowList에 저장하지 않는다.
            if rowList[i][0] in recent_id:
                count = 0
                for j in range(i, len(rowList)):
                    oldWantedAuthNo.append(rowList[j-count][0])
                    del rowList[j-count]
                    count += 1
                print("old data: " + str(len(oldWantedAuthNo)))  # log
                break

    except pymysql.err.InternalError as e:
        code, msg = e.args

    finally:
        print("check_duplicates() done")
        cursor.close()


def recruit_id():   # 공고 id만 추출 -> 각 공고의 채용 상세 데이터를 불러오기 위함
    rowsLen = len(rowList)
    for i in range(0, rowsLen):
        wantedAuthNo.append(rowList[i][0])
    print("new data: " + str(len(wantedAuthNo)))    # log


def recruit_detail():   # 채용 상세 데이터 불러와 리스트에 저장
    rows = []
    rowsLen = len(rowList)
    for i in range(0, rowsLen):
        queryParams_detail = '?' + urlencode(
            {
                quote_plus('authKey') : authKey,
                quote_plus('callTp'): 'D',
                quote_plus('returnType'): 'XML',
                quote_plus('wantedAuthNo'): wantedAuthNo[i],
                quote_plus('infoSvc'): 'VALIDATION'
            }
        )
        response_detail = requests.get(url + queryParams_detail).text.encode('utf-8')
        xmlobj_detail = bs4.BeautifulSoup(response_detail, 'lxml-xml')
        rows.append(xmlobj_detail.findAll('wantedInfo')
                    + xmlobj_detail.findAll('empchargeInfo')
                    + xmlobj_detail.findAll('corpInfo'))

    columnList = []
    columnNames = (
                'wantedTitle', 'jobCont', 'collectPsncnt', 'compAbl', 'pfCond', 'etcPfCond',
                'selMthd', 'rcptMthd', 'submitDoc', 'etcHopeCont', 'workdayWorkhrCont',
                'fourIns', 'retirepay', 'etcWelfare', 'disableCvntl', 'minEdubgIcd',
                'salTpCd', 'contactTelno', 'reperNm', 'totPsncnt',
                'yrSalesAmt', 'indTpCdNm', 'corpAddr'
            )
    delList = []

    for i in range(0, rowsLen):
        nameList = []
        row = rows[i]
        if not row:     # 종종 데이터를 받아오지 못하는 경우 존재함 -> 제외
            delList.append(i)
            continue
        columns = row[0].find_all() + row[1].find_all() + row[2].find_all()
        columnsLen = len(columns)
        attachCount = 0
        for j in range(0, columnsLen):
            name = columns[j].name
            eachColumn = columns[j].text
            if name in columnNames:
                if name in nameList:   # 중복된 태그 무시(corpInfo 태그에 empchargeInfo 데이터가 들어가 있는 경우)
                    continue
                else:
                    # eachColumn_decoded = html.unescape(eachColumn)  # html 디코딩
                    # columnList.append(eachColumn_decoded)
                    columnList.append(eachColumn)
                    nameList.append(name)
            elif name == 'enterTpNm':
                enterTpNm.append(eachColumn)
            elif name == 'certificate':
                certificate.append(eachColumn)
            elif name == 'empTpCd':
                empTpCd.append(eachColumn)
            elif name == 'enterTpCd':
                enterTpCd.append(eachColumn)
            elif name == 'corpAttachList':
                attachedFiles = columns[j].find_all()
                attachedLen = len(attachedFiles)
                for k in range(0, attachedLen):
                    tempList = []
                    eachFile = attachedFiles[k].text
                    tempList.append(rowList[i][0])
                    tempList.append(1 + attachCount)
                    tempList.append(eachFile)
                    corpAttachList.append(tempList)
                    attachCount += 1
        for index, name in enumerate(columnNames):  # 태그 자체가 없는 경우, 해당 자리에 ''값을 삽입한다.
            if name not in nameList:
                columnList.insert(index, '')

        rowList_detail.append(columnList)
        columnList = []  # 다음 row의 값을 넣기 위해 비워준다.

    # 무효 데이터 삭제
    if delList:
        count = 0
        for i in delList:
            del rowList[i-count]
            del rows[i-count]
            count += 1

    print("recruit_detail() done")


def find_id(certificateTxt, certifiInfo): # 자격증 ID를 찾는 함수
    for i in range(len(selectCertificate)):
        if selectCertificate[i]['certificate_name'] == certificateTxt[-1]:
            certifiInfo.append(selectCertificate[i]['certificate_id']) # 자격증 ID
            return


def processing():   # 데이터 전처리
    delList = []
    rowsLen = len(rowList)
    for i in range(0, rowsLen):
        print("processing%s, id:"%i, rowList[i][0])
        # 건물 본번, 부번
        tmp = re.findall(r'[로길] (.+)', rowList[i][8])
        if tmp:
            if '-' in tmp[0]:  # '-'가 있으면
                rowList[i].append(tmp[0].split('-')[0])
                rowList[i].append(tmp[0].split('-')[1])
            elif ' ' in tmp[0]:  # 가끔 '22 22', '26 26' 이런식이 있음
                rowList[i].append(tmp[0].split(' ')[0])
                rowList[i].append('0')  # 부번이 없으므로 0
                rowList[i][8] = rowList[i][8][:(len(tmp[0].split(' ')[0]) * (-1) - 1)]  # basicAddr의 값을 바꿔줌(중복 제거)
            else:  # '-'가 없으면
                rowList[i].append(tmp[0])
                rowList[i].append('0')  # 부번이 없으므로 0
        else:  # 건물 번호가 없으면
            rowList[i].append('0')  # 본번이 없으므로 0
            rowList[i].append('0')  # 부번이 없으므로 0

        # 자격증
        certificateTxt = []  # 자격증 이름을 담아두는 임시 리스트
        certifiInfoList = []  # 자격증 정보를 저장하는 임시 리스트

        if certificate[i]:  # 요구 자격증이 있으면
            if '기타' in certificate[i]:  # 기타 조건이 있으면
                etc = re.findall(r'기타: (.+).', certificate[i])
                if rowList_detail[i][5] == '':  # 기타 우대 조건이 비어 있으면
                    rowList_detail[i][5] = rowList_detail[i][5] + etc[0]
                else:  # 기타 우대 조건에 문자열이 들어 있으면
                    rowList_detail[i][5] = rowList_detail[i][5] + '\n' + etc[0]

                tmp = re.findall(r'(.+)\(기타:', certificate[i])
                if tmp:  # 기타 앞에 정보가 있으면
                    rowList_detail[i].append('1')
                    if ',' in tmp[0]:  # 자격증이 여러개이면
                        tmp = tmp[0].split(',')
                        for j in range(len(tmp)):
                            certificateTxt.append(tmp[j])
                            certifiInfoList.append(j)  # 자격증 순번
                            certifiInfoList.append(rowList[i][0])  # 공고 ID
                            find_id(certificateTxt, certifiInfoList)  # 자격증 ID

                            certificateList.append(certifiInfoList)
                            certifiInfoList = []
                    else:
                        certificateTxt.append(tmp[0])
                        certifiInfoList.append(0)  # 자격증 순번
                        certifiInfoList.append(rowList[i][0])  # 공고 ID
                        find_id(certificateTxt, certifiInfoList)  # 자격증 ID

                        certificateList.append(certifiInfoList)
                else:   # 기타 앞에 정보가 없으면
                    rowList_detail[i].append('0')
            else:  # 기타 조건이 없으면
                rowList_detail[i].append('1')
                tmp = re.findall(r'(.+)', certificate[i])
                if ',' in tmp[0]:  # 자격증이 여러개이면
                    tmp = tmp[0].split(',')
                    for j in range(len(tmp)):
                        certificateTxt.append(tmp[j])
                        certifiInfoList.append(j)  # 자격증 순번
                        certifiInfoList.append(rowList[i][0])  # 공고 ID
                        find_id(certificateTxt, certifiInfoList)  # 자격증 ID

                        certificateList.append(certifiInfoList)
                        certifiInfoList = []
                else:
                    certificateTxt.append(tmp[0])
                    certifiInfoList.append(0)  # 자격증 순번
                    certifiInfoList.append(rowList[i][0])  # 공고 ID
                    find_id(certificateTxt, certifiInfoList)  # 자격증 ID

                    certificateList.append(certifiInfoList)
        else:  # 요구 자격증이 없으면
            rowList_detail[i].append('0')

        # 경력
        if enterTpCd[i] == 'N' or enterTpCd[i] == 'Z':
            rowList_detail[i].append('0')  # 신입, 관계없음
            rowList_detail[i].append('')
        elif enterTpCd[i] == 'E':
            tmp = re.findall(r'.+(..)', enterTpNm[i])
            if tmp[0] == '우대':
                rowList_detail[i].append('1')  # 우대
            elif tmp[0] == '필수':
                rowList_detail[i].append('2')  # 필수

            if '년' in enterTpNm[i]:
                tmp = re.findall(r'([0-9]+)년', enterTpNm[i])
                if '개월' in enterTpNm[i]:
                    tmp2 = re.findall(r'([0-9]+)개월', enterTpNm[i])
                    rowList_detail[i].append(int(tmp[0]) * 12 + int(tmp2[0]))  # x년y개월
                else:
                    rowList_detail[i].append(int(tmp[0]) * 12)  # x년
            elif '개월' in enterTpNm[i]:
                tmp = re.findall(r'([0-9]+)개월', enterTpNm[i])
                rowList_detail[i].append(int(tmp[0]))  # x개월

        # 고용 형태
        if empTpCd[i] == '10' or empTpCd[i] == '11':
            rowList_detail[i].append('F')
        elif empTpCd[i] == '20' or empTpCd[i] == '21':
            rowList_detail[i].append('P')

        # x,y 구하기
        pk = (rowList[i][7], rowList[i][11], rowList[i][12])
        checked = db_checknull(pk)
        if checked == 1:   # 해당 주소의 x, y 값 존재 여부 확인 - 없으면 구해서 삽입
            result_xy = getXY(pk[0], pk[1], pk[2], rowList[i][8], i, delList)
            if result_xy:   # 응답 값이 없을 경우 result_xy는 비어있음. 이 경우를 제외
                xy.append(result_xy)
        elif checked == 2:
            delList.append(i)   # 종종 업데이트 되지 않은 도로명 주소 존재 -> 제외

    # 무효 데이터 삭제
    if delList:
        printlen = len(delList)
        print("delete " + str(printlen) + " data: no x, y")  # log
        count = 0
        for i in delList:
            certifi_count = 0
            for j in range(0, len(certificateList)):     # 삭제 공고의 자격증 데이터 또한 삭제
                if rowList[i-count][0] == certificateList[j-certifi_count][1]:
                    del certificateList[j-certifi_count]
                    certifi_count += 1
            print(rowList[i-count][0])  # log
            del rowList[i-count]
            del rowList_detail[i-count]
            count += 1


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


def db_checknull(pk):
    cursor = db.cursor(pymysql.cursors.DictCursor)

    try:
        # address(x,y) SELECT
        sql = """SELECT x, y from `address`
                WHERE street_code = %s and main_no = %s and additional_no = %s;"""
        cursor.execute(sql, pk)
        results = cursor.fetchall()
        if not results:
            return 2
        elif not results[0]['x'] or not results[0]['y']:
            return 1
        else:
            return 0

    except pymysql.err.InternalError as e:
        code, msg = e.args

    finally:
        cursor.close()


def db_check_constraint():
    # job_code 존재여부 확인
    cursor = db.cursor(pymysql.cursors.DictCursor)

    try:
        # job SELECT
        sql = """SELECT category_code FROM `job_category`
                WHERE LENGTH(category_code) > 4;"""
        cursor.execute(sql)
        results = cursor.fetchall()
        result = []
        delList = []
        for tmp in results:
            result.append(tmp['category_code'])
        for i in range(len(rowList)):
            if rowList[i][10] not in result:
                delList.append(i)

        # 무효 데이터 삭제
        if delList:
            printdel = len(delList)  # log
            print("delete " + str(printdel) + " data: no job_code")  # log
            count = 0
            for i in delList:
                certifi_count = 0
                for j in range(0, len(certificateList)):  # 삭제 공고의 자격증 데이터 또한 삭제
                    if rowList[i - count][0] == certificateList[j - certifi_count][1]:
                        del certificateList[j - certifi_count]
                        certifi_count += 1
                print(rowList[i - count][0])  # log
                del rowList[i - count]
                del rowList_detail[i - count]
                count += 1

    except pymysql.err.InternalError as e:
        code, msg = e.args

    finally:
        cursor.close()


def db_insert():
    cursor = db.cursor(pymysql.cursors.DictCursor)

    try:
        # recruit INSERT
        sql = """INSERT INTO `recruit`(recruit_id, organization, salary, work_day, register_date, 
                                        close_date, url, street_code, basic_address, 
                                        detail_address, job_code, main_no, additional_no, 
                                        title, content, num_of_people, computer_able, preference_cond, 
                                        etc_preference_cond, screening_process, register_method, submission_doc, 
                                        etc_info, work_time, four_insurence, retire_pay, etc_welfare, 
                                        disable_conv, min_education_code, salary_type_code, contact, 
                                        representative, total_worker, sales_amount, industry, corp_address,
                                        certificate_required, career_required, career_min, 
                                        enrollment_code, update_dt) 
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 
                        %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, now());"""
        cursor.executemany(sql, recruit)
        db.commit()

        # certificate INSERT
        sql = """INSERT INTO `recruit_certificate`(certificate_no, recruit_id, certificate_id) 
                VALUES (%s, %s, %s);"""
        cursor.executemany(sql, certificateList)
        db.commit()

        # recruit_files INSERT
        sql = """INSERT INTO `recruit_files`(recruit_id, file_no, file_url) 
                        VALUES (%s, %s, %s);"""
        cursor.executemany(sql, corpAttachList)
        db.commit()

        # address(x,y) UPDATE
        sql = """UPDATE `address`
                SET x = %s, y = %s
                WHERE street_code = %s and main_no = %s and additional_no = %s;"""
        cursor.executemany(sql, xy)
        db.commit()

    except pymysql.err.InternalError as e:
        code, msg = e.args

    finally:
        print("db inserted")
        cursor.close()


def db_select_certificate():
    global selectCertificate
    cursor = db.cursor(pymysql.cursors.DictCursor)

    sql = "select * from certificate"
    cursor.execute(sql)
    selectCertificate = list(cursor.fetchall())

    cursor.close()
    db.commit()


def db_check_deleted():
    cursor = db.cursor(pymysql.cursors.DictCursor)

    try:
        # 기존 저장되어있던 데이터 중 사라진 공고 id 확인
        sql = """select recruit_id from recruit
                where deleted = '0';"""
        cursor.execute(sql)
        results = cursor.fetchall()
        result = []
        for tmp in results:
            result.append(tmp['recruit_id'])
        delList = tuple(set(result)-set(oldWantedAuthNo))
        print("update " + str(len(delList)) + " data from recruit(deleted = 1) which has id below...")  # log
        print(delList)  # log

        # API에서 지워진 공고에 삭제 표시 업데이트
        sql = f"""UPDATE `recruit`
                SET deleted = '1'
                WHERE recruit_id in {delList};"""
        cursor.execute(sql)
        db.commit()

    except pymysql.err.InternalError as e:
        code, msg = e.args

    finally:
        print("deleted recruits checked")
        cursor.close()


if __name__ == '__main__':
    # init lists
    rowList = []
    oldWantedAuthNo = []
    wantedAuthNo = []
    rowList_detail = []
    certificateList = []
    recruit = []
    enterTpNm = []
    certificate = []
    empTpCd = []
    enterTpCd = []
    corpAttachList = []
    selectCertificate = []  # db에서 읽은 자격증을 저장하는 리스트
    xy = []

    db = db_connection()
    for i in range(0, int(check_total() / 100) + 1):
        recruit_list(i+1)
    print("recruit_list() done")
    check_duplicates()  # 새로 업데이트 된 데이터만 받아오도록 함
    recruit_id()
    recruit_detail()
    db_select_certificate()  # db에서 자격증 테이블 읽어오기
    processing()
    db_check_constraint()
    # 리스트 합치기
    rowsLen = len(rowList)
    for i in range(0, rowsLen):
        recruit.append(rowList[i] + rowList_detail[i])
    db_check_deleted()
    db_insert()
    db.close()
    print("db closed")

