package org.techtown.hanieum;

import android.os.AsyncTask;

import org.techtown.hanieum.db.dao.BdongDao;
import org.techtown.hanieum.db.dao.CertificateDao;
import org.techtown.hanieum.db.dao.CoverLetterDao;
import org.techtown.hanieum.db.dao.CvInfoDao;
import org.techtown.hanieum.db.dao.JobCategoryDao;
import org.techtown.hanieum.db.dao.RecruitCertificateDao;
import org.techtown.hanieum.db.dao.RecruitDao;
import org.techtown.hanieum.db.entity.Bdong;
import org.techtown.hanieum.db.entity.Certificate;
import org.techtown.hanieum.db.entity.CoverLetter;
import org.techtown.hanieum.db.entity.CvInfo;
import org.techtown.hanieum.db.entity.JobCategory;
import org.techtown.hanieum.db.entity.Recruit;
import org.techtown.hanieum.db.entity.RecruitCertificate;

import java.util.HashMap;
import java.util.List;

public class Query {
    //Recruit
    public static class RecruitGetListAsyncTask extends AsyncTask<String, Void, List<Recruit>> {
        private RecruitDao mRecruitDao;

        public RecruitGetListAsyncTask(RecruitDao recruitDao) {
            this.mRecruitDao = recruitDao;
        }

        @Override
        protected List<Recruit> doInBackground(String... strings) {
            return mRecruitDao.getList(strings[0]);
        }
    }

    public static class RecruitLastUpdateAsyncTask extends AsyncTask<Void, Void, List<String>> {
        private RecruitDao mRecruitDao;

        public RecruitLastUpdateAsyncTask(RecruitDao recruitDao) {
            this.mRecruitDao = recruitDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return mRecruitDao.getLastUpdated();
        }

    }

    // 메인스레드에서 데이터베이스에 접근할 수 없으므로 AsyncTask 사용 - INSERT
    public static class RecruitInsertAsyncTask extends AsyncTask<Recruit, Void, Void> {
        private RecruitDao mRecruitDao;

        public RecruitInsertAsyncTask(RecruitDao recruitDao) {
            this.mRecruitDao = recruitDao;
        }

        @Override // 백그라운드작업(메인스레드 X)
        protected Void doInBackground(Recruit... recruits) {
            mRecruitDao.insertNewRecruit(recruits[0]);
            return null;
        }
    }

    // 메인스레드에서 데이터베이스에 접근할 수 없으므로 AsyncTask 사용 - DELETE
    public static class RecruitDeleteAsyncTask extends AsyncTask<String, Void, Void> {
        private RecruitDao mRecruitDao;

        public RecruitDeleteAsyncTask(RecruitDao recruitDao) {
            this.mRecruitDao = recruitDao;
        }

        @Override // 백그라운드작업(메인스레드 X)
        protected Void doInBackground(String... strings) {
            mRecruitDao.deleteGoneRecruit(strings[0]);
            return null;
        }
    }

    //RecruitCertificate
    public static class CertifiGetAllAsyncTask extends AsyncTask<Void, Void, List<RecruitCertificate>> {
        private RecruitCertificateDao mRecruitCertifiDao;

        public CertifiGetAllAsyncTask(RecruitCertificateDao recruitCertificateDao) {
            this.mRecruitCertifiDao = recruitCertificateDao;
        }

        @Override
        protected List<RecruitCertificate> doInBackground(Void... voids) {
            return mRecruitCertifiDao.getAll();
        }
    }

    public static class CertifiInsertAsyncTask extends AsyncTask<RecruitCertificate, Void, Void> {
        private RecruitCertificateDao mRecruitCertifiDao;

        public CertifiInsertAsyncTask(RecruitCertificateDao recruitCertificateDao) {
            this.mRecruitCertifiDao = recruitCertificateDao;
        }

        @Override // 백그라운드작업(메인스레드 X)
        protected Void doInBackground(RecruitCertificate... recruits) {
            mRecruitCertifiDao.insertNewRecruit(recruits[0]);
            return null;
        }
    }

    public static class CertifiLastUpdateAsyncTask extends AsyncTask<Void, Void, List<String>> {
        private RecruitCertificateDao mRecruitCertifiDao;

        public CertifiLastUpdateAsyncTask(RecruitCertificateDao recruitCertificateDao) {
            this.mRecruitCertifiDao = recruitCertificateDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return mRecruitCertifiDao.getLastUpdated();
        }
    }

    public static class CertifiDeleteAsyncTask extends AsyncTask<RecruitCertificate, Void, Void> {
        private RecruitCertificateDao mRecruitCertifiDao;

        public CertifiDeleteAsyncTask(RecruitCertificateDao recruitCertificateDao) {
            this.mRecruitCertifiDao = recruitCertificateDao;
        }

        @Override // 백그라운드작업(메인스레드 X)
        protected Void doInBackground(RecruitCertificate... recruitCertificates) {
            mRecruitCertifiDao.deleteGoneRecruit(recruitCertificates[0]);
            return null;
        }
    }

    //Job
    public static class JobGetAllAsyncTask extends AsyncTask<Void, Void, List<JobCategory>> {
        private JobCategoryDao mJobCategoryDao;

        public JobGetAllAsyncTask(JobCategoryDao jobCategoryDao) {
            this.mJobCategoryDao = jobCategoryDao;
        }

        @Override
        protected List<JobCategory> doInBackground(Void... voids) {
            return mJobCategoryDao.getAll();
        }
    }

    public static class JobGetCategoryAsyncTask extends AsyncTask<Void, Void, List<JobCategory>> {
        private JobCategoryDao mJobCategoryDao;

        public JobGetCategoryAsyncTask(JobCategoryDao jobCategoryDao) {
            this.mJobCategoryDao = jobCategoryDao;
        }

        @Override
        protected List<JobCategory> doInBackground(Void... voids) {
            return mJobCategoryDao.getCategory();
        }
    }

    public static class JobGetAsyncTask extends AsyncTask<Void, Void, List<JobCategory>> {
        private JobCategoryDao mJobCategoryDao;

        public JobGetAsyncTask(JobCategoryDao jobCategoryDao) {
            this.mJobCategoryDao = jobCategoryDao;
        }

        @Override
        protected List<JobCategory> doInBackground(Void... voids) {
            return mJobCategoryDao.getJob();
        }
    }

    public static class JobGetAllJobCodeAsyncTask extends AsyncTask<String, Void, List<String>> {
        private JobCategoryDao mJobCategoryDao;

        public JobGetAllJobCodeAsyncTask(JobCategoryDao jobCategoryDao){
            this.mJobCategoryDao = jobCategoryDao;
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            return mJobCategoryDao.getAllJobCode(strings[0]);
        }
    }

    public static class JobGetCategoryNameAsyncTask extends AsyncTask<String, Void, String> {
        private JobCategoryDao mJobCategoryDao;

        public JobGetCategoryNameAsyncTask(JobCategoryDao jobCategoryDao){
            this.mJobCategoryDao = jobCategoryDao;
        }

        @Override
        protected String doInBackground(String... strings) {
            return mJobCategoryDao.getCategoryName(strings[0]);
        }
    }

    //CVInfo
    public static class CvInfoGetAllAsyncTask extends AsyncTask<Void, Void, List<CvInfo>> {
        private CvInfoDao mCvInfoDao;

        public CvInfoGetAllAsyncTask(CvInfoDao cvInfoDao) {
            this.mCvInfoDao = cvInfoDao;
        }

        @Override
        protected List<CvInfo> doInBackground(Void... voids) {
            return mCvInfoDao.getAll();
        }
    }

    public static class CvInfoGetInfoCodeAsyncTask extends AsyncTask<String, Void, String> {
        private CvInfoDao mCvInfoDao;

        public CvInfoGetInfoCodeAsyncTask(CvInfoDao cvInfoDao) {
            this.mCvInfoDao = cvInfoDao;
        }

        @Override
        protected String doInBackground(String... strings) {
            return mCvInfoDao.getInfoCode(strings[0]);
        }
    }

    public static class CvInfoGetAsyncTask extends AsyncTask<String, Void, List<CvInfo>> {
        private CvInfoDao mCvInfoDao;

        public CvInfoGetAsyncTask(CvInfoDao cvInfoDao) {
            this.mCvInfoDao = cvInfoDao;
        }

        @Override
        protected List<CvInfo> doInBackground(String... strings) {
            return mCvInfoDao.getCvInfo(strings[0]);
        }
    }

    public static class CvInfoDeleteAsyncTask extends AsyncTask<String, Void, Void> {
        private CvInfoDao mCvInfoDao;

        public CvInfoDeleteAsyncTask(CvInfoDao cvInfoDao) {
            this.mCvInfoDao = cvInfoDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mCvInfoDao.deleteCvInfo(strings[0]);
            return null;
        }
    }

    public static class CvInfoInsertAsyncTask extends AsyncTask<CvInfo, Void, Void> {
        private CvInfoDao mCvInfoDao;

        public CvInfoInsertAsyncTask(CvInfoDao cvInfoDao) {
            this.mCvInfoDao = cvInfoDao;
        }

        @Override
        protected Void doInBackground(CvInfo... cvInfos) {
            mCvInfoDao.insertCvInfo(cvInfos[0]);
            return null;
        }
    }

    //CoverLetter
    public static class CoverLetterGetSelectedAsyncTask extends AsyncTask<Integer, Void, CoverLetter> {
        private CoverLetterDao mCoverLetterDao;

        public CoverLetterGetSelectedAsyncTask(CoverLetterDao coverLetterDao) {
            this.mCoverLetterDao = coverLetterDao;
        }

        @Override
        protected CoverLetter doInBackground(Integer... integers) {
            return mCoverLetterDao.getSelected(integers[0]);
        }
    }

    public static class CoverLetterInsertAsyncTask extends AsyncTask<CoverLetter, Void, Void> {
        private CoverLetterDao mCoverLetterDao;

        public CoverLetterInsertAsyncTask(CoverLetterDao coverLetterDao) {
            this.mCoverLetterDao = coverLetterDao;
        }

        @Override
        protected Void doInBackground(CoverLetter... coverLetters) {
            mCoverLetterDao.insertCoverLetter(coverLetters[0]);
            return null;
        }
    }

    public static class CoverLetterUpdateAsyncTask extends AsyncTask<HashMap<Integer, String>, Void, Void> {
        private CoverLetterDao mCoverLetterDao;

        public CoverLetterUpdateAsyncTask(CoverLetterDao coverLetterDao) {
            this.mCoverLetterDao = coverLetterDao;
        }

        @Override
        protected Void doInBackground(HashMap<Integer, String>... hashMaps) {
            mCoverLetterDao.updateCoverLetter(hashMaps[0].get(1), hashMaps[0].get(2), hashMaps[0].get(3), Integer.parseInt(hashMaps[0].get(4)));
            return null;
        }
    }

    public static class CoverLetterDeleteAsyncTask extends AsyncTask<Integer, Void, Void> {
        private CoverLetterDao mCoverLetterDao;

        public CoverLetterDeleteAsyncTask(CoverLetterDao coverLetterDao) {
            this.mCoverLetterDao = coverLetterDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            mCoverLetterDao.deleteCoverLetter(integers[0]);
            return null;
        }
    }

    //Certificate
    public static class CertificateGetAsyncTask extends AsyncTask<Void, Void, List<Certificate>> {
        private CertificateDao mCertificateDao;

        public CertificateGetAsyncTask(CertificateDao certificateDao) {
            this.mCertificateDao = certificateDao;
        }

        @Override
        protected List<Certificate> doInBackground(Void... voids) {
            return mCertificateDao.getAll();
        }
    }

    //Bdong
    public static class BdongGetAllAsyncTask extends AsyncTask<Void,Void,List<Bdong>>{
        private BdongDao mBdongDao;

        public BdongGetAllAsyncTask(BdongDao bdongDao){
            this.mBdongDao = bdongDao;
        }

        @Override
        protected List<Bdong> doInBackground(Void... voids) {
            return mBdongDao.getAll();
        }
    }

    public static class BdongGetSidoAsyncTask extends AsyncTask<Void,Void,List<String>>{
        private BdongDao mBdongDao;

        public BdongGetSidoAsyncTask(BdongDao bdongDao){
            this.mBdongDao = bdongDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return mBdongDao.getsido();
        }
    }

    public static class BdongGetSigunguAsyncTask extends AsyncTask<String,Void,List<String>>{
        private BdongDao mBdongDao;

        public BdongGetSigunguAsyncTask(BdongDao bdongDao){
            this.mBdongDao = bdongDao;
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            return mBdongDao.getsigungu(strings[0]);
        }
    }

    public static class BdongGetEupmyeondongAsyncTask extends AsyncTask<String,Void,List<String>>{
        private BdongDao mBdongDao;

        public BdongGetEupmyeondongAsyncTask(BdongDao bdongDao){
            this.mBdongDao = bdongDao;
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            return mBdongDao.geteupmyeondong(strings[0],strings[1]);
        }
    }

    public static class BdongGetSidoCodeAsyncTask extends AsyncTask<String,Void,List<String>>{
        private BdongDao mBdongDao;

        public BdongGetSidoCodeAsyncTask(BdongDao bdongDao){
            this.mBdongDao = bdongDao;
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            return mBdongDao.getAllSidoCode(strings[0]);
        }
    }

    public static class BdongGetSigunguCodeAsyncTask extends AsyncTask<String,Void,List<String>>{
        private BdongDao mBdongDao;

        public BdongGetSigunguCodeAsyncTask(BdongDao bdongDao){
            this.mBdongDao = bdongDao;
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            return mBdongDao.getAllSigunguCode(strings[0]);
        }
    }

    public static class BdongGetCodeAsyncTask extends AsyncTask<String,Void,String>{
        private BdongDao mBdongDao;

        public BdongGetCodeAsyncTask(BdongDao bdongDao){
            this.mBdongDao = bdongDao;
        }

        @Override
        protected String doInBackground(String... strings) {
            return mBdongDao.getBDongCode(strings[0],strings[1],strings[2]);
        }
    }

    public static class BdongGetTotalSidoCodeAsyncTask extends AsyncTask<String,Void,String>{
        private BdongDao mBdongDao;

        public BdongGetTotalSidoCodeAsyncTask(BdongDao bdongDao){
            this.mBdongDao = bdongDao;
        }

        @Override
        protected String doInBackground(String... strings) {
            return mBdongDao.getTotalSidoCode(strings[0]);
        }
    }

    public static class BdongGetTotalSigunguCodeAsyncTask extends AsyncTask<String,Void,String>{
        private BdongDao mBdongDao;

        public BdongGetTotalSigunguCodeAsyncTask(BdongDao bdongDao){
            this.mBdongDao = bdongDao;
        }

        @Override
        protected String doInBackground(String... strings) {
            return mBdongDao.getTotalSigunguCode(strings[0],strings[1]);
        }
    }

}
