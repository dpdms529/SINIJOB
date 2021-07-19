import re
import requests, bs4
import pandas as pd
from urllib.parse import urlencode, quote_plus, unquote
import pymysql

from keys import worknetKey

# api url and key
url = 'http://openapi.work.go.kr/opi/opi/opia/wantedApi.do'
authKey = unquote(worknetKey)


def db_connection():
    db = pymysql.connect(
        user='hanium',
        passwd='hanium235!',
        host='haniumdb.caka4pfurzmq.ap-northeast-2.rds.amazonaws.com',
        port=3306,
        db='hanium',
        charset='utf8'
    )
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
            #, quote_plus('regDate'): 'D-0' # 첫 수집 이후 반복 시 주석 제거 후 사용(오늘 업로드 된 데이터만 가져오도록 함)
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
                columnList.append(eachColumn)
        rowList.append(columnList)
        columnList = []  # 다음 row 값을 넣기 위해 비워준다.


def check_duplicates(flag):     # 중복 데이터의 유무 확인, 새로운 데이터만 가져오도록 범위 설정
    cursor = db.cursor()
    # DB의 데이터 중 가장 최근 날짜에 업로드 된 공고들의 id를 가져온다.
    try:
        sql = """select recruit_id from recruit 
            where regDt = (select MAX(regDt) from recruit)"""
        cursor.execute(sql)
        result = cursor.fetchall()
        for recent_id in result:
            # DB의 공고 id와 api에서 새로 받아온 공고 id를 비교. 같은 id를 발견하면 이후의 데이터는 중복 데이터로 간주.
            # 해당 id 이후의 공고 목록을 list에서 삭제하고, 목록 가져오기를 멈춘다.
            for i in range(0, len(rowList)):
                if recent_id[0] == rowList[i][0]:
                    for j in range(i, len(rowList)):
                        del rowList[j]
                    flag[0] = True
                    break
            if flag[0]:
                break

    except pymysql.err.InternalError as e:
        code, msg = e.args

    finally:
        cursor.close()


def recruit_id():   # 공고 id만 추출 -> 각 공고의 채용 상세 데이터를 불러오기 위함
    rowsLen = len(rowList)
    for i in range(0, rowsLen):
        wantedAuthNo.append(rowList[i][0])


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
        rows.append(xmlobj_detail.findAll('wantedInfo') + xmlobj_detail.findAll('empchargeInfo'))

    columnList = []

    delList = []

    for i in range(0, rowsLen):
        row = rows[i]
        if not row:     # 종종 데이터를 받아오지 못하는 경우 존재함 -> 제외
            delList.append(i)
            continue
        columns = row[0].find_all() + row[1].find_all()
        columnsLen = len(columns)
        for j in range(0, columnsLen):
            name = columns[j].name
            eachColumn = columns[j].text
            if name in [
                'wantedTitle', 'jobCont', 'collectPsncnt', 'compAbl', 'pfCond', 'etcPfCond',
                'selMthd', 'rcptMthd', 'submitDoc', 'etcHopeCont', 'workdayWorkhrCont',
                'fourIns', 'retirepay', 'etcWelfare', 'disableCvntl', 'minEdubgIcd',
                'salTpCd', 'contactTelno'
            ]:
                columnList.append(eachColumn)
            elif name == 'enterTpNm':
                enterTpNm.append(eachColumn)
            elif name == 'certificate':
                certificate.append(eachColumn)
            elif name == 'empTpCd':
                empTpCd.append(eachColumn)
            elif name == 'enterTpCd':
                enterTpCd.append(eachColumn)
        rowList_detail.append(columnList)
        columnList = []  # 다음 row의 값을 넣기 위해 비워준다.

    # 무효 데이터 삭제
    if delList:
        count = 0
        for i in delList:
            del rowList[i-count]
            del rows[i-count]
            count += 1


def find_id(certificateTxt, certifiInfo): # 자격증 ID를 찾는 함수
    for i in range(len(selectCertificate)):
        if selectCertificate[i]['certificate_name'] == certificateTxt[-1]:
            certifiInfo.append(selectCertificate[i]['certificate_id']) # 자격증 ID
            return


def processing():   # 데이터 전처리
    rowsLen = len(rowList)
    for i in range(0, rowsLen):
        # 건물 본번, 부번
        tmp = re.findall(r'[로길] (.+)', rowList[i][8])
        if tmp:
            if '-' in tmp[0]:  # '-'가 있으면
                rowList[i].append(tmp[0].split('-')[0])
                rowList[i].append(tmp[0].split('-')[1])
            elif ' ' in tmp[0]:  # 가끔 '22 22', '26 26' 이런식이 있음
                rowList[i].append(tmp[0].split(' ')[0])
                rowList[i].append('0')  # 부번이 없으므로 0
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
            rowList_detail[i].append('1')
            if '기타' in certificate[i]:  # 기타 조건이 있으면
                etc = re.findall(r'기타: (.+).', certificate[i])
                if rowList_detail[i][5] == '':  # 기타 우대 조건이 비어 있으면
                    rowList_detail[i][5] = rowList_detail[i][5] + etc[0]
                else:  # 기타 우대 조건에 문자열이 들어 있으면
                    rowList_detail[i][5] = rowList_detail[i][5] + '\n' + etc[0]

                tmp = re.findall(r'(.+)\(기타:', certificate[i])
                if tmp:  # 기타 앞에 정보가 있으면
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
            else:  # 기타 조건이 없으면
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
                                        certificate_required, career_required, career_min, enrollment_code) 
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 
                        %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);"""
        # cursor.executemany(sql, recruit)
        # db.commit()

        # certificate INSERT
        sql = """INSERT INTO `certificate`(certificate_no, recruit_id, certificate_id) 
                VALUES (%s, %s, %s);"""
        # cursor.executemany(sql, certificateList)
        # db.commit()

    except pymysql.err.InternalError as e:
        code, msg = e.args

    finally:
        cursor.close()
        db.close()


def db_select_certificate():
    global selectCertificate
    cursor = db.cursor(pymysql.cursors.DictCursor)

    sql = "select * from certificate"
    cursor.execute(sql)
    selectCertificate = list(cursor.fetchall())

    cursor.close()
    db.commit()


if __name__ == '__main__':
    # init lists
    rowList = []
    wantedAuthNo = []
    rowList_detail = []
    certificateList = []
    recruit = []
    enterTpNm = []
    certificate = []
    empTpCd = []
    enterTpCd = []
    selectCertificate = []  # db에서 읽은 자격증을 저장하는 리스트

    db = db_connection()
    flag = [False]
    for i in range(0, int(check_total() / 100) + 1):
        recruit_list(i+1)
        # check_duplicates(flag) # 첫 수집 이후 반복 시 주석 제거 후 사용(새로 업데이트 된 데이터만 받아오도록 함)
        if flag[0]:
            break
    recruit_id()
    recruit_detail()
    db_select_certificate()  # db에서 자격증 테이블 읽어오기
    processing()
    # 리스트 합치기
    rowsLen = len(rowList)
    for i in range(0, rowsLen):
        recruit.append(rowList[i] + rowList_detail[i])
    db_insert()

