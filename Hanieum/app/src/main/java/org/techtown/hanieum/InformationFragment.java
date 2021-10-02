package org.techtown.hanieum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;

import org.jetbrains.annotations.NotNull;

public class InformationFragment extends Fragment implements View.OnClickListener {
    Context context;

    Button logoutBtn,disconnectBtn;
    LinearLayout myInfo, termsOfService, privacyPolicy, faq, email;
    TextView name;

    FirebaseAuth mAuth;
    FirebaseUser gsa;

    SharedPreference pref;

    public InformationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        context = getContext();

        mAuth = FirebaseAuth.getInstance();
        gsa = mAuth.getCurrentUser();

        pref = new SharedPreference(context);

        logoutBtn = view.findViewById(R.id.logout_btn);
        disconnectBtn = view.findViewById(R.id.disconnect_btn);
        myInfo = view.findViewById(R.id.myInfo);
        termsOfService = view.findViewById(R.id.termsOfService);
        privacyPolicy = view.findViewById(R.id.privacyPolicy);
        faq = view.findViewById(R.id.FAQ);
        email = view.findViewById(R.id.email);
        name = view.findViewById(R.id.name);
        name.setText(pref.preferences.getString(SharedPreference.NAME,"")+"님 안녕하세요 :)");

        logoutBtn.setOnClickListener(this);
        disconnectBtn.setOnClickListener(this);
        myInfo.setOnClickListener(this);
        termsOfService.setOnClickListener(this);
        privacyPolicy.setOnClickListener(this);
        faq.setOnClickListener(this);
        email.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == logoutBtn){
            switch (loginMethod()){
                case 0: break;
                case 1: //카카오 로그아웃
                    UserApiClient.getInstance().logout((error2) -> {
                        if(error2 != null){
                            Log.e("TAG", "카카오 로그아웃 실패. SDK에서 토큰 삭제됨", error2);
                        }else{
                            Log.i("TAG", "카카오 로그아웃 성공. SDK에서 토큰 삭제됨");
                        }
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        return  null;
                    });
                    break;
                case 2: //구글 로그아웃
                    FirebaseAuth.getInstance().signOut();
                    Log.d("TAG", "구글 사용자 로그아웃");
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;
            }
        }else if(v == disconnectBtn){
            switch(loginMethod()){
                case 0: break;
                case 1: //카카오 탈퇴
                    UserApiClient.getInstance().unlink((error) -> {
                        if(error != null){
                            Log.e("TAG", "카카오 탈퇴 실패", error);
                        }else{
                            Log.i("TAG", "카카오 탈퇴 성공. SDK에서 토큰 삭제 됨");

                            // db에서 삭제
                            String php = getResources().getString(R.string.serverIP) + "user_del.php?user_id=" + pref.preferences.getString(SharedPreference.USER_ID, "");
                            URLConnector urlConnector = new URLConnector(php);
                            urlConnector.start();
                            try {
                                urlConnector.join();
                            } catch (InterruptedException e) {
                            }
                            
                            pref.editor.remove(SharedPreference.NAME);
                            pref.editor.remove(SharedPreference.BIRTH);
                            pref.editor.remove(SharedPreference.GENDER);
                            pref.editor.remove(SharedPreference.PHONE);
                            pref.editor.remove(SharedPreference.AGE);
                            pref.editor.remove(SharedPreference.EMAIL);
                            pref.editor.remove(SharedPreference.USER_ID);
                            pref.editor.remove(SharedPreference.STREET_CODE);
                            pref.editor.remove(SharedPreference.ADDRESS);
                            pref.editor.commit();

                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        return null;
                    });
                    break;
                case 2: //구글 탈퇴
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("TAG", "구글 사용자 탈퇴");
                                        pref.editor.remove(SharedPreference.NAME);
                                        pref.editor.remove(SharedPreference.BIRTH);
                                        pref.editor.remove(SharedPreference.GENDER);
                                        pref.editor.remove(SharedPreference.PHONE);
                                        pref.editor.remove(SharedPreference.AGE);
                                        pref.editor.remove(SharedPreference.EMAIL);
                                        pref.editor.remove(SharedPreference.USER_ID);
                                        pref.editor.remove(SharedPreference.STREET_CODE);
                                        pref.editor.remove(SharedPreference.ADDRESS);
                                        pref.editor.commit();

                                        // db에서 삭제
                                        String php = getResources().getString(R.string.serverIP) + "user_del.php?user_id=" + user.getUid();
                                        URLConnector urlConnector = new URLConnector(php);
                                        urlConnector.start();
                                        try {
                                            urlConnector.join();
                                        } catch (InterruptedException e) {
                                        }

                                        Intent intent = new Intent(context, LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                }
                            });
                    break;
            }
        }else if(v==myInfo){
            Intent intent = new Intent(context, MyInfoActivity.class);
            startActivity(intent);
        }else if(v==termsOfService){

        }else if(v==privacyPolicy){

        }else if(v==faq){

        }else if(v==email){

        }
    }
    private int loginMethod(){
        if(AuthApiClient.getInstance().hasToken()){
            return 1;   //카카오 회원
        }else if(gsa != null){
            return 2;   //구글 회원
        }
        return 0;   //일반 회원
    }
}