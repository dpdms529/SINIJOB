package org.techtown.hanieum;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.techtown.hanieum.db.AppDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.techtown.hanieum.SharedPreference.getArrayPref;
import static org.techtown.hanieum.SharedPreference.setArrayPref;

public class RegionActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    Button regionSearchButton; // 지역 검색 화면으로 이동하는 버튼
    RecyclerView regionView1; // 지역 분류(시/도) 리사이클러뷰
    RecyclerView regionView2; // 지역 분류(구/군/시) 리사이클러뷰
    RecyclerView regionView3; // 지역 분류(동/읍/면) 리사이클러뷰
    RegionAdapter adapter1; // 지역 분류(시/도) 어댑터
    RegionAdapter adapter2; // 지역 분류(구/군/시) 어댑터
    static RegionAdapter adapter3; // 지역 분류(동/읍/면) 어댑터
    static ChipGroup chipGroup; // 선택한 지역을 나타내기 위한 ChipGroup
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);

        AppDatabase db = AppDatabase.getInstance(this);

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

        context = this;
        adapter1.setRegion1ClickListener(new OnRegion1ItemClickListener() {
            @Override
            public void OnRegion1Click(RegionAdapter.Region1ViewHolder holder, View view, int position) {
                Region region1 = adapter1.getItem(position);
                ArrayList<Region> items2 = new ArrayList<>();
                ArrayList<ChipList> chipList = getArrayPref(context, SharedPreference.REGION_LIST);

                List<String> item2 = db.BdongDao().getsigungu(region1.getRegion1());
                for(int i=0;i<item2.size();i++) {
                    items2.add(new Region(region1.getRegion1(), item2.get(i), null, "0" , Code.ViewType.REGION2));
                }

                for(int i=0;i<items2.size();i++) {
                    for(int j=0;j<chipList.size();j++) {
                        if(items2.get(i).equals(chipList.get(j).getName())) {
                            items2.get(i).setSelected(true);
                        }
                    }
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
                ArrayList<ChipList> chipList = getArrayPref(context, SharedPreference.REGION_LIST);

                List<String> item3 = db.BdongDao().geteupmyeondong(item.getRegion1(), item.getRegion2());
                String bDongCode = db.BdongDao().getBDongCode(item.getRegion1(), item.getRegion2(), item.getRegion3());
                Log.e("RegionDatabase", item3.get(0));
                for(int i=0;i<item3.size();i++)
                {
                    items3.add(new Region(item.getRegion1(), item.getRegion2(), item3.get(i), bDongCode, Code.ViewType.REGION3));
                }

                for(int i=0;i< items3.size();i++) {
                    for(int j=0;j<chipList.size();j++) {
                        if(items3.get(i).equals(chipList.get(j).getName())) {
                            items3.get(i).setSelected(true);
                        }
                    }
                }
                adapter3.setItems(items3);
                adapter3.notifyDataSetChanged();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        regionSearchButton.setOnClickListener(this);

        loadListData();
        loadChip(context, chipGroup);
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
            launcher.launch(intent);
        }
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        loadChip(context, chipGroup);
                    }
                }
            });


    private void loadListData() { // 항목을 로드하는 함수
        ArrayList<Region> items1 = new ArrayList<>();
        AppDatabase db = AppDatabase.getInstance(this);
        List<String> item1 = db.BdongDao().getsido();

        for(int i=0;i<item1.size();i++) {
            items1.add(new Region(item1.get(i), null, null, "0" , Code.ViewType.REGION1));
        }

        adapter1.setItems(items1);
    }

    public static void loadChip(Context context, ChipGroup chipGroup) {
        chipGroup.removeAllViews(); //칩그룹 초기화
        ArrayList<ChipList> chipList = getArrayPref(context, SharedPreference.REGION_LIST);

        for(int i=0;i<chipList.size();i++) {
            String name = chipList.get(i).getName();
            int position = chipList.get(i).getPosition();

            Chip chip = new Chip(context);
            chip.setText(name);
            chip.setCloseIconResource(R.drawable.close);
            chip.setCloseIconVisible(true);
            chipGroup.addView(chip);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //아이템 삭제
                    for(int i=0;i<chipList.size();i++) {
                        if(chipList.get(i).getName().equals(name)) {
                            chipList.remove(i);
                            if((adapter3.getItemCount() != 0) && name.equals(adapter3.getItem(position).getRegion3())) {
                                adapter3.getItem(position).setSelected(false);
                                setArrayPref(context, chipList, SharedPreference.REGION_LIST);
                                adapter3.notifyItemChanged(position);
                            } else {
                                setArrayPref(context, chipList, SharedPreference.REGION_LIST);
                            }
                        }
                    }
                    chipGroup.removeView(chip);
                }
            });
        }
    }
}