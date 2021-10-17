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
import com.kakao.sdk.user.model.Gender;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton kakaoLoginBtn;
    SignInButton googleLoginBtn;

    FirebaseAuth mAuth;

    private ActivityResultLauncher<Intent> resultLauncher;

    GoogleSignInClient mGoogleSignInClient;

    SharedPreference pref;

    boolean ismember = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        kakaoLoginBtn = findViewById(R.id.kakao_login_btn);
        googleLoginBtn = findViewById(R.id.google_login_btn);

        pref = new SharedPreference(getApplicationContext());

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

                    String php = getResources().getString(R.string.serverIP) + "user_read.php?user_id=" + pref.preferences.getString(SharedPreference.USER_ID, "");
                    URLConnector urlConnector = new URLConnector(php);
                    urlConnector.start();
                    try {
                        urlConnector.join();
                    } catch (InterruptedException e) {
                    }
                    String result = urlConnector.getResult();

                    Log.d("TAG", result);

                    Intent intent;
                    // db에 회원정보 없으면
                    if (result.contains("\"result\":[]")) {
                        pref.editor.putString(SharedPreference.ADDRESS, "");
                        pref.editor.commit();
                        intent = new Intent(getApplicationContext(), InfoGetActivity.class);
                    } else {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                    }

                    startActivity(intent);
                    finish();
                }
                return null;
            });
        }

        if(gsa != null){
            Log.i("TAG", "구글 로그인 성공");

            String php = getResources().getString(R.string.serverIP) + "user_read.php?user_id=" + pref.preferences.getString(SharedPreference.USER_ID, "");
            URLConnector urlConnector = new URLConnector(php);
            urlConnector.start();
            try {
                urlConnector.join();
            } catch (InterruptedException e) {
            }
            String result = urlConnector.getResult();

            Log.d("TAG", result);

            Intent intent;
            // db에 회원정보 없으면
            if (result.contains("\"result\":[]")) {
                pref.editor.putString(SharedPreference.ADDRESS, "");
                pref.editor.commit();
                Log.d("TAG", pref.preferences.getString(SharedPreference.ADDRESS,""));
                intent = new Intent(getApplicationContext(), InfoGetActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }

            startActivity(intent);
            finish();
        }

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
                                Log.i("TAG", "loginSuccess: "+user.getConnectedAt().getTime()  + " " + System.currentTimeMillis());
                                Log.i("TAG", "loginSuccess: "+user.getId());
                                Log.i("TAG", "loginSuccess: "+user.getKakaoAccount().getProfile().getNickname());
                                Log.i("TAG", "loginSuccess: "+user.getKakaoAccount().getGender());
                                Log.i("TAG", "loginSuccess: "+user.getKakaoAccount().getEmail());

                                Intent intent;

                                String php = getResources().getString(R.string.serverIP) + "user_read.php?user_id=" + user.getId();
                                URLConnector urlConnector = new URLConnector(php);
                                urlConnector.start();
                                try {
                                    urlConnector.join();
                                } catch (InterruptedException e) {
                                }
                                String result = urlConnector.getResult();

                                Log.d("TAG", result);

                                //회원가입한 경우
                                if(result.contains("\"result\":[]")){
                                    pref.editor.putString(SharedPreference.ADDRESS, "");
                                    pref.editor.putString(SharedPreference.USER_ID, String.valueOf(user.getId()));
                                    if (user.getKakaoAccount().getProfile().getNickname() != null) {
                                        pref.editor.putString(SharedPreference.NAME, user.getKakaoAccount().getProfile().getNickname());
                                    }
                                    if (user.getKakaoAccount().getGender() != null){
                                        if(user.getKakaoAccount().getGender().equals(Gender.FEMALE)){
                                            pref.editor.putString(SharedPreference.GENDER, "F");
                                        }else{
                                            pref.editor.putString(SharedPreference.GENDER, "M");
                                        }

                                    }
                                    if (user.getKakaoAccount().getEmail() != null) {
                                        pref.editor.putString(SharedPreference.EMAIL, user.getKakaoAccount().getEmail());
                                    }
                                    pref.editor.commit();
                                    intent = new Intent(getApplicationContext(), InfoGetActivity.class);
                                }else{
                                    try {
                                        Log.d("TAG", "db 정보 불러오기 시작");
                                        JSONObject jsonObject = new JSONObject(result);
                                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                        String user_id = jsonObject1.getString("user_id");
                                        String street_code = jsonObject1.getString("street_code");
                                        String main_no = jsonObject1.getString("main_no");
                                        String additional_no = jsonObject1.getString("additional_no");
                                        String name = jsonObject1.getString("name");
                                        String age = jsonObject1.getString("age");
                                        String gender = jsonObject1.getString("gender");
                                        String phone_number = jsonObject1.getString("phone_number");
                                        String email = jsonObject1.getString("email");
                                        String address = jsonObject1.getString("address");
                                        String birthday = jsonObject1.getString("birthday");

                                        pref.editor.putString(SharedPreference.USER_ID, user_id);
                                        pref.editor.putString(SharedPreference.STREET_CODE, street_code);
                                        pref.editor.putString(SharedPreference.MAIN_NO, main_no);
                                        pref.editor.putString(SharedPreference.ADDITIONAL_NO, additional_no);
                                        pref.editor.putString(SharedPreference.NAME, name);
                                        pref.editor.putString(SharedPreference.AGE, age);
                                        pref.editor.putString(SharedPreference.GENDER, gender);
                                        pref.editor.putString(SharedPreference.PHONE, phone_number);
                                        pref.editor.putString(SharedPreference.EMAIL, email);
                                        pref.editor.putString(SharedPreference.ADDRESS, address);
                                        pref.editor.putString(SharedPreference.BIRTH, birthday);
                                        pref.editor.commit();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    String AddressPhp = getResources().getString(R.string.serverIP) + "address_read.php?" +
                                            "street_code=" + pref.preferences.getString(SharedPreference.STREET_CODE, "") +
                                            "&main_no=" + pref.preferences.getString(SharedPreference.MAIN_NO, "") +
                                            "&additional_no=" + pref.preferences.getString(SharedPreference.ADDITIONAL_NO, "");
                                    URLConnector AddressUrlConnector = new URLConnector(AddressPhp);
                                    AddressUrlConnector.start();
                                    try {
                                        AddressUrlConnector.join();
                                    } catch (InterruptedException e) {
                                    }
                                    String AddressResult = AddressUrlConnector.getResult();
                                    Log.d("TAG", AddressResult);
                                    try {
                                        JSONObject jsonObject = new JSONObject(AddressResult);
                                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                        String x = jsonObject1.getString("x");
                                        String y = jsonObject1.getString("y");

                                        pref.editor.putString(SharedPreference.X, x);
                                        pref.editor.putString(SharedPreference.Y, y);
                                        pref.editor.commit();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Log.d("TAG", "db 정보 불러오기 끝");

                                    intent = new Intent(getApplicationContext(), MainActivity.class);
                                }
                                startActivity(intent);
                                finish();
                            }
                            return null;
                        });

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
                                Log.i("TAG", "loginSuccess: "+ user.getMetadata().getCreationTimestamp() + " " + System.currentTimeMillis());

                                String php = getResources().getString(R.string.serverIP) + "user_read.php?user_id=" + user.getUid();
                                URLConnector urlConnector = new URLConnector(php);
                                urlConnector.start();
                                try {
                                    urlConnector.join();
                                } catch (InterruptedException e) {
                                }
                                String result = urlConnector.getResult();

                                Log.d("TAG", result);

                                Intent intent;
                                if(result.contains("\"result\":[]")){
                                    pref.editor.putString(SharedPreference.ADDRESS, "");
                                    pref.editor.putString(SharedPreference.USER_ID, user.getUid());
                                    if (user.getDisplayName() != null){
                                        pref.editor.putString(SharedPreference.NAME, user.getDisplayName());
                                    }
                                    if (user.getEmail() != null) {
                                        pref.editor.putString(SharedPreference.EMAIL, user.getEmail());
                                    }
                                    pref.editor.commit();

                                    intent = new Intent(getApplicationContext(), InfoGetActivity.class);
                                }else{
                                    try {
                                        Log.d("TAG", "db 정보 불러오기 시작");
                                        JSONObject jsonObject = new JSONObject(result);
                                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                        String user_id = jsonObject1.getString("user_id");
                                        String street_code = jsonObject1.getString("street_code");
                                        String main_no = jsonObject1.getString("main_no");
                                        String additional_no = jsonObject1.getString("additional_no");
                                        String name = jsonObject1.getString("name");
                                        String age = jsonObject1.getString("age");
                                        String gender = jsonObject1.getString("gender");
                                        String phone_number = jsonObject1.getString("phone_number");
                                        String email = jsonObject1.getString("email");
                                        String address = jsonObject1.getString("address");
                                        String birthday = jsonObject1.getString("birthday");

                                        pref.editor.putString(SharedPreference.USER_ID, user_id);
                                        pref.editor.putString(SharedPreference.STREET_CODE, street_code);
                                        pref.editor.putString(SharedPreference.MAIN_NO, main_no);
                                        pref.editor.putString(SharedPreference.ADDITIONAL_NO, additional_no);
                                        pref.editor.putString(SharedPreference.NAME, name);
                                        pref.editor.putString(SharedPreference.AGE, age);
                                        pref.editor.putString(SharedPreference.GENDER, gender);
                                        pref.editor.putString(SharedPreference.PHONE, phone_number);
                                        pref.editor.putString(SharedPreference.EMAIL, email);
                                        pref.editor.putString(SharedPreference.ADDRESS, address);
                                        pref.editor.putString(SharedPreference.BIRTH, birthday);
                                        pref.editor.commit();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    String AddressPhp = getResources().getString(R.string.serverIP) + "address_read.php?" +
                                            "street_code=" + pref.preferences.getString(SharedPreference.STREET_CODE, "") +
                                            "main_no=" + pref.preferences.getString(SharedPreference.MAIN_NO, "") +
                                            "additional_no=" + pref.preferences.getString(SharedPreference.ADDITIONAL_NO, "");
                                    URLConnector AddressUrlConnector = new URLConnector(AddressPhp);
                                    AddressUrlConnector.start();
                                    try {
                                        AddressUrlConnector.join();
                                    } catch (InterruptedException e) {
                                    }
                                    String AddressResult = AddressUrlConnector.getResult();
                                    Log.d("TAG", AddressResult);
                                    try {
                                        JSONObject jsonObject = new JSONObject(AddressResult);
                                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                        String x = jsonObject1.getString("x");
                                        String y = jsonObject1.getString("y");

                                        pref.editor.putString(SharedPreference.X, x);
                                        pref.editor.putString(SharedPreference.Y, y);
                                        pref.editor.commit();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Log.d("TAG", "db 정보 불러오기 끝");

                                    intent = new Intent(getApplicationContext(), MainActivity.class);
                                }
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