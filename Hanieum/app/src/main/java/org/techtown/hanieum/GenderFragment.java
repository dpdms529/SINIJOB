package org.techtown.hanieum;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class GenderFragment extends Fragment implements View.OnClickListener {
    ImageView female;
    ImageView male;
    Button prevBtn;
    Button nextBtn;

    SharedPreference pref;

    String selected = "";   // 여성: 0    남성: 1

    public GenderFragment() {
        // Required empty public constructor
    }

    public static GenderFragment newInstance() {
        GenderFragment fragment = new GenderFragment();
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
        View view = inflater.inflate(R.layout.fragment_gender, container, false);

        female = view.findViewById(R.id.female);
        male = view.findViewById(R.id.male);
        prevBtn = view.findViewById(R.id.prevBtn);
        nextBtn = view.findViewById(R.id.nextBtn);

        pref = new SharedPreference(getContext());

        selected = pref.preferences.getString(SharedPreference.GENDER, "");
        if (selected.equals("F")) {
            female.setColorFilter(Color.parseColor("#FF5757"));
        } else if (selected.equals("M")) {
            male.setColorFilter(Color.parseColor("#3F51B5"));
        }

        female.setOnClickListener(this);
        male.setOnClickListener(this);
        prevBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == female) {
            male.setColorFilter(Color.parseColor("#9E9E9E"));
            selected = "F";
            female.setColorFilter(Color.parseColor("#FF5757"));
        } else if (v == male) {
            female.setColorFilter(Color.parseColor("#9E9E9E"));
            selected = "M";
            male.setColorFilter(Color.parseColor("#3F51B5"));
        } else if (v == prevBtn) {
            pref.editor.putString(SharedPreference.GENDER, selected);
            ((InfoGetActivity)getActivity()).replaceFragment(NameFragment.newInstance());
        } else if (v == nextBtn) {
            if (selected.equals("")) {
                Toast.makeText(getContext(), "성별을 선택하세요", Toast.LENGTH_SHORT).show();
            } else {
                pref.editor.putString(SharedPreference.GENDER, selected);
                ((InfoGetActivity)getActivity()).replaceFragment(BirthFragment.newInstance());
            }
        }
        pref.editor.commit();
    }
}