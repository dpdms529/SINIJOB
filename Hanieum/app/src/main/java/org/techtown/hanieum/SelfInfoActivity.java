package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.techtown.hanieum.db.entity.CoverLetter;

import java.util.ArrayList;
import java.util.List;

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
            List<CharSequence> items = new ArrayList<>();
            items.add("영상");
            items.add("일반");
            CharSequence[] charSequences = items.toArray(new CharSequence[items.size()]);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            alertDialog.setTitle("자기소개서 유형을 선택하세요");
            alertDialog.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (charSequences[which] == "영상") {
                        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent);
                    } else if (charSequences[which] == "일반") {
                        Intent intent = new Intent(getApplicationContext(), CoverLetterActivity.class);
                        startActivity(intent);
                    }
                }
            });
            alertDialog.show();
        }
    }
}