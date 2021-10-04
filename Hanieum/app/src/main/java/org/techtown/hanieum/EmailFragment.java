package org.techtown.hanieum;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EmailFragment extends Fragment implements View.OnClickListener {
    EditText emailText;
    Button prevBtn;
    Button nextBtn;

    SharedPreference pref;

    public EmailFragment() {
        // Required empty public constructor
    }

    public static EmailFragment newInstance() {
        EmailFragment fragment = new EmailFragment();
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
        View view = inflater.inflate(R.layout.fragment_email, container, false);

        emailText = view.findViewById(R.id.emailText);
        prevBtn = view.findViewById(R.id.prevBtn);
        nextBtn = view.findViewById(R.id.nextBtn);

        pref = new SharedPreference(getContext());

        emailText.setText(pref.preferences.getString(SharedPreference.EMAIL, ""));

        prevBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == prevBtn) {
            pref.editor.putString(SharedPreference.EMAIL, emailText.getText().toString());
            pref.editor.commit();
            ((InfoGetActivity)getActivity()).replaceFragment(BirthFragment.newInstance());
        } else if (v == nextBtn) {
            if (emailText.getText().length() == 0) {
                Toast.makeText(getContext(), "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                pref.editor.putString(SharedPreference.EMAIL, emailText.getText().toString());
                pref.editor.commit();
                ((InfoGetActivity)getActivity()).replaceFragment(PhoneFragment.newInstance());
            }
        }
    }
}