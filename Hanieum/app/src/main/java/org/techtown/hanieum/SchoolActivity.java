package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SchoolActivity extends AppCompatActivity {

    Button saveButton;
    Spinner spinner1;
    Spinner spinner2;
    ArrayAdapter adapter1;
    ArrayAdapter adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        saveButton = findViewById(R.id.saveButton);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);

        adapter1 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.schoolArray1));
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    adapter2 = new ArrayAdapter(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.schoolArray2_0));
                } else if (position==1 || position==2 || position==3) {
                    adapter2 = new ArrayAdapter(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.schoolArray2_123));
                } else {
                    adapter2 = new ArrayAdapter(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.schoolArray2_456));
                }
                spinner2.setAdapter(adapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}