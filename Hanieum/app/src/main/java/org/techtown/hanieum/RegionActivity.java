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

import java.util.ArrayList;
import java.util.List;

public class RegionActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    Button regionSearchButton; // 지역 검색 화면으로 이동하는 버튼
    Button resetButton; // 선택한 칩을 리셋하는 버튼
    Button okButton;    // 확인 버튼
    RecyclerView regionView1; // 지역 분류(시/도) 리사이클러뷰
    RecyclerView regionView2; // 지역 분류(구/군/시) 리사이클러뷰
    RecyclerView regionView3; // 지역 분류(동/읍/면) 리사이클러뷰
    RegionAdapter adapter1; // 지역 분류(시/도) 어댑터
    RegionAdapter adapter2; // 지역 분류(구/군/시) 어댑터
    static RegionAdapter adapter3; // 지역 분류(동/읍/면) 어댑터
    static ChipGroup chipGroup; // 선택한 지역을 나타내기 위한 ChipGroup
    Context context;
    static SharedPreference pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);

        pref = new SharedPreference(getApplicationContext());

        AppDatabase db = AppDatabase.getInstance(this);

        toolbar = findViewById(R.id.toolbar3);
        regionSearchButton = findViewById(R.id.regionSearchButton);
        okButton = findViewById(R.id.okButton);
        resetButton = findViewById(R.id.resetButton);
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
                ArrayList<Region> region2 = new ArrayList<>();
                ArrayList<Region> items3 = new ArrayList<>();
                ArrayList<ChipList> chipList = pref.getArrayPref(SharedPreference.REGION_TMP);
                String sidoTotalCode = db.BdongDao().getTotalSidoCode(region1.getRegion1());

                region2.add(new Region(region1.getRegion1(), "전체","", sidoTotalCode,Code.ViewType.REGION2));
                List<String> item2 = db.BdongDao().getsigungu(region1.getRegion1());
                for(int i=0;i<item2.size();i++) {
                    region2.add(new Region(region1.getRegion1(), item2.get(i), null, "0" , Code.ViewType.REGION2));
                }

                for(int i=0;i<chipList.size();i++) {
                    if(chipList.get(i).getName().equals(region2.get(0).getRegion1() + " " + region2.get(0).getRegion2())) { // 칩리스트에 전체 항목이 있으면
                        region2.get(0).setSelected(true); // region2의 전체 항목에 색칠
                        RegionAdapter.lastSelectedPosition2 = 0;
                    } else {
                        RegionAdapter.lastSelectedPosition2 = -1;
                    }
                }


                adapter3.setItems(items3);

                adapter2.setItems(region2);
                adapter2.notifyDataSetChanged();
            }
        });

        adapter2.setRegion2ClickListener(new OnRegion2ItemClickListener() {
            @Override
            public void OnRegion2Click(RegionAdapter.Region2ViewHolder holder, View view, int position) {
                Region item = adapter2.getItem(position);
                ArrayList<Region> items3 = new ArrayList<>();
                ArrayList<ChipList> chipList = pref.getArrayPref(SharedPreference.REGION_TMP);

                List<String> item3 = db.BdongDao().geteupmyeondong(item.getRegion1(), item.getRegion2());
                String totalSigunguCode = db.BdongDao().getTotalSigunguCode(item.getRegion1(), item.getRegion2());
                if(item3.size() == 0) { // 전체를 선택했을 때
                    adapter3.setItems(new ArrayList<Region>()); // 어댑터 3을 빈 상태로 둠
                } else {
                    items3.add(new Region(item.getRegion1(), item.getRegion2(), "전체", totalSigunguCode, Code.ViewType.REGION3));
                    for (int i = 0; i < item3.size(); i++) {
                        String bDongCode = db.BdongDao().getBDongCode(item.getRegion1(), item.getRegion2(), item3.get(i));
                        items3.add(new Region(item.getRegion1(), item.getRegion2(), item3.get(i), bDongCode, Code.ViewType.REGION3));
                    }
                }

                for(int i=0;i< items3.size();i++) {
                    for(int j=0;j<chipList.size();j++) {
                        String tmp = items3.get(i).getRegion1() + " " + items3.get(i).getRegion2() + " " +
                                items3.get(i).getRegion3();
                        if(tmp.equals(chipList.get(j).getName())) {
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
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        regionSearchButton.setOnClickListener(this);
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
        if (v == regionSearchButton) {
            Intent intent = new Intent(this, RegionSearchActivity.class);
            launcher.launch(intent);
        } else if (v == resetButton) {
            ArrayList<ChipList> chipList = new ArrayList<>();
            pref.setArrayPref(chipList, SharedPreference.REGION_TMP);
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
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        loadChip(context, chipGroup);
                        loadListData();
                    }
                }
            });


    private void loadListData() { // 항목을 로드하는 함수
        ArrayList<Region> items1 = new ArrayList<>();
        ArrayList<Region> items2 = new ArrayList<>();
        ArrayList<Region> items3 = new ArrayList<>();
        AppDatabase db = AppDatabase.getInstance(this);
        List<String> item1 = db.BdongDao().getsido();

        RegionAdapter.lastSelectedPosition1 = -1;
        RegionAdapter.lastSelectedPosition2 = -1;

        for(int i=0;i<item1.size();i++) {
            items1.add(new Region(item1.get(i), null, null, "0" , Code.ViewType.REGION1));
        }

        adapter1.setItems(items1);
        adapter1.notifyDataSetChanged();
        adapter2.setItems(items2); // 어댑터2를 빈 상태로 둠
        adapter3.setItems(items3); // 어댑터3을 빈 상태로 둠
    }

    public static void loadChip(Context context, ChipGroup chipGroup) {
        chipGroup.removeAllViews(); //칩그룹 초기화
        ArrayList<ChipList> chipList = pref.getArrayPref(SharedPreference.REGION_TMP);

        for(int i=chipList.size()-1;i>=0;i--) { // chipList에 있는 것을 추가가
            String name = chipList.get(i).getName();
            int position = chipList.get(i).getPosition();

            Chip chip = new Chip(context);
            chip.setText(name);
            chip.setTextSize(17);
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
                            if((adapter3.getItemCount() != 0) && name.contains(adapter3.getItem(position).getRegion3())) {
                                adapter3.getItem(position).setSelected(false);
                                pref.setArrayPref(chipList, SharedPreference.REGION_TMP);
                                adapter3.notifyItemChanged(position);
                            } else {
                                pref.setArrayPref(chipList, SharedPreference.REGION_TMP);
                            }
                        }
                    }
                    chipGroup.removeView(chip);
                }
            });
        }
    }
}
