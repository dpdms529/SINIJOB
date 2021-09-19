package org.techtown.hanieum;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.CvInfoDao;
import org.techtown.hanieum.db.entity.CvInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CarCerActivity extends AppCompatActivity implements View.OnClickListener {

    Button saveButton;
    Button addButton;
    RecyclerView recyclerView;
    TextView title;

    CareerAdapter adapter1;
    CertifiAdapter adapter2;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_cer);

        saveButton = findViewById(R.id.saveButton);
        addButton = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.title);

        db = AppDatabase.getInstance(this);

        Intent intent = getIntent();
        if (intent.getStringExtra("type").equals("career")) {   // 경력사항
            title.setText("경력사항");
            LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager1);
            adapter1 = new CareerAdapter();
            adapter1.setItems(new ArrayList<>());

            // db에 저장된거 띄우기
            List<CvInfo> cv = null;
            try {
                cv = new GetAllAsyncTask(db.CvInfoDao()).execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < cv.size(); i++) {
                adapter1.addItem(new Career("", cv.get(i).company_name, "", ""));
            }

            recyclerView.setAdapter(adapter1);
        } else {    // 보유자격증
            title.setText("보유자격증");
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager2);
            adapter2 = new CertifiAdapter();
            adapter2.setItems(new ArrayList<>());



            recyclerView.setAdapter(adapter2);
        }

        saveButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == saveButton) {
            if (title.getText().equals("경력사항")) {
                ArrayList<Career> items = adapter1.getItems();
                new CareerDeleteAsyncTask(db.CvInfoDao()).execute();    // "CA"를 모두 지우고 다시 저장
                for (int i = 0; i < items.size(); i++) {
                    Career item = items.get(i);
                    if (item.getJobCode() == null) {    // 직종을 선택하지 않았으면
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("알림")
                                .setMessage("직종을 선택하세요")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return;
                    }
                    if (!item.getPeriodStr().contains("~")) {    // 기간을 선택하지 않았으면
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("알림")
                                .setMessage("기간을 설정하세요")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return;
                    }
                    CvInfo cvInfo = new CvInfo("CA", i, item.getJobCode(), item.getPeriodInt(), item.getCompName());
                    new SchoolActivity.CvInfoInsertAsyncTask(db.CvInfoDao()).execute(cvInfo);
                }
            } else if (title.getText().equals("보유자격증")) {

            }
            finish();
        } else if (v == addButton) {
            int careerNum = adapter1.getItemCount();
//            int certifiNum = adapter2.getItemCount();

            if ((careerNum == 10)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알림")
                        .setMessage("최대 10개까지 등록 가능합니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                if (title.getText().equals("경력사항")) {
                    adapter1.addItem(new Career("", "", "", ""));
                    adapter1.notifyDataSetChanged();
                } else if (title.getText().equals("보유자격증")) {
                    adapter2.addItem(new Certificate("", ""));
                    adapter2.notifyDataSetChanged();
                }
            }
        }
    }

    public static class GetAllAsyncTask extends AsyncTask<Void, Void, List<CvInfo>> {
        private CvInfoDao mCvInfoDao;

        public GetAllAsyncTask(CvInfoDao cvInfoDao) {
            this.mCvInfoDao = cvInfoDao;
        }

        @Override
        protected List<CvInfo> doInBackground(Void... voids) {
            return mCvInfoDao.getCvInfo("CA");
        }
    }

    public static class CareerDeleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private CvInfoDao mCvInfoDao;

        public CareerDeleteAsyncTask(CvInfoDao cvInfoDao) {
            this.mCvInfoDao = cvInfoDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mCvInfoDao.deleteCvInfo("CA");
            return null;
        }
    }

}