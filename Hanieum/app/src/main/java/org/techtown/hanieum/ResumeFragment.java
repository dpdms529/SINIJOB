package org.techtown.hanieum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Dimension;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.CoverLetter;
import org.techtown.hanieum.db.entity.CvInfo;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ResumeFragment extends Fragment implements View.OnClickListener {

    private static final float FONT_SIZE = 16;

    LinearLayout schoolLayout;
    LinearLayout careerLayout;
    LinearLayout certifiLayout;
    LinearLayout selfIntroLayout;
    LinearLayout careerTextLayout;
    LinearLayout certifiTextLayout;
    TextView name, genderAge, address, phone, email, school;
    ImageView setting, profile_pic;

    RecyclerView selfInfoRecyclerView;
    SelfInfoAdapter selfInfoAdapter;

    Context context;

    AppDatabase db;
    SharedPreference pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resume, container, false);
        context = getContext();

        pref = new SharedPreference(context);

        schoolLayout = view.findViewById(R.id.schoolLayout);
        careerLayout = view.findViewById(R.id.careerLayout);
        certifiLayout = view.findViewById(R.id.certifiLayout);
        selfIntroLayout = view.findViewById(R.id.selfIntroLayout);
        careerTextLayout = view.findViewById(R.id.careerTextLayout);
        certifiTextLayout = view.findViewById(R.id.certifiTextLayout);
        name = view.findViewById(R.id.name);
        genderAge = view.findViewById(R.id.genderAge);
        address = view.findViewById(R.id.address);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);
        school = view.findViewById(R.id.school);
        setting = view.findViewById(R.id.setting);
        profile_pic = view.findViewById(R.id.imageView3);

        selfInfoRecyclerView = view.findViewById(R.id.selfInfoRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        selfInfoRecyclerView.setLayoutManager(layoutManager);
        selfInfoAdapter = new SelfInfoAdapter();
        selfInfoRecyclerView.setAdapter(selfInfoAdapter);

        db = AppDatabase.getInstance(this.getContext());

        db.CoverLetterDao().getUserAll(pref.preferences.getString(SharedPreference.USER_ID,"")).observe((LifecycleOwner) this, new Observer<List<CoverLetter>>() {
            @Override
            public void onChanged(List<CoverLetter> coverLetters) {
                selfInfoAdapter.clearItems();
                for (CoverLetter c : coverLetters) {
                    selfInfoAdapter.addItem(new SelfInfo(c));
                }
                selfInfoAdapter.notifyDataSetChanged();
            }
        });

        setting.setOnClickListener(this);
        schoolLayout.setOnClickListener(this);
        careerLayout.setOnClickListener(this);
        certifiLayout.setOnClickListener(this);
        selfIntroLayout.setOnClickListener(this);
        selfInfoAdapter.setOnItemClickListener(new OnSelfInfoItemClickListener() {
            @Override
            public void OnItemClick(SelfInfoAdapter.ViewHolder holder, View view, int position) {
                Intent intent = new Intent(getContext(), SelfInfoActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        name.setText(pref.preferences.getString(SharedPreference.NAME,""));
        if(pref.preferences.getString(SharedPreference.GENDER,"").equals("F")){
            genderAge.setText("?????? / " + pref.preferences.getString(SharedPreference.AGE,"")+"???");
        }else{
            genderAge.setText("?????? / " + pref.preferences.getString(SharedPreference.AGE,"")+"???");
        }
        address.setText(pref.preferences.getString(SharedPreference.ADDRESS,""));
        phone.setText(pref.preferences.getString(SharedPreference.PHONE,""));
        email.setText(pref.preferences.getString(SharedPreference.EMAIL,""));

        // ????????????
        String education = null;
        try {
            education = new Query.CvInfoGetInfoCodeAsyncTask(db.CvInfoDao()).execute(pref.preferences.getString(SharedPreference.USER_ID, " "),"E").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (education != null) {
            if (education.equals("00")) {
                school.setVisibility(View.GONE);
            } else if (education.equals("01")) {
                school.setText("???????????? ?????? ??????");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("02")) {
                school.setText("????????? ??????");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("03")) {
                school.setText("???????????? ??????");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("04")) {
                school.setText("??????(2,3??????) ??????");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("05")) {
                school.setText("??????(4??????) ??????");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("06")) {
                school.setText("??????");
                school.setVisibility(View.VISIBLE);
            } else if (education.equals("07")) {
                school.setText("??????");
                school.setVisibility(View.VISIBLE);
            }
        }

        // ????????????
        List<CvInfo> cv = null;
        try {
            cv = new Query.CvInfoGetAsyncTask(db.CvInfoDao()).execute(pref.preferences.getString(SharedPreference.USER_ID, " "), "CA").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cv != null) {
            careerTextLayout.removeAllViews();
            for (CvInfo cvInfo : cv) {
                if (cvInfo.job_position.equals("")) {
                    careerTextLayout.addView(textview((cvInfo.info_no+1) + ". " + cvInfo.info + " / " + cvInfo.company_name + " / " + cvInfo.period + "??????"));
                } else {
                    careerTextLayout.addView(textview((cvInfo.info_no+1) + ". " + cvInfo.info + " / " + cvInfo.company_name + " / " + cvInfo.job_position + " / " + cvInfo.period + "??????")); }
            }
        }

        // ???????????????
        cv = null;
        try {
            cv = new Query.CvInfoGetAsyncTask(db.CvInfoDao()).execute(pref.preferences.getString(SharedPreference.USER_ID, " "), "CE").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cv != null) {
            certifiTextLayout.removeAllViews();
            for (CvInfo cvInfo : cv) {
                certifiTextLayout.addView(textview((cvInfo.info_no+1) + ". " + cvInfo.info));
            }
        }

        //????????? ??????
        try {
            String filename = "profile_pic_" + pref.preferences.getString(SharedPreference.USER_ID, "") + ".jpg";
            String storage = getContext().getFilesDir() + "/" + pref.preferences.getString(SharedPreference.USER_ID, "");
            File storageDir = new File(getContext().getFilesDir() + "/" + pref.preferences.getString(SharedPreference.USER_ID, ""));
            if(!storageDir.exists()) {
                Log.d("profile Dir","????????? ?????? ?????? ?????? ??????");
                profile_pic.setImageResource(R.drawable.person);
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(storage+"/"+filename);
                profile_pic.setImageBitmap(bitmap);
            }
        } catch(Exception e) { // ????????? ?????? ?????? ??????
            Log.e("TAG",e.getMessage());
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
        } else if(v == setting){
            Intent intent = new Intent(getContext(), MyInfoActivity.class);
            startActivity(intent);
        }
    }

    public TextView textview(String a) {
        //TextView ??????
        TextView view = new TextView(context);
        view.setText(a);
        view.setTextSize(Dimension.SP, FONT_SIZE);
        view.setTextColor(Color.BLACK);

        //layout_width, layout_height, gravity ??????
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.gravity = Gravity.CENTER;
//        view1.setLayoutParams(lp);

        return view;
    }


}