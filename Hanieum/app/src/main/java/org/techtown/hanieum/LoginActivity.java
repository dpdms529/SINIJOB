package org.techtown.hanieum;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton kakaoLoginBtn;
    SignInButton googleLoginBtn;

    FirebaseAuth mAuth;

    private ActivityResultLauncher<Intent> resultLauncher;

    GoogleSignInClient mGoogleSignInClient;

    SharedPreference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        kakaoLoginBtn = findViewById(R.id.kakao_login_btn);
        googleLoginBtn = findViewById(R.id.google_login_btn);
        //구글 로그인 버튼 text변경
        TextView textView = (TextView)googleLoginBtn.getChildAt(0);
        textView.setText("Google 계정 로그인");

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            Intent intent = result.getData();
                            int CallType = intent.getIntExtra("CallType",0);
                            if(CallType == 0){
                                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                                try {
                                    GoogleSignInAccount account = task.getResult(ApiException.class);
                                    Log.d("TAG", "구글 로그인 인증 : " + account.getId());
                                    firebaseAuthWithGoogle(account.getIdToken());

                                }catch (ApiException e){
                                    Log.w("TAG", "구글 로그인 실패 ", e);
                                }
                            }

                        }
                    }
                }
        );

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        FirebaseUser gsa = mAuth.getCurrentUser();

        if(AuthApiClient.getInstance().hasToken()){
            UserApiClient.getInstance().accessTokenInfo((tokenInfo, error) -> {
                if(error != null){
                    Log.e("TAG", "카카오 토큰 정보 보기 실패", error);
                    UserApiClient.getInstance().logout(error2 -> {
                        if(error2 != null){
                            Log.e("TAG", "로그아웃 실패. SDK에서 토큰 삭제됨", error2);
                        }else{
                            Log.i("TAG", "로그아웃 성공. SDK에서 토큰 삭제됨");
                        }
                        return null;
                    });
                }else if(tokenInfo != null){
                    Log.i("TAG", "카카오 토큰 정보 보기 성공" + tokenInfo.getId());
                    Intent intent = new Intent(getApplicationContext(), InfoGetActivity.class);
                    startActivity(intent);
                    finish();
                }
                return null;
            });
        }

        if(gsa != null){
            Log.i("TAG", "구글 로그인 성공");
            Intent intent = new Intent(getApplicationContext(), InfoGetActivity.class);
            startActivity(intent);
            finish();
        }

        pref = new SharedPreference(getApplicationContext());

        kakaoLoginBtn.setOnClickListener(this);
        googleLoginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == kakaoLoginBtn){
            //카카오톡 로그인 가능한지 확인
            if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(getApplicationContext())){
                //카카오톡으로 로그인
                UserApiClient.getInstance().loginWithKakaoTalk(this, (token, error2) -> {
                    if(error2 != null){
                        Log.e("TAG", "카카오 로그인 실패 ", error2);
                    }else if(token != null){
                        Log.i("TAG", "카카오 로그인 성공 " + token.getAccessToken());
                        //사용자 정보 요청
                        UserApiClient.getInstance().me((user,error)->{
                            if(error != null){
                                Log.e("TAG", "카카오 사용자 정보 요청 실패", error);
                            }else if(user != null){
                                Log.i("TAG", "카카오 사용자 정보 요청 성공");
                                Log.i("TAG", "loginSuccess: "+user.getId());
                                Log.i("TAG", "loginSuccess: "+user.getKakaoAccount().getProfile().getNickname());
                                Log.i("TAG", "loginSuccess: "+user.getKakaoAccount().getGender());
                                Log.i("TAG", "loginSuccess: "+user.getKakaoAccount().getEmail());
                                //출생월일, 이메일, 성별 필수로 가져오려면 카카오 비즈 앱 등록 필요
                                //닉네임은 대체로 이름이지만 이름이 아닐수도..
                                //주소는 따로 받아야함
                                pref.editor.putString(SharedPreference.USER_ID, String.valueOf(user.getId()));
                                if (user.getKakaoAccount().getProfile().getNickname() != null) {
                                    pref.editor.putString(SharedPreference.NAME, user.getKakaoAccount().getProfile().getNickname());
                                }
                                if (user.getKakaoAccount().getGender() != null){
                                    pref.editor.putString(SharedPreference.GENDER, String.valueOf(user.getKakaoAccount().getGender()));
                                }
                                if (user.getKakaoAccount().getEmail() != null) {
                                    pref.editor.putString(SharedPreference.EMAIL, user.getKakaoAccount().getEmail());
                                }
                                pref.editor.commit();
                            }
                            return null;
                        });
                        Intent intent = new Intent(getApplicationContext(), InfoGetActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    return null;
                });
            }

        }else if(view == googleLoginBtn){
            //구글 로그인
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInIntent.putExtra("CallType",0);
            resultLauncher.launch(signInIntent);
        }
    }

    //구글 로그인 인증
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("TAG", "구글 로그인 인증 성공");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null){
                                Log.i("TAG", "구글 사용자 정보 요청 성공");
                                Log.i("TAG", "loginSuccess: "+ user.getUid());
                                Log.i("TAG", "loginSuccess: "+ user.getDisplayName());
                                Log.i("TAG", "loginSuccess: "+ user.getEmail());

                                pref.editor.putString(SharedPreference.USER_ID, user.getUid());
                                if (user.getDisplayName() != null){
                                    pref.editor.putString(SharedPreference.NAME, user.getDisplayName());
                                }
                                if (user.getEmail() != null) {
                                    pref.editor.putString(SharedPreference.EMAIL, user.getEmail());
                                }
                                pref.editor.commit();

                                Intent intent = new Intent(getApplicationContext(), InfoGetActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }else{
                            Log.w("TAG", "구글 로그인 인증 실패", task.getException() );
                        }
                    }
                });
    }

}