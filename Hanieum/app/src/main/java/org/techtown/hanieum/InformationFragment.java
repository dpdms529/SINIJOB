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
    FirebaseAuth mAuth;
    FirebaseUser gsa;

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
        logoutBtn = view.findViewById(R.id.logout_btn);
        disconnectBtn = view.findViewById(R.id.disconnect_btn);
        logoutBtn.setOnClickListener(this);
        disconnectBtn.setOnClickListener(this);
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
                                        Intent intent = new Intent(context, LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                }
                            });
                    break;
            }
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