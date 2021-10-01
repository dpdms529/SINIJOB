package org.techtown.hanieum;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class BirthFragment extends Fragment implements View.OnClickListener {

    DatePicker birth;
    Button prevBtn;
    Button nextBtn;

    Calendar calendar;
    SharedPreference pref;

    String mon = "";
    String day = "";

    public BirthFragment() {
        // Required empty public constructor
    }

    public static BirthFragment newInstance() {
        BirthFragment fragment = new BirthFragment();
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
        View view = inflater.inflate(R.layout.fragment_birth, container, false);

        birth = view.findViewById(R.id.birth);
        prevBtn = view.findViewById(R.id.prevBtn);
        nextBtn = view.findViewById(R.id.nextBtn);

        pref = new SharedPreference(getContext());
        String prefBirth = pref.preferences.getString(SharedPreference.BIRTH, "");
        calendar = new GregorianCalendar();
        if (!prefBirth.equals("")) {
            String year = prefBirth.substring(0, 4);
            mon = prefBirth.substring(4, 6);
            day = prefBirth.substring(6);
            birth.init(Integer.parseInt(year), Integer.parseInt(mon)-1, Integer.parseInt(day), mOnDateChangedListener);
        } else {
            birth.init(1970, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), mOnDateChangedListener);
        }

        prevBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == prevBtn) {
            if (birth.getMonth()+1 < 10) {
                mon = "0"+(birth.getMonth()+1);
            } else {
                mon = String.valueOf(birth.getMonth()+1);
            }
            if (birth.getDayOfMonth() < 10) {
                day = "0"+(birth.getDayOfMonth());
            } else {
                day = String.valueOf(birth.getDayOfMonth());
            }
            pref.editor.putString(SharedPreference.BIRTH, birth.getYear()+mon+day);
            pref.editor.putInt(SharedPreference.AGE, calendar.get(Calendar.YEAR) - birth.getYear() + 1);
            pref.editor.commit();
            ((InfoGetActivity)getActivity()).replaceFragment(GenderFragment.newInstance());
        } else if (v == nextBtn) {
            if (birth.getMonth()+1 < 10) {
                mon = "0"+(birth.getMonth()+1);
            } else {
                mon = String.valueOf(birth.getMonth()+1);
            }
            if (birth.getDayOfMonth() < 10) {
                day = "0"+(birth.getDayOfMonth());
            } else {
                day = String.valueOf(birth.getDayOfMonth());
            }
            pref.editor.putString(SharedPreference.BIRTH, birth.getYear()+mon+day);
            pref.editor.putInt(SharedPreference.AGE, calendar.get(Calendar.YEAR) - birth.getYear() + 1);
            pref.editor.commit();
            ((InfoGetActivity)getActivity()).replaceFragment(EmailFragment.newInstance());
        }
    }

    DatePicker.OnDateChangedListener mOnDateChangedListener = new DatePicker.OnDateChangedListener(){
        @Override
        public void onDateChanged(DatePicker datePicker, int yy, int mm, int dd) {

        }
    };
}