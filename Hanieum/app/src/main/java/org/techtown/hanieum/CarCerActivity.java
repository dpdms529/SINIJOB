package org.techtown.hanieum;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.techtown.hanieum.db.AppDatabase;
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
    SharedPreference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_cer);

        saveButton = findViewById(R.id.saveButton);
        addButton = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.title);

        db = AppDatabase.getInstance(this);
        pref = new SharedPreference(this);

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
                cv = new Query.CvInfoGetAsyncTask(db.CvInfoDao()).execute(pref.preferences.getString(SharedPreference.USER_ID," "),"CA").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (CvInfo cvInfo: cv) {
                adapter1.addItem(new Career(cvInfo));
                Log.d("TAG", "onCreate: " + adapter1.getItem(cvInfo.info_no).getCareerStart());
            }

            recyclerView.setAdapter(adapter1);
        } else {    // 보유자격증
            title.setText("보유자격증");
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager2);
            adapter2 = new CertifiAdapter();
            adapter2.setItems(new ArrayList<>());

            List<CvInfo> cv = null;
            try {
                cv = new Query.CvInfoGetAsyncTask(db.CvInfoDao()).execute(pref.preferences.getString(SharedPreference.USER_ID," "),"CE").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (CvInfo cvInfo: cv) {
                adapter2.addItem(new Certificate(cvInfo));
                Log.d("TAG", "onCreate: " + adapter2.getItem(cvInfo.info_no).getCertifi());
            }

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
                new Query.CvInfoDeleteAsyncTask(db.CvInfoDao()).execute(pref.preferences.getString(SharedPreference.USER_ID,""), "CA");    // "CA"를 모두 지우고 다시 저장
                for (int i = 0; i < items.size(); i++) {
                    Career item = items.get(i);
                    if (item.getJobCode() == null) {    // 직종을 선택하지 않았으면
                        alterDialog("직종을 선택하세요");
                        return;
                    }
                    if (item.getCompName().equals("")) {
                        alterDialog("회사명을 입력하세요");
                        return;
                    }
                    if (item.getCareerStart() == null) {    // 기간을 선택하지 않았으면
                        alterDialog("기간을 설정하세요");
                        return;
                    }
                    CvInfo cvInfo = new CvInfo(pref.preferences.getString(SharedPreference.USER_ID,""),"CA", i, item.getJobCode(), item.getJobName(), item.getCompName(), item.getPosition(), item.getCareerStart(), item.getCarrerEnd(), item.getPeriod());
                    new Query.CvInfoInsertAsyncTask(db.CvInfoDao()).execute(cvInfo);
                }
            } else if (title.getText().equals("보유자격증")) {
                ArrayList<Certificate> items = adapter2.getItmes();
                new Query.CvInfoDeleteAsyncTask(db.CvInfoDao()).execute(pref.preferences.getString(SharedPreference.USER_ID,""), "CE");
                for(int i = 0; i < items.size();i++){
                    Certificate item = items.get(i);
                    if(item.getCertifiCode() == null){
                        alterDialog("자격증을 선택하세요");
                        return;
                    }
                    CvInfo cvInfo = new CvInfo(pref.preferences.getString(SharedPreference.USER_ID,""),"CE", i, item.getCertifiCode(), item.getCertifi(), null, null, null, null, 0);
                    new Query.CvInfoInsertAsyncTask(db.CvInfoDao()).execute(cvInfo);
                }


            }
            finish();
        } else if (v == addButton) {
            if(title.getText().equals("경력사항")){
                int careerNum = adapter1.getItemCount();
                if(careerNum < 10){
                    adapter1.addItem(new Career());
                    adapter1.notifyDataSetChanged();
                } else {
                    alterDialog("최대 10개까지 등록 가능합니다.");
                }
            }else if(title.getText().equals("보유자격증")){
                int certifiNum = adapter2.getItemCount();
                if(certifiNum < 10){
                    adapter2.addItem(new Certificate());
                    adapter2.notifyDataSetChanged();
                } else {
                    alterDialog("최대 10개까지 등록 가능합니다.");
                }
            }
        }
    }

    private void alterDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림")
                .setMessage(text)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}