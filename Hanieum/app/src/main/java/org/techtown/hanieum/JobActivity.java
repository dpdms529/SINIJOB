package org.techtown.hanieum;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Dao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.JobCategoryDao;
import org.techtown.hanieum.db.dao.RecruitDao;
import org.techtown.hanieum.db.entity.JobCategory;
import org.techtown.hanieum.db.entity.Recruit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class JobActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    Button jobSearchButton; // 직종 검색 화면으로 이동하는 버튼
    Button resetButton; // 선택한 칩을 리셋하는 버튼
    Button okButton;    // 확인 버튼
    RecyclerView jobView1; // 직종 분류(1차) 리사이클러뷰
    RecyclerView jobView2; // 직종 분류(2차) 리사이클러뷰
    static ChipGroup chipGroup; // 선택한 직종을 나타내기 위한 ChipGroup
    JobAdapter adapter1; // 직종 분류(1차) 어댑터
    static JobAdapter adapter2; // 직종 분류(2차) 어댑터
    Context context;
    static SharedPreference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        pref = new SharedPreference(getApplicationContext());

        AppDatabase db = AppDatabase.getInstance(this);
        Log.e("JobDatabase","job data 조회");
        List<JobCategory> category = null;
        try {
            category = new JobGetCategoryAsyncTask(db.jobCategoryDao()).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        db.jobCategoryDao().getCategory();

        toolbar = findViewById(R.id.toolbar5);
        jobSearchButton = findViewById(R.id.jobSearchButton);
        resetButton = findViewById(R.id.resetButton);
        okButton = findViewById(R.id.okButton);
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

        context = this;
        List<JobCategory> finalCategory = category;
        adapter1.setItemClickListener(new OnJobItemClickListener() {
            @Override
            public void OnItemClick(JobAdapter.Job1ViewHolder holder, View view, int position) {
                Job job = adapter1.getItem(position);
                ArrayList<Job> items = new ArrayList<>();
                ArrayList<ChipList> chipList = pref.getArrayPref(SharedPreference.JOB_TMP);

                items.add(new Job(job.getCode(), job.getJob1()+" 전체", job.getCode(), Code.ViewType.JOB2));
                for (int i = 0; i< finalCategory.size(); i++) {
                    if (job.getJob2().equals(finalCategory.get(i).primary_cate_code)) {
                        items.add(new Job(finalCategory.get(i).primary_cate_code, finalCategory.get(i).category_name, finalCategory.get(i).category_code, Code.ViewType.JOB2));
                    }
                }

                // 아이템과 선택된 칩의 이름이 같으면 아이템의 setSelected를 true로 설정
                for (int i=0; i<items.size(); i++) {
                    for (int j=0; j<chipList.size(); j++) {
                        if (items.get(i).getJob2().equals(chipList.get(j).getName())) {
                            items.get(i).setSelected(true);
                        }
                    }
                }

                adapter2.setItems(items);
                adapter2.notifyDataSetChanged();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        jobSearchButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        okButton.setOnClickListener(this);

        loadListData();
        loadChip(context, chipGroup);

        setResult(Activity.RESULT_OK);
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
            launcher.launch(intent);
        } else if (v == resetButton) {
            ArrayList<ChipList> chipList = new ArrayList<>();
            pref.setArrayPref(chipList, SharedPreference.JOB_TMP);
            loadChip(context, chipGroup);
            loadListData();
        } else if (v == okButton) {
            finish();
        }
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        loadChip(context, chipGroup);
                        loadListData();
                    }
                }
            });

    private void loadListData() { // 항목을 로드하는 함수
        ArrayList<Job> items1 = new ArrayList<>();
        ArrayList<Job> items2 = new ArrayList<>();
        AppDatabase db = AppDatabase.getInstance(this);
        List<JobCategory> category = null;
        try {
            category = new JobGetCategoryAsyncTask(db.jobCategoryDao()).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        db.jobCategoryDao().getCategory();

        JobAdapter.lastSelectedPosition1 = -1;
        for (int i=0; i<category.size(); i++) {
            if (category.get(i).category_code.length() == 2) {
                items1.add(new Job(category.get(i).category_name, category.get(i).category_code, category.get(i).category_code, Code.ViewType.JOB1));
            }
        }

        adapter1.setItems(items1);
        adapter1.notifyDataSetChanged();
        adapter2.setItems(items2); // 어댑터2를 빈 상태로 둠
    }

    public static void loadChip(Context context, ChipGroup chipGroup) { // 선택된 칩을 불러오는 함수
        chipGroup.removeAllViews(); // 칩그룹 초기화
        ArrayList<ChipList> chipList = pref.getArrayPref(SharedPreference.JOB_TMP);

        for (int i=chipList.size()-1;i>=0;i--) { // chipList에 있는 것을 추가
            String name = chipList.get(i).getName();
            int position = chipList.get(i).getPosition();

            Chip chip = new Chip(context);
            chip.setText(name);
            chip.setTextSize(17);
            chip.setCloseIconResource(R.drawable.close);
            chip.setCloseIconVisible(true);
            chipGroup.addView(chip);
            chip.setOnCloseIconClickListener(new View.OnClickListener() { // 삭제 클릭 시
                @Override
                public void onClick(View v) {
                    // 아이템 삭제 코드
                    for (int i=0; i<chipList.size(); i++) {
                        if (chipList.get(i).getName().equals(name)) {
                            chipList.remove(i);
                            // 1차 직종이 선택된 상태이고 삭제되는 칩 이름과 현재 표시된 2차 직종(position)의 이름이 같으면
                            if ((adapter2.getItemCount()!=0) && name.equals(adapter2.getItem(position).getJob2())) {
                                adapter2.getItem(position).setSelected(false);
                                pref.setArrayPref(chipList, SharedPreference.JOB_TMP);
                                adapter2.notifyItemChanged(position);
                            } else {
                                pref.setArrayPref(chipList, SharedPreference.JOB_TMP);
                            }
                        }
                    }
                    chipGroup.removeView(chip);
                }
            });
        }
    }

    public static class JobGetCategoryAsyncTask extends AsyncTask<Void,Void,List<JobCategory>> {
        private JobCategoryDao mJobCategoryDao;

        public JobGetCategoryAsyncTask(JobCategoryDao jobCategoryDao){
            this.mJobCategoryDao = jobCategoryDao;
        }

        @Override
        protected List<JobCategory> doInBackground(Void... voids) {
            return mJobCategoryDao.getCategory();
        }
    }
}