package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class FilteringActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    RecyclerView priorityContentsView; // 우선 순위 내용 리사이클러뷰
    RecyclerView priorityNumView; // 우선 순위 숫자 리사이클러뷰
    PriorityAdapter contentsAdapter; // 우선 순위 내용 어댑터
    PriorityAdapter numAdapter; // 우선 순위 숫자 어댑터
    ItemTouchHelper helper; // 우선 순위 내용을 드래그 하기 위한 helper
    Button saveButton; // 저장 버튼
    Button resetButton; // 초기화 버튼
    Button regionButton; // 지역 선택 화면으로 이동하는 버튼
    Button jobButton; // 직종 선택 화면으로 이동하는 버튼
    RadioGroup careerGroup; // 경력 RadioGroup
    RadioGroup licenseGroup; // 자격증 RadioGroup
    RadioGroup workFormGroup; // 근무형태 RadioGroup
    RadioButton noCareerButton; // 경력 적용 안 함 라디오버튼
    RadioButton yesCareerButton; // 나의 경력 적용(이력서) 라디오버튼
    RadioButton noLicenseButton; // 자격증 적용 안 함 라디오버튼
    RadioButton yesLicenseButton; // 나의 자격증 적용(이력서) 라디오버튼
    RadioButton allWorkFormButton; // 근무형태 전체 라디오버튼
    RadioButton workFormButton1; // 정규직 라디오버튼
    RadioButton workFormButton2; // 계약직 라디오버튼
    static SharedPreferences pref;
    SharedPreferences.Editor edit;
    String careerStatus;
    String licenseStatus;
    String workFormStatus;
    Context context;
    int careerId;
    int licenseId;
    int workFormId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);

        toolbar = findViewById(R.id.toolbar2);
        priorityContentsView = findViewById(R.id.priorityContentsView);
        priorityNumView = findViewById(R.id.priorityNumView);
        saveButton = findViewById(R.id.saveButton);
        resetButton = findViewById(R.id.resetButton);
        regionButton = findViewById(R.id.regionButton);
        jobButton = findViewById(R.id.jobButton);
        careerGroup = findViewById(R.id.careerGroup);
        licenseGroup = findViewById(R.id.licenseGroup);
        workFormGroup = findViewById(R.id.workFormGroup);
        noCareerButton = findViewById(R.id.noCareer);
        yesCareerButton = findViewById(R.id.yesCareer);
        noLicenseButton = findViewById(R.id.noLicense);
        yesLicenseButton = findViewById(R.id.yesLicense);
        allWorkFormButton = findViewById(R.id.allWorkFrom);
        workFormButton1 = findViewById(R.id.workForm1);
        workFormButton2 = findViewById(R.id.workForm2);
        context = this;

        // 경력 조건 상태값 불러오기
        try {
            pref = getSharedPreferences(SharedPreference.CAREER_STATUS, MODE_PRIVATE);
            careerId = Integer.parseInt(pref.getString(SharedPreference.CAREER_STATUS, "0"));
            System.out.println(careerId);
        } catch (NullPointerException e) {
            Log.e("carerId","NullException");
        }
        if(careerId == 0) { // 적용 안 함 선택한 상태
            noCareerButton.setChecked(true);
        } else { // 경력 적용 선택한 상태
            yesCareerButton.setChecked(true);
        }

        // 자격조건 상태값 불러오기
        try {
            pref = getSharedPreferences(SharedPreference.LICENSE_STATUS, MODE_PRIVATE);
            licenseId = Integer.parseInt(pref.getString(SharedPreference.LICENSE_STATUS, "0"));
        } catch (NullPointerException e) {
            Log.e("license","NullException");
        }
        if(licenseId == 0) { // 적용 안 함 선택한 상태
            noLicenseButton.setChecked(true);
        } else { // 자격증 적용 선택한 상태
            yesLicenseButton.setChecked(true);
        }

        // 근무형태 조건 상태값 불러오기
        try {
            pref = getSharedPreferences(SharedPreference.WORKFORM_STATUS, MODE_PRIVATE);
            workFormId = Integer.parseInt(pref.getString(SharedPreference.WORKFORM_STATUS, "0"));
        } catch (NullPointerException e) {
            Log.e("workForm","NullException");
        }
        if(workFormId == 0) { // 전체 선택한 상태
            allWorkFormButton.setChecked(true);
        } else if (workFormId == 1) { // 정규직 선택한 상태
            workFormButton1.setChecked(true);
        } else { // 계약직 선택한 상태
            workFormButton2.setChecked(true);
        }

        careerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.noCareer) { // 적용 안 함을 선택하면
                    careerStatus = "0";
                    Toast.makeText(FilteringActivity.this, "noCareer",Toast.LENGTH_SHORT).show();
                } else if(i == R.id.yesCareer) { // 나의 경력 적용(이력서)를 선택하면
                    careerStatus = "1";
                }
            }
        });

        licenseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.noLicense) { // 적용 안 함을 선택하면
                    licenseStatus = "0";
                } else if(i == R.id.yesLicense) { // 나의 자격증 적용(이력서)를 선택하면
                    licenseStatus = "1";
                }
            }
        });

        workFormGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.allWorkFrom) { // 전체를 선택하면
                    workFormStatus = "0";
                } else if(i == R.id.workForm1) { // 정규직을 선택하면
                    workFormStatus = "1";
                } else if (i == R.id.workForm2) { // 계약직을 선택하면
                    workFormStatus = "2";
                }
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기

        // 리사이클러뷰와 어댑터 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        priorityContentsView.setLayoutManager(layoutManager);
        priorityNumView.setLayoutManager(layoutManager2);
        contentsAdapter = new PriorityAdapter();
        numAdapter = new PriorityAdapter();
        priorityContentsView.setAdapter(contentsAdapter);
        priorityNumView.setAdapter(numAdapter);

        // 내용 리사이클러뷰에 드래그를 위한 helper 연결
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(contentsAdapter));
        helper.attachToRecyclerView(priorityContentsView);

        saveButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        regionButton.setOnClickListener(this);
        jobButton.setOnClickListener(this);

        loadListData();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == saveButton) { //저장 버튼 누른 경우
           // 경력 조건 저장
            pref = getSharedPreferences(SharedPreference.CAREER_STATUS, MODE_PRIVATE);
            edit = pref.edit();
            edit.putString(SharedPreference.CAREER_STATUS, careerStatus);
            edit.commit();

            // 자격증 조건 저장
            pref = getSharedPreferences(SharedPreference.LICENSE_STATUS, MODE_PRIVATE);
            edit = pref.edit();
            edit.putString(SharedPreference.LICENSE_STATUS, licenseStatus);
            edit.commit();

            // 근무형태 조건 저장
            pref = getSharedPreferences(SharedPreference.WORKFORM_STATUS, MODE_PRIVATE);
            edit = pref.edit();
            edit.putString(SharedPreference.WORKFORM_STATUS, workFormStatus);
            edit.commit();
            finish();
        } else if (v == resetButton) {
            Toast.makeText(this, "초기화 버튼 눌림", Toast.LENGTH_LONG).show();
        } else if (v == regionButton) {
            Intent intent = new Intent(this, RegionActivity.class);
            startActivity(intent);
        } else if (v == jobButton) {
            Intent intent = new Intent(this, JobActivity.class);
            startActivity(intent);
        }
    }

    private void loadListData() { // 항목을 로드하는 함수
        ArrayList<Priority> contentsItems = new ArrayList<>();
        ArrayList<Priority> numItems = new ArrayList<>();

        contentsItems.add(new Priority(-1, "거리 짧은 순", Code.ViewType.PRIORITY_CONTENTS));
        contentsItems.add(new Priority(-1, "경력 우대 순", Code.ViewType.PRIORITY_CONTENTS));
        contentsItems.add(new Priority(-1, "희망 직종 순", Code.ViewType.PRIORITY_CONTENTS));
        contentsItems.add(new Priority(-1, "근무 형태 순", Code.ViewType.PRIORITY_CONTENTS));
        contentsItems.add(new Priority(-1, "요구 자격증 순", Code.ViewType.PRIORITY_CONTENTS));

        numItems.add(new Priority(1, null, Code.ViewType.PRIORITY_NUM));
        numItems.add(new Priority(2, null, Code.ViewType.PRIORITY_NUM));
        numItems.add(new Priority(3, null, Code.ViewType.PRIORITY_NUM));
        numItems.add(new Priority(4, null, Code.ViewType.PRIORITY_NUM));
        numItems.add(new Priority(5, null, Code.ViewType.PRIORITY_NUM));

        contentsAdapter.setItems(contentsItems);
        numAdapter.setItems(numItems);
    }
}