package org.techtown.hanieum;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class JobSearchActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView; // 검색 항목 리사이클러뷰
    SearchAdapter adapter; // 검색 항목 어댑터
    static ChipGroup chipGroup; // 선택 항목을 나타내는 ChipGroup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = findViewById(R.id.toolbar4);
        recyclerView = findViewById(R.id.searchView);
        chipGroup = findViewById(R.id.searchChipGroup);

        // 리사이클러뷰와 어댑터 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchAdapter();
        recyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    public boolean onCreateOptionsMenu(Menu menu) { // toolbar 검색
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchView searchView = (SearchView)menu.findItem(R.id.searchItem).getActionView();
        ImageView icon = (ImageView)searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        View v = searchView.findViewById(androidx.appcompat.R.id.search_plate);

        searchView.setMaxWidth(Integer.MAX_VALUE); // 최대 넓이
        searchView.setQueryHint("직종을 검색하세요"); // 검색 힌트
        searchView.setIconifiedByDefault(false); // 펼쳐서 보이기

        v.setBackgroundColor(Color.TRANSPARENT); // 밑줄 제거

        // 검색 아이콘 제거
        icon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        icon.setImageDrawable(null);

        return true;
    }

    private void loadListData() { // 항목을 로드하는 함수
        ArrayList<Search> items = new ArrayList<>();

        items.add(new Search("운전/배달 전체", false, Code.ViewType.JOB_SEARCH));
        items.add(new Search("대리운전/일반운전", false, Code.ViewType.JOB_SEARCH));
        items.add(new Search("택시/버스운전", false, Code.ViewType.JOB_SEARCH));

        adapter.setItems(items);
    }
}
