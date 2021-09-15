package org.techtown.hanieum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Dimension;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.CvInfoDao;
import org.techtown.hanieum.db.entity.CoverLetter;
import org.techtown.hanieum.db.entity.CvInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ResumeFragment extends Fragment implements View.OnClickListener {

    private static final float FONT_SIZE = 16;

    LinearLayout schoolLayout;
    LinearLayout careerLayout;
    LinearLayout certifiLayout;
    LinearLayout selfIntroLayout;
    TextView school;

    RecyclerView selfInfoRecyclerView;
    SelfInfoAdapter selfInfoAdapter;

    Context context;

    AppDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resume, container, false);
        context = getContext();

        schoolLayout = view.findViewById(R.id.schoolLayout);
        careerLayout = view.findViewById(R.id.careerLayout);
        certifiLayout = view.findViewById(R.id.certifiLayout);
        selfIntroLayout = view.findViewById(R.id.selfIntroLayout);
        school = view.findViewById(R.id.school);

        selfInfoRecyclerView = view.findViewById(R.id.selfInfoRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        selfInfoRecyclerView.setLayoutManager(layoutManager);
        selfInfoAdapter = new SelfInfoAdapter();
        selfInfoRecyclerView.setAdapter(selfInfoAdapter);

        db = AppDatabase.getInstance(this.getContext());


        // 지울거
        List<CvInfo> tmp = null;
        try {
            tmp = new GetAllAsyncTask(db.CvInfoDao()).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < tmp.size(); i++) {
            Log.d("info_no", String.valueOf(tmp.get(i).info_no));
            Log.d("info_code", tmp.get(i).info_code);
            Log.d("career_period", String.valueOf(tmp.get(i).career_period));
            Log.d("company_name", tmp.get(i).company_name);
            Log.d("cv_dist_code", tmp.get(i).cv_dist_code);
        }

        db.CoverLetterDao().getAll().observe((LifecycleOwner) this, new Observer<List<CoverLetter>>() {
            @Override
            public void onChanged(List<CoverLetter> coverLetters) {
                selfInfoAdapter.clearItems();
                for (CoverLetter c : coverLetters) {
                    selfInfoAdapter.addItem(new SelfInfo(c));
                }
                selfInfoAdapter.notifyDataSetChanged();
            }
        });

        schoolLayout.setOnClickListener(this);
        careerLayout.setOnClickListener(this);
        certifiLayout.setOnClickListener(this);
        selfIntroLayout.setOnClickListener(this);
        selfInfoAdapter.setOnItemClickListener(new OnSelfInfoItemClickListener() {
            @Override
            public void OnItemClick(SelfInfoAdapter.ViewHolder holder, View view, int position) {
                Intent intent = new Intent(getContext(), SelfInfoActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String education = null;
        try {
            education = new GetCvInfoAsyncTask(db.CvInfoDao()).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (education != null) {
            if (education.equals("00")) {
                school.setVisibility(View.GONE);
            } else if (education.equals("01")) {
                school.setText("초등학교 졸업 이하");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("02")) {
                school.setText("중학교 졸업");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("03")) {
                school.setText("고등학교 졸업");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("04")) {
                school.setText("대학(2,3년제) 졸업");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("05")) {
                school.setText("대학(4년제) 졸업");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("06")) {
                school.setText("석사");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("07")) {
                school.setText("박사");
                school.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == schoolLayout) {
            Intent intent = new Intent(getContext(), SchoolActivity.class);
            startActivity(intent);
        } else if (v == careerLayout) {
            Intent intent = new Intent(getContext(), CarCerActivity.class);
            intent.putExtra("type", "career");
            startActivity(intent);
        } else if (v == certifiLayout) {
            Intent intent = new Intent(getContext(), CarCerActivity.class);
            intent.putExtra("type", "certificate");
            startActivity(intent);
        } else if (v == selfIntroLayout) {
            Intent intent = new Intent(getContext(), SelfInfoActivity.class);
            startActivity(intent);
        }
    }

    public void textview(String a) {
        //TextView 생성
        TextView view1 = new TextView(context);
        view1.setText(a);
        view1.setTextSize(Dimension.SP, FONT_SIZE);
        view1.setTextColor(Color.BLACK);

        //layout_width, layout_height, gravity 설정
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.gravity = Gravity.CENTER;
//        view1.setLayoutParams(lp);

        //부모 뷰에 추가
        careerLayout.addView(view1);
    }

    // 사용처: ResumeFragment, SchoolActivity
    public static class GetCvInfoAsyncTask extends AsyncTask<Void, Void, String> {
        private CvInfoDao mCvInfoDao;

        public GetCvInfoAsyncTask(CvInfoDao cvInfoDao) {
            this.mCvInfoDao = cvInfoDao;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return mCvInfoDao.getInfoCode("E");
        }
    }

    // 지울거
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

}