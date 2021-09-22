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
import com.kakao.sdk.user.UserApiClient;

import org.jetbrains.annotations.NotNull;

public class InformationFragment extends Fragment implements View.OnClickListener {
    Context context;
    Button logoutBtn,disconnectBtn;

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
        logoutBtn = view.findViewById(R.id.logout_btn);
        disconnectBtn = view.findViewById(R.id.disconnect_btn);
        logoutBtn.setOnClickListener(this);
        disconnectBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == logoutBtn){
//            //구글 로그아웃
//            FirebaseAuth.getInstance().signOut();
//            Log.d("TAG", "구글 사용자 로그아웃");
//            Intent intent = new Intent(context, LoginActivity.class);
//            startActivity(intent);
//            getActivity().finish();

            //카카오 로그아웃
            UserApiClient.getInstance().logout((error) -> {
                if(error != null){
                    Log.e("TAG", "카카오 로그아웃 실패. SDK에서 토큰 삭제됨", error);
                }else{
                    Log.i("TAG", "카카오 로그아웃 성공. SDK에서 토큰 삭제됨");
                }
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                return  null;
            });
        }else if(v == disconnectBtn){
//            //구글 탈퇴
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            user.delete()
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull @NotNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                Log.d("TAG", "구글 사용자 탈퇴");
//                                Intent intent = new Intent(context, LoginActivity.class);
//                                startActivity(intent);
//                                getActivity().finish();
//                            }
//                        }
//                    });

            //카카오 탈퇴
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
        }
    }
}