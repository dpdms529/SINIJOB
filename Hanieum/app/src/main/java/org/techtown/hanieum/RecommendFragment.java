package org.techtown.hanieum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.collection.ArraySet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.RecruitCertificateDao;
import org.techtown.hanieum.db.dao.RecruitDao;
import org.techtown.hanieum.db.entity.Recruit;
import org.techtown.hanieum.db.entity.RecruitCertificate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RecommendFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView; // 추천 목록 리사이클러뷰
    RecommendAdapter adapter; // 추천 목록 어댑터
    Button changeButton; // 조건 변경 화면으로 이동하는 버튼
    ImageButton searchButton; // 검색 버튼
    ImageButton menuButton; // 메뉴 버튼
    TextView itemNum;
    TextView title; //화면 제목
    EditText editSearch;  //검색창

    ArrayList<Recommendation> items; //리사이클러뷰에서 보여줄 공고 아이템을 저장하고 있는 ArrayList
    ArrayList<Recommendation> allItems = new ArrayList<>(); //전체 공고 아이템을 저장하고있는 ArrayList

    InputMethodManager imm;
    AppDatabase db;

    SharedPreference pref;

    public RecommendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        recyclerView = view.findViewById(R.id.recommendView);
        changeButton = view.findViewById(R.id.changeButton);
        searchButton = view.findViewById(R.id.searchButton);
        menuButton = view.findViewById(R.id.menuButton);
        itemNum = view.findViewById(R.id.itemNum);
        title = view.findViewById(R.id.title);
        editSearch = view.findViewById(R.id.editSearch);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        db = AppDatabase.getInstance(this.getContext());

        pref = new SharedPreference(getContext());

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

        changeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        menuButton.setOnClickListener(this);

        editSearch.addTextChangedListener(new TextWatcher() {   // 공고 검색
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editSearch.getText().toString();
                search(text);
            }
        });

        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {   // 공고 검색
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    title.setVisibility(View.VISIBLE);
                    editSearch.setVisibility(View.GONE);

                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(),0); //키보드 내리기
                    return true;
                }
                return false;
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<ChipList> regions = pref.getArrayPref(SharedPreference.REGION_LIST);
        ArrayList<ChipList> jobs = pref.getArrayPref(SharedPreference.JOB_LIST);
        Log.d("recruit", "onResume: jobs : " + jobs.toString());
        int careerStatus = pref.preferences.getInt(SharedPreference.CAREER_STATUS,0);
        Log.d("recruit", "onResume: careerStatus : " + careerStatus);
        String workform =  pref.preferences.getString(SharedPreference.WORKFORM_STATUS,"A");    //근무형태
        Log.d("recruit", "onResume: workform :" + workform);
        int licenseStatus = pref.preferences.getInt(SharedPreference.LICENSE_STATUS,0);
        Log.d("recruit", "onResume: licenseStatus : " + licenseStatus);

        List<String> bDongCode = new ArrayList<>(); //지역
        for(ChipList i : regions){
            if(i.getCode().length()==2){    //시도 전체 법정동 코드 가져오기
                List<String> sido = db.BdongDao().getAllSidoCode(i.getCode());
                for(String j : sido){
                    bDongCode.add(j);
                    Log.d("recruit", "onResume: bDongCode : " + j);
                }
            }else if(i.getCode().length()==5){  //시군구 전체 법정도 코드 가져오기
                List<String> sigungu = db.BdongDao().getAllSigunguCode(i.getCode());
                for(String j : sigungu){
                    bDongCode.add(j);
                    Log.d("recruit", "onResume: bDongCode : " + j);
                }
            }else{  //해당 법정동 코드 가져오기
                bDongCode.add(i.getCode());
                Log.d("recruit", "onResume: bDongCode : " + i.getCode());
            }
        }

        List<String> jobCode = new ArrayList<>();   //직종
        for(ChipList i : jobs){
            if(i.getCode().length()==2){    //1차분류 전체 직종코드 가져오기
                List<String> allJob = db.jobCategoryDao().getAllJobCode(i.getCode());
                for(String j : allJob){
                    jobCode.add(j);
                }
            }else{  //해당 직종코드 가져오기
                jobCode.add(i.getCode());
            }
        }
        for(String i : jobCode){
            Log.d("recruit", "onResume: jobCode : " + i);
        }

        List<String> careerJobTmp= pref.getStringArrayPref(SharedPreference.CAREER_JOB_CODE);
        String careerJobCode = "";
        if (!careerJobTmp.isEmpty()) {
            careerJobCode = careerJobTmp.get(0);
        }
        List<String> careerTmp = pref.getStringArrayPref(SharedPreference.CAREER_PERIOD);
        int career = 0;
        if (!careerTmp.isEmpty()) {
            if (!careerTmp.get(0).equals("")) {
                career = Integer.valueOf(careerTmp.get(0));
            }
        }
        List<String> certificateTmp = pref.getStringArrayPref(SharedPreference.CERTIFICATE_CODE);
        List<String> certificate = new ArrayList<>();
        for(String i : certificateTmp){
            if(!i.equals("")) {
                certificate.add(i);
            }
        }
        List<Recruit> result;

        if(careerStatus == 0 && licenseStatus == 0){    //경력, 자격증 적용안함
            if(bDongCode.size() == 0){  //지역 선택 안했을 때 -> 전체 지역
                if(jobs.size() == 0){   //직종 선택 안했을 때 -> 전체 직종
                    if(workform.equals("A")){   //근무형태 전체 선택
                        checkLastUpdated();     // 최신 업데이트 일시 확인(일치 -> 유지, 불일치 -> 데이터 가져오기)
                        checkCertifiLastUpdated();
                        db.RecruitDao().getAll().observe((LifecycleOwner) this.getContext(), new Observer<List<Recruit>>() {
                            @Override
                            public void onChanged(List<Recruit> recruits) {
                                loadListData(recruits);     // LiveData - 데이터 변경을 감지하면 UI 갱신
                            }
                        });
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList1(workform);
                        Log.d("recruit", "onResume: dao1" + result.toString());
                        loadListData(result);
                    }
                }else{  //직종 선택했을 때
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList2(jobCode);
                        Log.d("recruit", "onResume: dao2" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList3(jobCode, workform);
                        Log.d("recruit", "onResume: dao3" + result.toString());
                        loadListData(result);
                    }
                }
            }else{  //지역 선택 했을 때
                if(jobs.size() == 0){   //직종 선택 안했을 때 -> 전체 지역
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList4(bDongCode);
                        Log.d("recruit", "onResume: dao4" + result.toString());
                        loadListData(result);

                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList5(bDongCode,workform);
                        Log.d("recruit", "onResume: dao5" + result.toString());
                        loadListData(result);
                    }

                }else{  //직종 선택 했을 때
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList6(bDongCode,jobCode);
                        Log.d("recruit", "onResume: dao6" + result.toString());
                        loadListData(result);

                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList7(bDongCode,jobCode,workform);
                        Log.d("recruit", "onResume: dao7" + result.toString());
                        loadListData(result);
                    }

                }

            }
        }else if(licenseStatus == 0){    //경력 적용, 자격증 적용 안함
            if(bDongCode.size() == 0){  //지역 선택 안했을 때 -> 전체 지역
                if(jobs.size() == 0){   //직종 선택 안했을 때 -> 전체 직종
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList8(careerJobCode, career);
                        Log.d("recruit", "onResume: dao8" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList9(careerJobCode, career, workform);
                        Log.d("recruit", "onResume: dao9" + result.toString());
                        loadListData(result);
                    }
                }else{  //직종 선택했을 때
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList10(jobCode, careerJobCode, career);
                        Log.d("recruit", "onResume: dao10" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList11(jobCode, careerJobCode, career, workform);
                        Log.d("recruit", "onResume: dao11" + result.toString());
                        loadListData(result);
                    }
                }
            }else{  //지역 선택 했을 때
                if(jobs.size() == 0){   //직종 선택 안했을 때 -> 전체 지역
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList12(bDongCode, careerJobCode, career);
                        Log.d("recruit", "onResume: dao12" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList13(bDongCode, careerJobCode, career, workform);
                        Log.d("recruit", "onResume: dao13" + result.toString());
                        loadListData(result);
                    }

                }else{  //직종 선택 했을 때
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList14(bDongCode, jobCode, careerJobCode, career);
                        Log.d("recruit", "onResume: dao14" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList15(bDongCode, jobCode, careerJobCode, career, workform);
                        Log.d("recruit", "onResume: dao15" + result.toString());
                        loadListData(result);
                    }

                }

            }

        }else if(careerStatus == 0){   //경력 적용 안함, 자격증 적용
            if(bDongCode.size() == 0){  //지역 선택 안했을 때 -> 전체 지역
                if(jobs.size() == 0){   //직종 선택 안했을 때 -> 전체 직종
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList16(certificate);
                        Log.d("recruit", "onResume: dao16" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList17(certificate, workform);
                        Log.d("recruit", "onResume: dao17" + result.toString());
                        loadListData(result);
                    }
                }else{  //직종 선택했을 때
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList18(jobCode, certificate);
                        Log.d("recruit", "onResume: dao18" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList19(jobCode, workform, certificate);
                        Log.d("recruit", "onResume: dao19" + result.toString());
                        loadListData(result);
                    }
                }
            }else{  //지역 선택 했을 때
                if(jobs.size() == 0){   //직종 선택 안했을 때 -> 전체 지역
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList20(bDongCode, certificate);
                        Log.d("recruit", "onResume: dao20" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList21(bDongCode, workform, certificate);
                        Log.d("recruit", "onResume: dao21" + result.toString());
                        loadListData(result);
                    }

                }else{  //직종 선택 했을 때
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList22(bDongCode, jobCode, certificate);
                        Log.d("recruit", "onResume: dao22" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList23(bDongCode, jobCode, workform, certificate);
                        Log.d("recruit", "onResume: dao23" + result.toString());
                        loadListData(result);
                    }

                }

            }

        }else{  //경력 적용, 자격증 적용
            if(bDongCode.size() == 0){  //지역 선택 안했을 때 -> 전체 지역
                if(jobs.size() == 0){   //직종 선택 안했을 때 -> 전체 직종
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList24(careerJobCode, career, certificate);
                        Log.d("recruit", "onResume: dao24" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList25(careerJobCode, career, workform, certificate);
                        Log.d("recruit", "onResume: dao25" + result.toString());
                        loadListData(result);
                    }
                }else{  //직종 선택했을 때
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList26(jobCode, careerJobCode, career, certificate);
                        Log.d("recruit", "onResume: dao26" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList27(jobCode, careerJobCode, career, workform, certificate);
                        Log.d("recruit", "onResume: dao27" + result.toString());
                        loadListData(result);
                    }
                }
            }else{  //지역 선택 했을 때
                if(jobs.size() == 0){   //직종 선택 안했을 때 -> 전체 지역
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList28(bDongCode, careerJobCode, career, certificate);
                        Log.d("recruit", "onResume: dao28" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList29(bDongCode, careerJobCode, career, workform, certificate);
                        Log.d("recruit", "onResume: dao" + result.toString());
                        loadListData(result);
                    }

                }else{  //직종 선택 했을 때
                    if(workform.equals("A")){   //근무형태 전체 선택
                        result = db.RecruitDao().getFilteredList30(bDongCode, jobCode, careerJobCode, career, certificate);
                        Log.d("recruit", "onResume: dao30" + result.toString());
                        loadListData(result);
                    }else{  //근무형태 선택했을 때 (정규직 or 계약직)
                        result = db.RecruitDao().getFilteredList31(bDongCode, jobCode, careerJobCode, career, workform, certificate);
                        Log.d("recruit", "onResume: dao31" + result.toString());
                        loadListData(result);
                    }

                }

            }

        }

    }

    @Override
    public void onClick(View v) {
        if (v == changeButton) {
            Intent intent = new Intent(this.getContext(), FilteringActivity.class);
            startActivity(intent);
        } else if (v == searchButton) {
            if(title.getVisibility()==View.VISIBLE){
                title.setVisibility(View.GONE);
                editSearch.setVisibility(View.VISIBLE);
            }else{
                title.setVisibility(View.VISIBLE);
                editSearch.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(editSearch.getWindowToken(),0);
            }


        } else if (v == menuButton) {
            Toast.makeText(v.getContext(), "메뉴 버튼 눌림", Toast.LENGTH_LONG).show();
        }
    }

    private void checkLastUpdated() { // 기기의 업데이트 일시와 DB의 업데이트 일시를 확인
        List<String> rows = db.RecruitDao().getLastUpdated();
        String lastUpdated = rows.get(0);
        Log.d("date: ", "recruit: "+lastUpdated);
        String dbLastUpdated = "";

        String php = getResources().getString(R.string.serverIP)+"recruit_lastupdated.php";
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
            String recruitPhp = getResources().getString(R.string.serverIP)+"recruit_update.php?last_updated=" + lastUpdated;
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
                for(int i = 0; i < jsonArray.length(); i++) {
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
                        new RecruitInsertAsyncTask(db.RecruitDao()).execute(newRecruit);   // 백그라운드 INSERT 실행
                    } else {    // 지워진 기존 데이터
                        new RecruitDeleteAsyncTask(db.RecruitDao()).execute(jsonObject1.getString("recruit_id"));   // 백그라운드 DELETE 실행
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkCertifiLastUpdated() { // 기기의 업데이트 일시와 DB의 업데이트 일시를 확인
        List<String> rows = db.recruitCertificateDao().getLastUpdated();
        String lastUpdated = rows.get(0);
        Log.d("date: ", "certifi: "+lastUpdated);
        String dbLastUpdated = "";

        String php = getResources().getString(R.string.serverIP)+"recruit_lastupdated.php";
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
            String recruitCertificatePhp = getResources().getString(R.string.serverIP)+"certificate_update.php?last_updated=" + lastUpdated;
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
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if (jsonObject1.getString("deleted").equals("0")) {     // 새로 생긴 데이터
                        String recruit_id = jsonObject1.getString("recruit_id");
                        Integer certificate_no = jsonObject1.getInt("certificate_no");
                        String certificate_id = jsonObject1.getString("certificate_id");
                        RecruitCertificate newRecruitCertificate = new RecruitCertificate(certificate_no, recruit_id, certificate_id);
                        new CertifiInsertAsyncTask(db.recruitCertificateDao()).execute(newRecruitCertificate);   // 백그라운드 INSERT 실행
                    } else {    // 지워진 기존 데이터
                        String recruit_id = jsonObject1.getString("recruit_id");
                        Integer certificate_no = jsonObject1.getInt("certificate_no");
                        String certificate_id = jsonObject1.getString("certificate_id");
                        RecruitCertificate newRecruitCertificate = new RecruitCertificate(certificate_no, recruit_id, certificate_id);
                        new CertifiDeleteAsyncTask(db.recruitCertificateDao()).execute(newRecruitCertificate);   // 백그라운드 DELETE 실행
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 메인스레드에서 데이터베이스에 접근할 수 없으므로 AsyncTask 사용 - INSERT
    public static class RecruitInsertAsyncTask extends AsyncTask<Recruit, Void, Void> {
        private RecruitDao mRecruitDao;

        public  RecruitInsertAsyncTask(RecruitDao recruitDao){
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

        public  CertifiInsertAsyncTask(RecruitCertificateDao recruitCertificateDao){
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

        public  RecruitDeleteAsyncTask(RecruitDao recruitDao){
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

        public  CertifiDeleteAsyncTask(RecruitCertificateDao recruitCertificateDao){
            this.mRecruitCertifiDao = recruitCertificateDao;
        }

        @Override // 백그라운드작업(메인스레드 X)
        protected Void doInBackground(RecruitCertificate... recruitCertificates) {
            mRecruitCertifiDao.deleteGoneRecruit(recruitCertificates[0]);
            return null;
        }
    }

    private void loadListData(List<Recruit> result) { // 항목을 로드하는 함수
        List<Recruit> rows = result;

        allItems.clear();
        items = new ArrayList<>();

        itemNum.setText(String.valueOf(rows.size()));

        for (int i=0; i<rows.size(); i++) {
            Recruit row = rows.get(i);
            String salaryType = new String();
            switch (row.salary_type_code) {     // 급여 타입에 알맞은 단어
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
            String[] tmp = row.salary.split(" ~ ");
            if (tmp.length == 1 || tmp[0].equals(tmp[1])) {
                String[] tmp2 = tmp[0].split("만원|원");
                sal = tmp2[0];
            } else {
                String[] tmp2 = tmp[0].split("만원|원");
                String[] tmp3 = tmp[1].split("만원|원");
                sal = tmp2[0] + " ~ " + tmp3[0];
            }
            DistanceCalculator distance =  new DistanceCalculator("127.12934", "35.84688", row.x_coordinate, row.y_coordinate);
            Double dist = distance.getStraightDist();   // 직선거리 구하는 함수
            allItems.add(new Recommendation(row.recruit_id, row.organization, row.recruit_title, salaryType, sal, dist, false));
        }
        Collections.sort(allItems);    // 거리순으로 정렬
        items.addAll(allItems);
        adapter.setItems(items);
        recyclerView.setAdapter(adapter);
    }

    public void search(String text){    //공고 제목 또는 회사명으로 검색하는 함수
        items.clear();
        if(text.length() == 0){ //검색어 없을 때 전체 데이터 보여줌
            items.addAll(allItems);
        }else{  //검색어 있을 때 검색어를 포함하는 데이터만 보여줌
            for(int i = 0;i<allItems.size();i++) {
                if (allItems.get(i).getTitle().contains(text) || allItems.get(i).getCompanyName().contains(text)) {
                    items.add(allItems.get(i));
                }
            }
        }
        itemNum.setText(String.valueOf(items.size()));
        adapter.notifyDataSetChanged();
    }
}