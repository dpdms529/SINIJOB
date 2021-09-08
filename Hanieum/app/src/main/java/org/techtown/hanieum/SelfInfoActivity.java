package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.techtown.hanieum.db.entity.CoverLetter;

public class SelfInfoActivity extends AppCompatActivity implements View.OnClickListener {

    Button saveButton;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_info);

        saveButton = findViewById(R.id.saveButton);
        addButton = findViewById(R.id.addButton);

        saveButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == saveButton) {
            finish();
        }else if(v == addButton) {
            Intent intent = new Intent(getApplicationContext(), CoverLetterActivity.class);
            startActivity(intent);
        }
    }
}