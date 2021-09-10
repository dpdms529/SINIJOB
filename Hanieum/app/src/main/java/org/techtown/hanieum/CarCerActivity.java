package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class CarCerActivity extends AppCompatActivity implements View.OnClickListener {

    Button saveButton;
    Button addButton;
    RecyclerView recyclerView;
    TextView title;

    CareerAdapter adapter1;
    CertifiAdapter adapter2;

    private int careerNum;
    private int certifiNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_cer);

        saveButton = findViewById(R.id.saveButton);
        addButton = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.title);

        Intent intent = getIntent();
        if (intent.getStringExtra("type").equals("career")) {   // 경력사항
            title.setText("경력사항");
            LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager1);
            adapter1 = new CareerAdapter();
            adapter1.setItems(new ArrayList<>());
            recyclerView.setAdapter(adapter1);
        } else {    // 보유자격증
            title.setText("보유자격증");
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager2);
            adapter2 = new CertifiAdapter();
            adapter2.setItems(new ArrayList<>());
            recyclerView.setAdapter(adapter2);
        }

        // 경력, 자격증 등록된 개수를 가져오는 코드
        careerNum = 0;  // 수정해야함
        certifiNum = 0; // 수정해야함

        saveButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == saveButton) {
            finish();
        } else if (v == addButton) {
            if (title.getText().equals("경력사항") && careerNum<10) {
                careerNum++;
                adapter1.addItem(new Career("", "", "", "", ""));
                adapter1.notifyDataSetChanged();
            } else if (title.getText().equals("보유자격증") && certifiNum<10) {
                certifiNum++;
                adapter2.addItem(new Certificate("", ""));
                adapter2.notifyDataSetChanged();
            }
        }
    }
}