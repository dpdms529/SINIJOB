package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {
    EditText name, phone, email;
    TextView birth;
    Spinner gender;
    ArrayList<String> items = new ArrayList<>();

    SharedPreference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        pref = new SharedPreference(getApplicationContext());

        name = findViewById(R.id.name);
        birth = findViewById(R.id.birth);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        gender = findViewById(R.id.gender);

        items.add("남");
        items.add("여");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        name.setText(pref.preferences.getString(SharedPreference.NAME,""));
        birth.setText(pref.preferences.getString(SharedPreference.BIRTH,""));
        phone.setText(pref.preferences.getString(SharedPreference.PHONE,""));
        email.setText(pref.preferences.getString(SharedPreference.EMAIL,""));
        if(pref.preferences.getString(SharedPreference.GENDER,"")=="M"){
            gender.setSelection(0);
        }else{
            gender.setSelection(1);
        }

        birth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==birth){
            int year = Integer.parseInt(birth.getText().subSequence(0,4).toString());
            int month = Integer.parseInt(birth.getText().subSequence(4,6).toString())-1;
            int day = Integer.parseInt(birth.getText().subSequence(6,8).toString());
            DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    StringBuilder sb = new StringBuilder(String.valueOf(year));
                    if (month < 9) {
                        sb.append("0"+(month+1));
                    }else{
                        sb.append(month+1);
                    }
                    if(day < 10){
                        sb.append("0"+day);
                    }else{
                        sb.append(day);
                    }
                    birth.setText(sb.toString());
                }
            };
            DatePickerDialog oDialog = new DatePickerDialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog,mDateSetListener,year,month,day);
            oDialog.show();
        }
    }
}