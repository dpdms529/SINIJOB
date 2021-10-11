package org.techtown.hanieum;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PhoneFragment extends Fragment implements View.OnClickListener {
    EditText phoneText;
    Button prevBtn;
    Button finishBtn;

    SharedPreference pref;

    public PhoneFragment() {
        // Required empty public constructor
    }

    public static PhoneFragment newInstance() {
        PhoneFragment fragment = new PhoneFragment();
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
        View view = inflater.inflate(R.layout.fragment_phone, container, false);

        phoneText = view.findViewById(R.id.phoneText);
        prevBtn = view.findViewById(R.id.prevBtn);
        finishBtn = view.findViewById(R.id.finishBtn);

        pref = new SharedPreference(getContext());

        phoneText.setText(pref.preferences.getString(SharedPreference.PHONE, ""));

        prevBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == prevBtn) {
            pref.editor.putString(SharedPreference.PHONE, phoneText.getText().toString());
            pref.editor.commit();
            ((InfoGetActivity)getActivity()).replaceFragment(EmailFragment.newInstance());
        } else if (v == finishBtn) {
            if (phoneText.getText().length() < 11) {
                Toast.makeText(getContext(), "전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                pref.editor.putString(SharedPreference.PHONE, phoneText.getText().toString());
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
                        "&birthday="+pref.preferences.getString(SharedPreference.BIRTH, "") );
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
                        "&birthday="+pref.preferences.getString(SharedPreference.BIRTH, "");
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
}