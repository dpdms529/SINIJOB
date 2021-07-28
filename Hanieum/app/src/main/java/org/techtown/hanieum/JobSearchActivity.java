package org.techtown.hanieum;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.JobCategory;

import java.util.ArrayList;
import java.util.List;

import static org.techtown.hanieum.SharedPreference.getArrayPref;
import static org.techtown.hanieum.SharedPreference.setArrayPref;

public class JobSearchActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView; // 검색 항목 리사이클러뷰
    static SearchAdapter adapter; // 검색 항목 어댑터
    static ChipGroup chipGroup; // 선택 항목을 나타내는 ChipGroup
    Context context;

    List<JobCategory> category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        AppDatabase db = AppDatabase.getInstance(this);
        Log.e("JobDatabase","job data 조회");
        category = db.jobCategoryDao().getAll();

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

//        loadListData();
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
    public boolean onCreateOptionsMenu(Menu menu) { // toolbar 검색
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchView searchView = (SearchView)menu.findItem(R.id.searchItem).getActionView();
        ImageView icon = (ImageView)searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        View v = searchView.findViewById(androidx.appcompat.R.id.search_plate);

        searchView.setMaxWidth(Integer.MAX_VALUE); // 최대 넓이
        searchView.setQueryHint("직종을 검색하세요"); // 검색 힌트
        searchView.setIconifiedByDefault(false); // 펼쳐서 보이기
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                ArrayList<Search> items = new ArrayList<>();
//
//                for (int i=0; i<category.size(); i++) {
//                    if (category.get(i).category_code.length() != 2 && category.get(i).category_name.contains(query)) {
//                        items.add(new Search(category.get(i).category_name, Code.ViewType.JOB_SEARCH));
//                    }
//                }
//
//                adapter.setItems(items);
//                adapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Search> items = new ArrayList<>();
                ArrayList<ChipList> chipList = getArrayPref(context, SharedPreference.JOB_LIST);

                for (int i=0; i<category.size(); i++) {
                    if (category.get(i).category_code.length() != 2 && category.get(i).category_name.contains(newText)) {
                        items.add(new Search(category.get(i).category_name, Code.ViewType.JOB_SEARCH));
                    }
                }

                // 아이템과 선택된 칩의 이름이 같으면 아이템의 setChecked true로 설정
                for (int i=0; i<items.size(); i++) {
                    for (int j=0; j<chipList.size(); j++) {
                        if (items.get(i).getTitle().equals(chipList.get(j).getName())) {
                            items.get(i).setChecked(true);
                        }
                    }
                }

                adapter.setItems(items);
                adapter.notifyDataSetChanged();

                return false;
            }
        });

        v.setBackgroundColor(Color.TRANSPARENT); // 밑줄 제거

        // 검색 아이콘 제거
        icon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        icon.setImageDrawable(null);

        return true;
    }

    public static void loadChip(Context context, ChipGroup chipGroup) { // 선택된 칩을 불러오는 함수
        chipGroup.removeAllViews(); // 칩그룹 초기화
        ArrayList<ChipList> chipList = getArrayPref(context, SharedPreference.JOB_LIST);

        for (int i=0;i<chipList.size();i++) { // chipList에 있는 것을 추가
            String name = chipList.get(i).getName();
            int position = chipList.get(i).getPosition();

            Chip chip = new Chip(context);
            chip.setText(name);
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

                            if ((adapter.getItemCount()!=0) && name.equals(adapter.getItem(position).getTitle())) {
                                adapter.getItem(position).setChecked(false);
                                setArrayPref(context, chipList, SharedPreference.JOB_LIST);
                                adapter.notifyItemChanged(position);
                            } else {
                                setArrayPref(context, chipList, SharedPreference.JOB_LIST);
                            }

                        }
                    }
                    chipGroup.removeView(chip);
                }
            });
        }
    }
}
