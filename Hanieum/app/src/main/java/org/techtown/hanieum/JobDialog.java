package org.techtown.hanieum;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.JobCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class JobDialog extends Dialog {

    EditText jobName;
    RecyclerView recyclerView;

    InputMethodManager imm;
    JobDialogAdapter adapter;
    Context context;
    List<JobCategory> job;
    List<org.techtown.hanieum.db.entity.Certificate> certificateList;

    Career career;
    Certificate certificate;

    public JobDialog(@NonNull Context context, Career career) {
        super(context);
        this.context = context;
        this.career = career;
    }

    public JobDialog(@NonNull Context context, Certificate certificate) {
        super(context);
        this.context = context;
        this.certificate = certificate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_dialog);

        jobName = findViewById(R.id.jobName);
        recyclerView = findViewById(R.id.recyclerView);
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new JobDialogAdapter();
        recyclerView.setAdapter(adapter);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        AppDatabase db = AppDatabase.getInstance(context);
        if(career != null){
            job = null;
            try {
                job = new Query.JobGetAsyncTask(db.jobCategoryDao()).execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if(certificate != null){
            try {
                certificateList = null;
                certificateList = new Query.CertificateGetAsyncTask(db.CertificateDao()).execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        jobName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력에 변화가 있을 때
                String text = jobName.getText().toString();
                ArrayList<JobDialogItem> items = new ArrayList<>();

                if (text.length() != 0) {
                    if(career != null){
                        for (int i = 0; i < job.size(); i++) {
                            if (job.get(i).category_name.contains(text)) {
                                items.add(new JobDialogItem(job.get(i).category_code, job.get(i).category_name));
                            }
                        }
                    }else if(certificate != null){
                        for (int i = 0; i < certificateList.size(); i++) {
                            if(certificate != null && certificateList.get(i).certificate_name.contains(text)){
                                items.add(new JobDialogItem(certificateList.get(i).certificate_id, certificateList.get(i).certificate_name));
                            }
                        }
                    }

                }
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전
            }
        });
        jobName.setOnEditorActionListener(new TextView.OnEditorActionListener() {   // 공고 검색
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    imm.hideSoftInputFromWindow(jobName.getWindowToken(), 0); //키보드 내리기
                    return true;
                }
                return false;
            }
        });

        adapter.setItemClickListener(new OnJobDialogClickListener() {
            @Override
            public void OnItemClick(JobDialogAdapter.ViewHolder holder, View view, int position) {
                JobDialogItem item = adapter.getItem(position);
                String name = item.categoryName;
                String code = item.categoryCode;
                if(career != null){
                    career.setJobCode(code);
                    career.setJobName(name);
                    Log.d("TAG", "JobDialog: " + career.getJobName() + career.getJobCode());
                }else if(certificate != null){
                    certificate.setCertifiCode(code);
                    certificate.setCertifi(name);
                }
                cancel();
            }
        });
    }

}
