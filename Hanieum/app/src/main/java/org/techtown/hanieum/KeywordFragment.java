package org.techtown.hanieum;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Dimension;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;


import java.util.ArrayList;

public class KeywordFragment extends Fragment implements View.OnClickListener {

    ChipGroup chipGroup;
    Button saveBtn;

    String[] item = {"건설", "경비","경영", "경찰/소방/군인", "공예", "공학", "교육", "금융", "기계/금속",
            "농림어업", "돌봄", "디자인", "목재", "미용", "방송", "법률", "보험", "사무", "사회복지", "숙박",
            "스포츠", "식품가공", "여행", "연구", "영업", "예술", "요리", "운전", "인쇄", "의료", "전기/전자",
            "청소", "채굴", "판매", "화학/섬유"};
    ArrayList<String> choice = new ArrayList<String>();
    String keywordText = "";

    SharedPreference pref;

    public KeywordFragment() {
        // Required empty public constructor
    }

    public static KeywordFragment newInstance() {
        KeywordFragment fragment = new KeywordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyword, container, false);

        chipGroup = view.findViewById(R.id.chipGroup);
        saveBtn = view.findViewById(R.id.saveBtn);

        pref = new SharedPreference(getContext());

        saveBtn.setOnClickListener(this);
        loadChip();
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == saveBtn) {
            if (choice.size() == 0) {
                Toast.makeText(getContext(), "단어를 1개이상 선택해주세요", Toast.LENGTH_SHORT).show();
            } else if (choice.size() > 10) {
                Toast.makeText(getContext(), "단어를 10개이하 선택해주세요", Toast.LENGTH_SHORT).show();
            } else {
                keywordText = choice.get(0);
                for (int i=1; i<choice.size(); i++) {
                    keywordText = keywordText + "|" + choice.get(i);
                }

                pref.editor.putString(SharedPreference.KEYWORD, keywordText);
                pref.editor.commit();

                Log.d("TAG","user_id="+pref.preferences.getString(SharedPreference.USER_ID, "")+
                        "&street_code="+pref.preferences.getString(SharedPreference.STREET_CODE,"")+
                        "&main_no="+pref.preferences.getString(SharedPreference.MAIN_NO,"")+
                        "&additional_no="+pref.preferences.getString(SharedPreference.ADDITIONAL_NO,"") +
                        "&name="+pref.preferences.getString(SharedPreference.NAME, "")+
                        "&age="+pref.preferences.getString(SharedPreference.AGE, "")+
                        "&gender="+pref.preferences.getString(SharedPreference.GENDER, "")+
                        "&phone_number="+pref.preferences.getString(SharedPreference.PHONE, "")+
                        "&email="+pref.preferences.getString(SharedPreference.EMAIL, "none")+
                        "&address="+pref.preferences.getString(SharedPreference.ADDRESS,"")  +
                        "&birthday="+pref.preferences.getString(SharedPreference.BIRTH, "") +
                        "&keyword="+pref.preferences.getString(SharedPreference.KEYWORD, ""));
                // db에 저장
                String php = getResources().getString(R.string.serverIP) + "user_save.php?" +
                        "user_id="+pref.preferences.getString(SharedPreference.USER_ID, "")+
                        "&street_code="+pref.preferences.getString(SharedPreference.STREET_CODE,"")+
                        "&main_no="+pref.preferences.getString(SharedPreference.MAIN_NO,"")+
                        "&additional_no="+pref.preferences.getString(SharedPreference.ADDITIONAL_NO,"") +
                        "&name="+pref.preferences.getString(SharedPreference.NAME, "")+
                        "&age="+pref.preferences.getString(SharedPreference.AGE, "")+
                        "&gender="+pref.preferences.getString(SharedPreference.GENDER, "")+
                        "&phone_number="+pref.preferences.getString(SharedPreference.PHONE, "")+
                        "&email="+pref.preferences.getString(SharedPreference.EMAIL, "none")+
                        "&address="+pref.preferences.getString(SharedPreference.ADDRESS,"")  +
                        "&birthday="+pref.preferences.getString(SharedPreference.BIRTH, "") +
                        "&keyword="+pref.preferences.getString(SharedPreference.KEYWORD, "");
                URLConnector urlConnector = new URLConnector(php);
                urlConnector.start();
                try {
                    urlConnector.join();
                } catch (InterruptedException e) {
                }

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }

    private void loadChip() {
        for (int i=0; i<item.length; i++) {
            Chip chip = new Chip(getContext());
            chip.setText(item[i]);
            chip.setTextSize(Dimension.SP, 16);
            chip.setCheckable(true);
            chip.setCheckedIconResource(R.drawable.outline_check_24);
            chip.setCheckedIconTint(ColorStateList.valueOf(Color.WHITE));
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        chip.setTextColor(Color.WHITE);
                        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F2AA52")));
                        choice.add((String) chip.getText());
                    } else {
                        chip.setTextColor(Color.BLACK);
                        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E5E5E5")));
                        choice.remove((String) chip.getText());
                    }
                }
            });
            chipGroup.addView(chip);
        }
    }

}