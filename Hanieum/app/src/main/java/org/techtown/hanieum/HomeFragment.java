package org.techtown.hanieum;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.RecruitCertificateDao;
import org.techtown.hanieum.db.dao.RecruitDao;
import org.techtown.hanieum.db.entity.Recruit;
import org.techtown.hanieum.db.entity.RecruitCertificate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView; // 추천 목록 리사이클러뷰
    RecommendAdapter adapter; // 추천 목록 어댑터
    Button changeButton; // 조건 변경 화면으로 이동하는 버튼
    Button summaryButton; // 음성 요약 버튼
    ImageButton helpButton; // 도움말 버튼
    TextView itemNum;
    String msg; // 음성 요약 메세지

    AppDatabase db;

    Context context;

    TextToSpeech tts;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        context = getContext();
        recyclerView = view.findViewById(R.id.recommendView);
        changeButton = view.findViewById(R.id.changeButton);
        summaryButton = view.findViewById(R.id.voice_summary_rec);
        helpButton = view.findViewById(R.id.helpButton);
        itemNum = view.findViewById(R.id.itemNum);
        msg = "추천된 일자리가 없습니다.";

        db = AppDatabase.getInstance(this.getContext());

        // 음성출력 생성, 리스너 초기화
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        // 리사이클러뷰와 어댑터 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), 1)); // 구분선
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecommendAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setItemClickListener(new OnRecoItemClickListener() {
            @Override
            public void OnItemClick(RecommendAdapter.ViewHolder holder, View view, int position) {
                Recommendation item = adapter.getItem(position);
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("id", item.getId());
                startActivity(intent);
            }
        });

        summaryButton.setOnClickListener(this);
        helpButton.setOnClickListener(this);

        checkLastUpdated();
        checkCertifiLastUpdated();

        loadListData();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == helpButton) {
            Intent intent = new Intent(this.getContext(), HelpActivity.class);
            intent.putExtra("from", "HomeFragment");
            startActivity(intent);
        } else if (v == summaryButton) {
            voiceOut(msg);
        }
    }

    private void checkLastUpdated() { // 기기의 업데이트 일시와 DB의 업데이트 일시를 확인
        List<String> rows = null;
        try {
            rows = new RecommendFragment.RecruitLastUpdateAsyncTask(db.RecruitDao()).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String lastUpdated = rows.get(0);
        Log.d("date: ", "recruit: " + lastUpdated);
        String dbLastUpdated = "";

        String php = getResources().getString(R.string.serverIP) + "recruit_lastupdated.php";
        URLConnector urlConnector = new URLConnector(php);

        urlConnector.start();
        try {
            urlConnector.join();
        } catch (InterruptedException e) {
        }
        String result = urlConnector.getResult();

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            dbLastUpdated = jsonArray.getJSONObject(0).getString("last_updated");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!lastUpdated.equals(dbLastUpdated)) {   // 최신 업데이트 일시 확인(불일치 -> 데이터 가져오기)
            String recruitPhp = getResources().getString(R.string.serverIP) + "recruit_update.php?last_updated=" + lastUpdated;
            URLConnector urlConnectorRecruit = new URLConnector(recruitPhp);

            urlConnectorRecruit.start();
            try {
                urlConnectorRecruit.join();
            } catch (InterruptedException e) {
            }
            String recruitResult = urlConnectorRecruit.getResult();

            try {
                JSONObject jsonObject = new JSONObject(recruitResult);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if (jsonObject1.getString("deleted").equals("0")) {     // 새로 생긴 데이터
                        String recruit_id = jsonObject1.getString("recruit_id");
                        String title = jsonObject1.getString("title");
                        String organization = jsonObject1.getString("organization");
                        String salary_type_code = jsonObject1.getString("salary_type_code");
                        String salary = jsonObject1.getString("salary");
                        String b_dong_code = jsonObject1.getString("b_dong_code");
                        String job_code = jsonObject1.getString("job_code");
                        String career_required = jsonObject1.getString("career_required");
                        int career_min = jsonObject1.getInt("career_min");
                        String enrollment_code = jsonObject1.getString("enrollment_code");
                        String certificate_required = jsonObject1.getString("certificate_required");
                        String x = jsonObject1.getString("x");
                        String y = jsonObject1.getString("y");
                        String update_dt = jsonObject1.getString("update_dt");
                        Recruit newRecruit = new Recruit(recruit_id, title, organization, salary_type_code, salary, b_dong_code, job_code, career_required, career_min, enrollment_code, certificate_required, x, y, update_dt);
                        new RecommendFragment.RecruitInsertAsyncTask(db.RecruitDao()).execute(newRecruit);   // 백그라운드 INSERT 실행
                    } else {    // 지워진 기존 데이터
                        new RecommendFragment.RecruitDeleteAsyncTask(db.RecruitDao()).execute(jsonObject1.getString("recruit_id"));   // 백그라운드 DELETE 실행
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 북마크 업데이트 (삭제된 공고 제거)
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
            String _uId = getResources().getString(R.string.user_id);  // 유저 아이디
            String bookmarkPhp = context.getResources().getString(R.string.serverIP) + "bookmark_read.php?user_id=" + _uId;
            URLConnector urlConnectorBookmark = new URLConnector(bookmarkPhp);
            urlConnectorBookmark.start();
            try {
                urlConnectorBookmark.join();
            } catch (InterruptedException e) {
            }
            String bookmarkResult = urlConnectorBookmark.getResult();

            try {
                JSONObject jsonObject = new JSONObject(bookmarkResult);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    HashMap<String, String> hashMap = new HashMap<>();
                    String user_id = jsonObject1.getString("user_id");
                    String recruit_id = jsonObject1.getString("recruit_id");

                    hashMap.put("user_id", user_id);
                    hashMap.put("recruit_id", recruit_id);

                    arrayList.add(hashMap);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int j = 0; j < arrayList.size(); j++) {
                HashMap<String, String> hashMap = arrayList.get(j);
                String uId = hashMap.get("user_id");
                String rId = hashMap.get("recruit_id");
                List<Recruit> recruits = null;
                try {
                    recruits = new RecommendFragment.RecruitGetListAsyncTask(db.RecruitDao()).execute(rId).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (recruits.size() == 0) { // 삭제된 공고면 북마크 테이블에서 해당 공고 삭제
                    String bookmarkDelPhp = context.getResources().getString(R.string.serverIP) + "bookmark_del.php?user_id=" + _uId + "&recruit_id=" + rId;
                    URLConnector urlConnectorBookmarkDel = new URLConnector(bookmarkDelPhp);
                    urlConnectorBookmarkDel.start();
                    try {
                        urlConnectorBookmarkDel.join();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    private void checkCertifiLastUpdated() { // 기기의 업데이트 일시와 DB의 업데이트 일시를 확인
        List<String> rows = null;
        try {
            rows = new CertifiLastUpdateAsyncTask(db.recruitCertificateDao()).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        db.recruitCertificateDao().getLastUpdated();
        String lastUpdated = rows.get(0);
        Log.d("lastUpdated", "checkCertifiLastUpdated: " + lastUpdated);
        Log.d("date: ", "certifi: " + lastUpdated);
        String dbLastUpdated = "";

        String php = getResources().getString(R.string.serverIP) + "recruit_lastupdated.php";
        URLConnector urlConnector = new URLConnector(php);

        urlConnector.start();
        try {
            urlConnector.join();
        } catch (InterruptedException e) {
        }
        String result = urlConnector.getResult();

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            dbLastUpdated = jsonArray.getJSONObject(0).getString("last_updated");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!lastUpdated.equals(dbLastUpdated)) {   // 최신 업데이트 일시 확인(불일치 -> 데이터 가져오기)
            String recruitCertificatePhp = getResources().getString(R.string.serverIP) + "certificate_update.php?last_updated=" + lastUpdated;
            URLConnector urlConnectorRecruitCertificate = new URLConnector(recruitCertificatePhp);

            urlConnectorRecruitCertificate.start();
            try {
                urlConnectorRecruitCertificate.join();
            } catch (InterruptedException e) {
            }
            String recruitCertificateResult = urlConnectorRecruitCertificate.getResult();

            try {
                JSONObject jsonObject = new JSONObject(recruitCertificateResult);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if (jsonObject1.getString("deleted").equals("0")) {     // 새로 생긴 데이터
                        String recruit_id = jsonObject1.getString("recruit_id");
                        Integer certificate_no = jsonObject1.getInt("certificate_no");
                        String certificate_id = jsonObject1.getString("certificate_id");
                        RecruitCertificate newRecruitCertificate = new RecruitCertificate(certificate_no, recruit_id, certificate_id);
                        new RecommendFragment.CertifiInsertAsyncTask(db.recruitCertificateDao()).execute(newRecruitCertificate);   // 백그라운드 INSERT 실행
                    } else {    // 지워진 기존 데이터
                        String recruit_id = jsonObject1.getString("recruit_id");
                        Integer certificate_no = jsonObject1.getInt("certificate_no");
                        String certificate_id = jsonObject1.getString("certificate_id");
                        RecruitCertificate newRecruitCertificate = new RecruitCertificate(certificate_no, recruit_id, certificate_id);
                        new RecommendFragment.CertifiDeleteAsyncTask(db.recruitCertificateDao()).execute(newRecruitCertificate);   // 백그라운드 DELETE 실행
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

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

    private void loadListData() {
        ArrayList<Recommendation> items = new ArrayList<>();
        String recoPhp = getResources().getString(R.string.serverIP) + "reco_list.php?user_id=" + getResources().getString(R.string.user_id);
        URLConnector urlConnector = new URLConnector(recoPhp);

        ArrayList<String> jobNm = new ArrayList<>(); // 직종명과 추천순위 저장

        // 북마크 테이블 읽어오기
        ArrayList<String> arrayList = new ArrayList<>();
        String bookmarkPhp = context.getResources().getString(R.string.serverIP) + "bookmark_read.php?user_id=" + getResources().getString(R.string.user_id);
        URLConnector urlConnectorBookmark = new URLConnector(bookmarkPhp);
        urlConnectorBookmark.start();
        try {
            urlConnectorBookmark.join();
        } catch (InterruptedException e) {
        }
        String bookmarkResult = urlConnectorBookmark.getResult();

        try {
            JSONObject jsonObject = new JSONObject(bookmarkResult);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String recruit_id = jsonObject1.getString("recruit_id");
                arrayList.add(recruit_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        urlConnector.start();
        try {
            urlConnector.join();
        } catch (InterruptedException e) {
        }
        String result = urlConnector.getResult();

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            int count = 0;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                String recruit_id = jsonObject1.getString("recruit_id");
                List<Recruit> recruit = null;
                try {
                    recruit = new RecruitGetListAsyncTask(db.RecruitDao()).execute(recruit_id).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int flag = 0;
                String salaryType = new String();
                switch (recruit.get(0).salary_type_code) {     // 급여 타입에 알맞은 단어
                    case "H":
                        salaryType = "시";
                        break;
                    case "D":
                        salaryType = "일";
                        break;
                    case "M":
                        salaryType = "월";
                        break;
                    case "Y":
                        salaryType = "연";
                        break;
                }
                String sal = new String();
                String[] tmp = recruit.get(0).salary.split(" ~ ");
                if (tmp.length == 1 || tmp[0].equals(tmp[1])) {
                    String[] tmp2 = tmp[0].split("만원|원");
                    sal = tmp2[0];
                } else {
                    String[] tmp2 = tmp[0].split("만원|원");
                    String[] tmp3 = tmp[1].split("만원|원");
                    sal = tmp2[0] + " ~ " + tmp3[0];
                }
                DistanceCalculator distance = new DistanceCalculator("127.12934", "35.84688", recruit.get(0).x_coordinate, recruit.get(0).y_coordinate);
                Double dist = distance.getStraightDist();   // 직선거리 구하는 함수
                if (dist > 15000) {
                    continue;
                } else {
                    // 북마크 확인하는 코드
                    for (int j = 0; j < arrayList.size(); j++) {
                        String rId = arrayList.get(j);
                        if (rId.equals(recruit.get(0).recruit_id)) {
                            flag = 1;
                        }
                    }
                    if (flag == 1) {    // 북마크가 되어 있을 때
                        items.add(new Recommendation(recruit.get(0).recruit_id, recruit.get(0).organization, recruit.get(0).recruit_title, salaryType, sal, dist, true));
                    } else {    // 북마크가 안 되어 있을 때
                        items.add(new Recommendation(recruit.get(0).recruit_id, recruit.get(0).organization, recruit.get(0).recruit_title, salaryType, sal, dist, false));
                    }

                    if (count == 0 || count == 1 || count == 2) { // 1,2,3번째 추천된 공고의 직종명 저장
                        String job = db.jobCategoryDao().getCategoryName(recruit.get(0).job_code);
                        int idx = job.indexOf("(");
                        if (idx != -1) {
                            job = job.substring(0, idx);
                        }
                        jobNm.add(count, job);
                    }

                    count++;
                }
                if (count == 100) {
                    break;
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        itemNum.setText(String.valueOf(items.size()));
        adapter.setItems(items);

        if (Integer.parseInt(itemNum.getText().toString()) > 0) {    // 추천 공고가 있을 경우 최대 3건까지 요약
            msg = "추천된 공고는 총" + itemNum.getText().toString() + " 건 입니다.";
            if (Integer.parseInt(itemNum.getText().toString()) >= 3) {
                msg += "첫 번째로 추천된 공고는 " + items.get(0).getCompanyName() + "에서 " + jobNm.get(0) + "를 모집하는 공고입니다. " +
                        "두 번째로 추천된 공고는 " + items.get(1).getCompanyName() + "에서 " + jobNm.get(1) + "를 모집하는 공고입니다. " +
                        "세 번째로 추천된 공고는 " + items.get(2).getCompanyName() + "에서 " + jobNm.get(2) + "를 모집하는 공고입니다. ";
            } else if (Integer.parseInt(itemNum.getText().toString()) >= 2) {
                msg += "첫 번째로 추천된 공고는 " + items.get(0).getCompanyName() + "에서 " + jobNm.get(0) + "를 모집하는 공고입니다. " +
                        "두 번째로 추천된 공고는 " + items.get(1).getCompanyName() + "에서 " + jobNm.get(1) + "를 모집하는 공고입니다. ";
            } else if (Integer.parseInt(itemNum.getText().toString()) >= 1) {
                msg += "추천된 공고는 " + items.get(0).getCompanyName() + "에서 " + jobNm.get(0) + "를 모집하는 공고입니다. ";
            } else {
                // 추천 공고가 없을 경우 "추천된 일자리가 없습니다" 출력
            }
        } else {
        }
    }

    // 음성 메세지 출력용
    private void voiceOut(String msg) {
        if (msg.length() < 1) return;

        // 음성 출력
        tts.setPitch(0.8f); //목소리 톤1.0
        tts.setSpeechRate(0.9f);    //목소리 속도
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}