package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class FilteringActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    Button saveButton; // 저장 버튼
    Button resetButton; // 초기화 버튼
    Button regionButton; // 지역 선택 화면으로 이동하는 버튼
    Button jobButton; // 직종 선택 화면으로 이동하는 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);

        toolbar = findViewById(R.id.toolbar2);
        saveButton = findViewById(R.id.saveButton);
        resetButton = findViewById(R.id.resetButton);
        regionButton = findViewById(R.id.regionButton);
        jobButton = findViewById(R.id.jobButton);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기

        saveButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        regionButton.setOnClickListener(this);
        jobButton.setOnClickListener(this);
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
        if (v == saveButton) {
            finish();
        } else if (v == resetButton) {
            Toast.makeText(this, "초기화 버튼 눌림", Toast.LENGTH_LONG).show();
        } else if (v == regionButton) {
            Intent intent = new Intent(this, RegionActivity.class);
            startActivity(intent);
        } else if (v == jobButton) {
            Intent intent = new Intent(this, JobActivity.class);
            startActivity(intent);
        }
    }
}