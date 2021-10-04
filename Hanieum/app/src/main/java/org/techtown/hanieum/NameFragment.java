package org.techtown.hanieum;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NameFragment extends Fragment implements View.OnClickListener {

    EditText nameText;
    Button prevBtn;
    Button nextBtn;

    SharedPreference pref;

    public NameFragment() {
        // Required empty public constructor
    }

    public static NameFragment newInstance() {
        NameFragment fragment = new NameFragment();
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
        View view = inflater.inflate(R.layout.fragment_name, container, false);

        nameText = view.findViewById(R.id.nameText);
        prevBtn = view.findViewById(R.id.prevBtn);
        nextBtn = view.findViewById(R.id.nextBtn);

        pref = new SharedPreference(getContext());

        nameText.setText(pref.preferences.getString(SharedPreference.NAME, ""));

        prevBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == prevBtn) {
            pref.editor.putString(SharedPreference.NAME, nameText.getText().toString());
            pref.editor.commit();
            ((InfoGetActivity)getActivity()).replaceFragment(AddressFragment.newInstance());
        } else if (v == nextBtn) {
            if (nameText.getText().length() == 0) {
                Toast.makeText(getContext(), "이름을 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                pref.editor.putString(SharedPreference.NAME, nameText.getText().toString());
                pref.editor.commit();
                ((InfoGetActivity)getActivity()).replaceFragment(GenderFragment.newInstance());
            }
        }
    }
}