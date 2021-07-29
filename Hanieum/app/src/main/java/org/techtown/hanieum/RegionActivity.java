package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.chip.ChipGroup;

import org.techtown.hanieum.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class RegionActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    Button regionSearchButton; // 지역 검색 화면으로 이동하는 버튼
    RecyclerView regionView1; // 지역 분류(시/도) 리사이클러뷰
    RecyclerView regionView2; // 지역 분류(구/군/시) 리사이클러뷰
    RecyclerView regionView3; // 지역 분류(동/읍/면) 리사이클러뷰
    RegionAdapter adapter1; // 지역 분류(시/도) 어댑터
    RegionAdapter adapter2; // 지역 분류(구/군/시) 어댑터
    RegionAdapter adapter3; // 지역 분류(동/읍/면) 어댑터
    static ChipGroup chipGroup; // 선택한 지역을 나타내기 위한 ChipGroup


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);

        toolbar = findViewById(R.id.toolbar3);
        regionSearchButton = findViewById(R.id.regionSearchButton);
        regionView1 = findViewById(R.id.regionView1);
        regionView2 = findViewById(R.id.regionView2);
        regionView3 = findViewById(R.id.regionView3);
        chipGroup = findViewById(R.id.regionChipGroup);

        // 리사이클러뷰와 어댑터 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        regionView1.setLayoutManager(layoutManager);
        regionView2.setLayoutManager(layoutManager2);
        regionView3.setLayoutManager(layoutManager3);
        adapter1 = new RegionAdapter();
        adapter2 = new RegionAdapter();
        adapter3 = new RegionAdapter();
        regionView1.setAdapter(adapter1);
        regionView2.setAdapter(adapter2);
        regionView3.setAdapter(adapter3);

        AppDatabase db = AppDatabase.getInstance(this);

        adapter1.setRegion1ClickListener(new OnRegion1ItemClickListener() {
            @Override
            public void OnRegion1Click(RegionAdapter.Region1ViewHolder holder, View view, int position) {
                Region item = adapter1.getItem(position);
                ArrayList<Region> items2 = new ArrayList<>();

                List<String> item2 = db.BdongDao().getsigungu(item.getRegion1());
                for(int i=0;i<item2.size();i++) {
                    items2.add(new Region(item.getRegion1(), item2.get(i), null, Code.ViewType.REGION2));
                }
                adapter2.setItems(items2);
                adapter2.notifyDataSetChanged();
            }
        });

        adapter2.setRegion2ClickListener(new OnRegion2ItemClickListener() {
            @Override
            public void OnRegion2Click(RegionAdapter.Region2ViewHolder holder, View view, int position) {
                Region item = adapter2.getItem(position);
                ArrayList<Region> items3 = new ArrayList<>();

                List<String> item3 = db.BdongDao().geteupmyeondong(item.getRegion1(), item.getRegion2());
                for(int i=0;i<item3.size();i++)
                {
                    items3.add(new Region(item.getRegion1(), item.getRegion2(), item3.get(i), Code.ViewType.REGION3));
                }
                adapter3.setItems(items3);
                adapter3.notifyDataSetChanged();

            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        regionSearchButton.setOnClickListener(this);

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
        if (v == regionSearchButton) {
            Intent intent = new Intent(this, RegionSearchActivity.class);
            startActivity(intent);
        }
    }


    private void loadListData() { // 항목을 로드하는 함수
        ArrayList<Region> items1 = new ArrayList<>();



        items1.add(new Region("전체", null, null, Code.ViewType.REGION1));

        AppDatabase db = AppDatabase.getInstance(this);
        List<String> item1 = db.BdongDao().getsido();
        for(int i=0;i<item1.size();i++) {
            items1.add(new Region(item1.get(i), null, null, Code.ViewType.REGION1));
        }

        /*
        items1.add(new Region("전체", null, null, Code.ViewType.REGION1));
        items1.add(new Region("서울", null, null, Code.ViewType.REGION1));
        items1.add(new Region("경기", null, null, Code.ViewType.REGION1));
        items1.add(new Region("인천", null, null, Code.ViewType.REGION1));
        items1.add(new Region("부산", null, null, Code.ViewType.REGION1));
        items1.add(new Region("대구", null, null, Code.ViewType.REGION1));
        items1.add(new Region("광주", null, null, Code.ViewType.REGION1));
        items1.add(new Region("대전", null, null, Code.ViewType.REGION1));
        items1.add(new Region("울산", null, null, Code.ViewType.REGION1));
        items1.add(new Region("강원", null, null, Code.ViewType.REGION1));
        items1.add(new Region("경남", null, null, Code.ViewType.REGION1));
        items1.add(new Region("경북", null, null, Code.ViewType.REGION1));
        items1.add(new Region("전남", null, null, Code.ViewType.REGION1));
        items1.add(new Region("전북", null, null, Code.ViewType.REGION1));
        items1.add(new Region("충남", null, null, Code.ViewType.REGION1));
        items1.add(new Region("충북", null, null, Code.ViewType.REGION1));
        items1.add(new Region("제주", null, null, Code.ViewType.REGION1));
        items1.add(new Region("세종", null, null, Code.ViewType.REGION1));

        items2.add(new Region("전북", "전체", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "고창군", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "군산시", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "김제시", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "남원시", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "무주군", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "부안군", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "순창군", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "완주군", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "익산시", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "임실군", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "장수군", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "전주시 덕진구", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "전주시 완산구", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "정읍시", null, Code.ViewType.REGION2));
        items2.add(new Region("전북", "진안군", null, Code.ViewType.REGION2));

        items3.add(new Region("전북", "전주시 덕진구", "강흥동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "고랑동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "금상동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "금암동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "남정동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "덕진동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "도덕동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "도도동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "동산동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "만성동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "반월동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "산정동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "성덕동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "송천동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "여의동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "용정동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "우아동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "원동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "인후동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "장동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "전미동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "중동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "진북동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "팔복동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "호성동", Code.ViewType.REGION3));
        items3.add(new Region("전북", "전주시 덕진구", "화전동", Code.ViewType.REGION3));
        */


        adapter1.setItems(items1);
        adapter1.notifyDataSetChanged();
    }
}