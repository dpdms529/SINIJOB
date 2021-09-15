package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.CoverLetterDao;
import org.techtown.hanieum.db.dao.CvInfoDao;
import org.techtown.hanieum.db.entity.CoverLetter;
import org.techtown.hanieum.db.entity.CvInfo;

import java.util.concurrent.ExecutionException;

public class SchoolActivity extends AppCompatActivity {

    Button saveButton;
    Spinner spinner;
    ArrayAdapter adapter;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        saveButton = findViewById(R.id.saveButton);
        spinner = findViewById(R.id.spinner);

        db = AppDatabase.getInstance(this);
        String education = null;
        try {
            education = new ResumeFragment.GetCvInfoAsyncTask(db.CvInfoDao()).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.schoolArray1));
        spinner.setAdapter(adapter);
        if (education != null) {
            if (education.equals("01")) {
                spinner.setSelection(1);
            } else if (education.equals("02")) {
                spinner.setSelection(2);
            } else if (education.equals("03")) {
                spinner.setSelection(3);
            } else if (education.equals("04")) {
                spinner.setSelection(4);
            } else if (education.equals("05")) {
                spinner.setSelection(5);
            } else if (education.equals("06")) {
                spinner.setSelection(6);
            } else if (education.equals("07")) {
                spinner.setSelection(7);
            }
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = "00";
                if (spinner.getSelectedItem().equals("박사")) {
                    code = "07";
                } else if (spinner.getSelectedItem().equals("석사")) {
                    code = "06";
                } else if (spinner.getSelectedItem().equals("대학(4년제) 졸업")) {
                    code = "05";
                } else if (spinner.getSelectedItem().equals("대학(2,3년제) 졸업")) {
                    code = "04";
                } else if (spinner.getSelectedItem().equals("고등학교 졸업")) {
                    code = "03";
                } else if (spinner.getSelectedItem().equals("중학교 졸업")) {
                    code = "02";
                } else if (spinner.getSelectedItem().equals("초등학교 졸업 이하")) {
                    code = "01";
                }
                CvInfo cvInfo = new CvInfo("E", 0, code, null, null);
                new CvInfoInsertAsyncTask(db.CvInfoDao()).execute(cvInfo);

                finish();
            }
        });
    }

    // 사용처: SchoolActivity, CarCerActivity
    public static class CvInfoInsertAsyncTask extends AsyncTask<CvInfo, Void, Void> {
        private CvInfoDao mCvInfoDao;

        public CvInfoInsertAsyncTask(CvInfoDao cvInfoDao){
            this.mCvInfoDao = cvInfoDao;
        }
        @Override
        protected Void doInBackground(CvInfo... cvInfos) {
            mCvInfoDao.insertCvInfo(cvInfos[0]);
            return null;
        }
    }
}