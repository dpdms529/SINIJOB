package org.techtown.hanieum;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ResumeFragment extends Fragment {
    EditText job_code_1;
    EditText job_code_2;
    EditText job_code_3;
    EditText career_period_1;
    EditText career_period_2;
    EditText career_period_3;
    EditText certificate_id_1;
    EditText certificate_id_2;
    EditText certificate_id_3;
    Button button;
    ArrayList<String> job_code;
    ArrayList<String> career_period;
    ArrayList<String> certificate_id;

    SharedPreference pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resume, container, false);

        job_code_1 = view.findViewById(R.id.job_code_1);
        job_code_2 = view.findViewById(R.id.job_code_2);
        job_code_3 = view.findViewById(R.id.job_code_3);
        career_period_1 = view.findViewById(R.id.career_period_1);
        career_period_2 = view.findViewById(R.id.career_period_2);
        career_period_3 = view.findViewById(R.id.career_period_3);
        certificate_id_1 = view.findViewById(R.id.certificate_id_1);
        certificate_id_2 = view.findViewById(R.id.certificate_id_2);
        certificate_id_3 = view.findViewById(R.id.certificate_id_3);
        button = view.findViewById(R.id.button);

        pref = new SharedPreference(view.getContext());

        job_code = new ArrayList<String>();
        career_period = new ArrayList<String>();
        certificate_id = new ArrayList<String>();

        job_code = pref.getStringArrayPref(SharedPreference.CAREER_JOB_CODE);
        career_period = pref.getStringArrayPref(SharedPreference.CAREER_PERIOD);
        certificate_id = pref.getStringArrayPref(SharedPreference.CERTIFICATE_CODE);

        if (job_code.size() > 0) {
            job_code_1.setText(job_code.get(0));
            job_code_2.setText(job_code.get(1));
            job_code_3.setText(job_code.get(2));
        }
        if (career_period.size() > 0) {
            career_period_1.setText(career_period.get(0));
            career_period_2.setText(career_period.get(1));
            career_period_3.setText(career_period.get(2));
        }
        if (certificate_id.size() > 0) {
            certificate_id_1.setText(certificate_id.get(0));
            certificate_id_2.setText(certificate_id.get(1));
            certificate_id_3.setText(certificate_id.get(2));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> job_code_tmp = new ArrayList<String>();
                ArrayList<String> career_period_tmp = new ArrayList<String>();
                ArrayList<String> certificate_id_tmp = new ArrayList<String>();
                // 경력 직종
                if (!job_code_1.getText().equals("")) {
                    job_code_tmp.add(String.valueOf(job_code_1.getText()));
                }
                if (!job_code_2.getText().equals("")) {
                    job_code_tmp.add(String.valueOf(job_code_2.getText()));
                }
                if (!job_code_3.getText().equals("")) {
                    job_code_tmp.add(String.valueOf(job_code_3.getText()));
                }
                // 경력 기간
                if (!career_period_1.getText().equals("")) {
                    career_period_tmp.add(String.valueOf(career_period_1.getText()));
                }
                if (!career_period_2.getText().equals("")) {
                    career_period_tmp.add(String.valueOf(career_period_2.getText()));
                }
                if (!career_period_3.getText().equals("")) {
                    career_period_tmp.add(String.valueOf(career_period_3.getText()));
                }
                // 보유 자격증
                if (!certificate_id_1.getText().equals("")) {
                    certificate_id_tmp.add(String.valueOf(certificate_id_1.getText()));
                }
                if (!certificate_id_2.getText().equals("")) {
                    certificate_id_tmp.add(String.valueOf(certificate_id_2.getText()));
                }
                if (!certificate_id_3.getText().equals("")) {
                    certificate_id_tmp.add(String.valueOf(certificate_id_3.getText()));
                }

                pref.setStringArrayPref(job_code_tmp, SharedPreference.CAREER_JOB_CODE);
                pref.setStringArrayPref(career_period_tmp, SharedPreference.CAREER_PERIOD);
                pref.setStringArrayPref(certificate_id_tmp, SharedPreference.CERTIFICATE_CODE);
                Toast.makeText(view.getContext(), "SharedPreference에 저장됨", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}