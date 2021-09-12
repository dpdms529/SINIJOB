package org.techtown.hanieum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Dimension;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.techtown.hanieum.db.AppDatabase;

public class ResumeFragment extends Fragment implements View.OnClickListener {

    private static final float FONT_SIZE = 16;

    LinearLayout schoolLayout;
    LinearLayout careerLayout;
    LinearLayout certifiLayout;
    LinearLayout selfIntroLayout;
    TextView school;

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

        db = AppDatabase.getInstance(this.getContext());

        schoolLayout.setOnClickListener(this);
        careerLayout.setOnClickListener(this);
        certifiLayout.setOnClickListener(this);
        selfIntroLayout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String education = db.CvInfoDao().getInfoCode("E");
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

    public void textview(String a){
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

}