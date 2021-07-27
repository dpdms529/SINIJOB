package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        adapter1.setItemClickListener(new OnJobItemClickListener() {
            @Override
            public void OnItemClick(JobAdapter.Job1ViewHolder holder, View view, int position) {
                Job item = adapter1.getItem(position);
                ArrayList<Job> items2 = new ArrayList<>();

                String job = "http://3.36.129.83/job.php";
                URLConnector task = new URLConnector(job);

                task.start();

                try {
                    task.join();
                    System.out.println("Waiting...for result");
                }
                catch(InterruptedException e) {
                }

                String result = task.getResult();

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        String primary_cate_code = jsonObject1.getString("primary_cate_code");
                        String category_name = jsonObject1.getString("category_name");

                        if (item.getJob2().equals(primary_cate_code)) {
                            items2.add(new Job(primary_cate_code, category_name, Code.ViewType.JOB2));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter2.setItems(items2);
                adapter2.notifyDataSetChanged();
            }
        });

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
//        ArrayList<Job> items2 = new ArrayList<>();

        String job = "http://3.36.129.83/job.php";
        URLConnector task = new URLConnector(job);

        task.start();

        try {
            task.join();
            System.out.println("Waiting...for result");
        }
        catch(InterruptedException e) {

        }

        String result = task.getResult();

        try {
            JSONObject jsonObject = new JSONObject(result);

            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i=0; i<jsonArray.length(); i++)
            {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                String category_code = jsonObject1.getString("category_code");
                String primary_cate_code = jsonObject1.getString("primary_cate_code");
                String category_name = jsonObject1.getString("category_name");

                if (category_code.length() == 2) {
                    items1.add(new Job(category_name, category_code, Code.ViewType.JOB1));
                } else {
//                    items2.add(new Job(primary_cate_code, category_name, Code.ViewType.JOB2));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 여기서 items for문 돌고 찾기

        adapter1.setItems(items1);
//        adapter2.setItems(items2);
    }
}