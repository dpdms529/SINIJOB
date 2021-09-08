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

public class ResumeFragment extends Fragment implements View.OnClickListener {

    private static final float FONT_SIZE = 16;

    LinearLayout schoolLayout;
    LinearLayout careerLayout;
    LinearLayout certifiLayout;
    LinearLayout selfIntroLayout;

    Context context;

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

        textview("웹 개발자 / 팀장 / xx회사 / 5년 6개월");
        textview("웹 개발자 / 팀장 / oo회사 / 3년");

        schoolLayout.setOnClickListener(this);
        careerLayout.setOnClickListener(this);
        certifiLayout.setOnClickListener(this);
        selfIntroLayout.setOnClickListener(this);

        return view;
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