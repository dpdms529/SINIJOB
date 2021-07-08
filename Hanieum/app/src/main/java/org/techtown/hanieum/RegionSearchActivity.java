package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class RegionSearchActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_search);

        toolbar = findViewById(R.id.toolbar4);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        searchView.setMaxWidth(Integer.MAX_VALUE); // 최대 넓이
        searchView.setQueryHint("지역을 검색하세요"); // 검색 힌트
        searchView.setIconifiedByDefault(false); // 펼쳐서 보이기

        v.setBackgroundColor(Color.TRANSPARENT); // 밑줄 제거

        // 검색 아이콘 제거
        icon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        icon.setImageDrawable(null);

        return true;
    }
}