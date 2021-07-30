package org.techtown.hanieum;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.Bdong;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.techtown.hanieum.SharedPreference.getArrayPref;
import static org.techtown.hanieum.SharedPreference.setArrayPref;

public class RegionSearchActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView; // 검색 항목 리사이클러뷰
    static ChipGroup chipGroup; // 선택 항목을 나타내는 ChipGroup
    static SearchAdapter adapter; // 검색 항목 어댑터
    Context context;

    List<Bdong> bDong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        AppDatabase db = AppDatabase.getInstance(this);
        Log.e("BdongDatabase", "region data 조회");
        bDong = db.BdongDao().getAll();

        toolbar = findViewById(R.id.toolbar4);
        recyclerView = findViewById(R.id.searchView);
        chipGroup = findViewById(R.id.searchChipGroup);
        context = this;

        // 리사이클러뷰와 어댑터 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchAdapter();
        recyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadChip(this, chipGroup);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchView searchView = (SearchView)menu.findItem(R.id.searchItem).getActionView();
        ImageView icon = (ImageView)searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        View v = searchView.findViewById(androidx.appcompat.R.id.search_plate);

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("지역을 검색하세요");
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Search> items = new ArrayList<>();
                ArrayList<ChipList> chipList = getArrayPref(context, SharedPreference.REGION_LIST);

                for(int i=0; i<bDong.size(); i++) {
                    if(bDong.get(i).sido_name.contains(newText) || bDong.get(i).sigungu_name.contains(newText) || bDong.get(i).eupmyeondong_name.contains(newText)) {
                        items.add(new Search(bDong.get(i).sido_name + " " + bDong.get(i).sigungu_name + " " + bDong.get(i).eupmyeondong_name, Code.ViewType.REGION_SEARCH));
                    }
                }

                for(int i=0; i<items.size(); i++) {
                    for(int j=0; j<chipList.size(); j++) {
                        if(items.get(i).getTitle().equals(chipList.get(j).getName())) {
                            items.get(i).setChecked(true);
                        }
                    }
                }
                adapter.setItems(items);
                adapter.notifyDataSetChanged();

                return false;
            }
        });

        v.setBackgroundColor(Color.TRANSPARENT);

        icon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        icon.setImageDrawable(null);

        return true;
    }


    public static void loadChip(Context context, ChipGroup chipGroup) {
        chipGroup.removeAllViews();
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
                public void onClick(View v) {
                    for(int i=0;i<chipList.size();i++) {
                        if(chipList.get(i).getName().equals(name)) {
                            chipList.remove(i);

                            if((adapter.getItemCount() != 0) && name.equals(adapter.getItem(position).getTitle())) {
                                adapter.getItem(position).setChecked(false);
                                setArrayPref(context, chipList, SharedPreference.REGION_LIST);
                                adapter.notifyItemChanged(position);
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