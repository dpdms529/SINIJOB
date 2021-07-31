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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import static org.techtown.hanieum.SharedPreference.WORKFORM_LIST;
import static org.techtown.hanieum.SharedPreference.getArrayPref;
import static org.techtown.hanieum.SharedPreference.getWorkFormPref;
import static org.techtown.hanieum.SharedPreference.setWorkFormPref;

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
    RadioGroup careerGroup;
    RadioGroup licenseGroup;
    RadioButton noCareerButton;
    RadioButton yesCareerButton;
    RadioButton noLicenseButton;
    RadioButton yesLicenseButton;
    CheckBox workFormCheckBox1; // 정규직 체크박스
    CheckBox workFormCheckBox2; // 계약직 체크박스
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    String careerStatus = "0";
    String licenseStatus = "0";
    Context context;

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
        noCareerButton = findViewById(R.id.noCareer);
        yesCareerButton = findViewById(R.id.yesCareer);
        noLicenseButton = findViewById(R.id.noLicense);
        yesLicenseButton = findViewById(R.id.yesLicense);
        workFormCheckBox1 = findViewById(R.id.workFormCheck1);
        workFormCheckBox2 = findViewById(R.id.workFormCheck2);
        context = this;

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 경력 조건 저장
                pref = getSharedPreferences(SharedPreference.CAREER_STATUS, 0);
                edit = pref.edit();
                edit.putString(SharedPreference.CAREER_STATUS, careerStatus);
                edit.commit();

                // 자격증 조건 저장
                pref = getSharedPreferences(SharedPreference.LICENSE_STATUS, 0);
                edit = pref.edit();
                edit.putString(SharedPreference.LICENSE_STATUS, "1");
                edit.commit();

                Toast.makeText(getApplicationContext(),"저장 완료",Toast.LENGTH_SHORT).show();

            }

        });

        careerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.noCareer) { // 적용 안함을 선택하면
                    careerStatus = "0";
//                    pref = getSharedPreferences(SharedPreference.CAREER_STATUS, 0);
//                    edit = pref.edit();
//                    edit.putString(SharedPreference.CAREER_STATUS, "0");
//                    edit.commit();
                    Toast.makeText(FilteringActivity.this, "noCareer",Toast.LENGTH_SHORT).show();
                } else if(i == R.id.yesCareer) { // 나의 경력 적용(이력서)를 선택하면
                    careerStatus = "1";
//                    pref = getSharedPreferences(SharedPreference.CAREER_STATUS, 0);
//                    edit = pref.edit();
//                    edit.putString(SharedPreference.CAREER_STATUS, "1");
//                    edit.commit();
                }
            }
        });

        licenseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.noLicense) { // 적용 안함을 선택하면
                    licenseStatus = "0";
//                    pref = getSharedPreferences(SharedPreference.LICENSE_STATUS, 0);
//                    edit = pref.edit();
//                    edit.putString(SharedPreference.LICENSE_STATUS, "0");
//                    edit.commit();
                } else if(i == R.id.yesLicense) { // 나의 자격증 적용(이력서)를 선택하면
                    licenseStatus = "1";
//                    pref = getSharedPreferences(SharedPreference.LICENSE_STATUS, 0);
//                    edit = pref.edit();
//                    edit.putString(SharedPreference.LICENSE_STATUS, "1");
//                    edit.commit();
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
        if (v == saveButton) {
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