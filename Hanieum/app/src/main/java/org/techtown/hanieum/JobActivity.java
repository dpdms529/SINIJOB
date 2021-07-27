package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.chip.ChipGroup;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.JobCategory;

import java.util.ArrayList;
import java.util.List;

public class JobActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    Button jobSearchButton; // 직종 검색 화면으로 이동하는 버튼
    RecyclerView jobView1; // 직종 분류(1차) 리사이클러뷰
    RecyclerView jobView2; // 직종 분류(2차) 리사이클러뷰
    static ChipGroup chipGroup; // 선택한 직종을 나타내기 위한 ChipGroup
    JobAdapter adapter1; // 직종 분류(1차) 어댑터
    JobAdapter adapter2; // 직종 분류(2차) 어댑터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        AppDatabase db = AppDatabase.getInstance(this);
        Log.e("JobDatabase","job data 조회");
        List<JobCategory> item = db.jobCategoryDao().getAll();
        Log.e("JobDatabase",item.get(0).category_name);

        toolbar = findViewById(R.id.toolbar5);
        jobSearchButton = findViewById(R.id.jobSearchButton);
        jobView1 = findViewById(R.id.jobView1);
        jobView2 = findViewById(R.id.jobView2);
        chipGroup = findViewById(R.id.jobChipGroup);

        // 리사이클러뷰와 어댑터 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        jobView1.setLayoutManager(layoutManager);
        jobView2.setLayoutManager(layoutManager2);
        adapter1 = new JobAdapter();
        adapter2 = new JobAdapter();
        jobView1.setAdapter(adapter1);
        jobView2.setAdapter(adapter2);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        jobSearchButton.setOnClickListener(this);

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
        if (v == jobSearchButton) {
            Intent intent = new Intent(this, JobSearchActivity.class);
            startActivity(intent);
        }
    }

    private void loadListData() { // 항목을 로드하는 함수
        ArrayList<Job> items1 = new ArrayList<>();
        ArrayList<Job> items2 = new ArrayList<>();

        items1.add(new Job("전체", "null", Code.ViewType.JOB1));
        items1.add(new Job("외식/음료", "null", Code.ViewType.JOB1));
        items1.add(new Job("유통/판매", "null", Code.ViewType.JOB1));
        items1.add(new Job("운전/배달", "null", Code.ViewType.JOB1));

        items2.add(new Job("운전/배달", "전체", Code.ViewType.JOB2));
        items2.add(new Job("운전/배달", "운반/이사", Code.ViewType.JOB2));
        items2.add(new Job("운전/배달", "대리운전/일반운전", Code.ViewType.JOB2));
        items2.add(new Job("운전/배달", "택시/버스운전", Code.ViewType.JOB2));
        items2.add(new Job("운전/배달", "수행기사", Code.ViewType.JOB2));

        adapter1.setItems(items1);
        adapter2.setItems(items2);
    }
}