package org.techtown.hanieum;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class KeywordFragment extends Fragment {

//    Chip chip4, chip5, chip6, chip7, chip8;
    ChipGroup chipGroup;

    String[] item = {"경비", "사무", "보험", "교육", "심리", "연구", "회계", "사회복지", "통역", "건설"};

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

//        chip4 = view.findViewById(R.id.chip4);
//        chip5 = view.findViewById(R.id.chip5);
//        chip6 = view.findViewById(R.id.chip6);
//        chip7 = view.findViewById(R.id.chip7);
//        chip8 = view.findViewById(R.id.chip8);
        chipGroup = view.findViewById(R.id.chipGroup);
//
//        chip4.setCheckedIconResource(R.drawable.outline_check_24);
//        chip4.setCheckedIconTint(ColorStateList.valueOf(Color.WHITE));
//        chip4.setOnCheckedChangeListener(this);
//
//        Log.d("color", String.valueOf(chip5.getChipBackgroundColor()));

        loadChip();
        return view;
    }

//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (isChecked) {
//            chip4.setTextColor(Color.WHITE);
//            chip4.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F2AA52")));
//        } else {
//            chip4.setTextColor(Color.BLACK);
//            chip4.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E5E5E5")));
//        }
//    }

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
                    } else {
                        chip.setTextColor(Color.BLACK);
                        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E5E5E5")));
                    }
                }
            });
            chipGroup.addView(chip);
        }
    }
}