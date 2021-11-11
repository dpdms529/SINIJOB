package org.techtown.hanieum;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {
    EditText name, address, phone, email;
    EditText birth;
    Spinner gender;
    Button saveButton;
    WebView webView;
    ImageView picture;

    Handler handler;

    ArrayList<String> items = new ArrayList<>();

    SharedPreference pref;

    String streetCode;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        pref = new SharedPreference(getApplicationContext());

        name = findViewById(R.id.name);
        birth = findViewById(R.id.birth);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        gender = findViewById(R.id.gender);
        saveButton = findViewById(R.id.saveButton);
        webView = findViewById(R.id.webview);
        picture = findViewById(R.id.imageView3);

        items.add("남");
        items.add("여");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String prefBirth = pref.preferences.getString(SharedPreference.BIRTH,"");
        String year = prefBirth.substring(0, 4);
        String mon = prefBirth.substring(4, 6);
        String day = prefBirth.substring(6);
        try {
            String filename = "profile_pic.jpg";
            String storage = getFilesDir() + "/" + pref.preferences.getString(SharedPreference.USER_ID, "");
            File storageDir = new File(getFilesDir() + "/" + pref.preferences.getString(SharedPreference.USER_ID, ""));
            Bitmap bitmap = BitmapFactory.decodeFile(storage+"/"+filename);
            picture.setImageBitmap(bitmap);
            String[] fileList = storageDir.list();
            for(int i=0;i< fileList().length;i++) {
                Log.e("profile Dir",fileList[i]);
            }
        } catch(Exception e) { // 프로필 사진 없는 경우
            Log.e("TAG","프로필 사진 없음");
            picture.setImageResource(R.drawable.person);
        }
        if(picture==null) {
            picture.setImageResource(R.drawable.person);
        }

        name.setText(pref.preferences.getString(SharedPreference.NAME,""));
        birth.setText(year + "년 " + mon + "월 " + day + "일");
        address.setText(pref.preferences.getString(SharedPreference.ADDRESS,""));
        phone.setText(pref.preferences.getString(SharedPreference.PHONE,""));
        email.setText(pref.preferences.getString(SharedPreference.EMAIL,""));
        if(pref.preferences.getString(SharedPreference.GENDER,"").equals("M")){
            gender.setSelection(0);
        }else{
            gender.setSelection(1);
        }
        streetCode = pref.preferences.getString(SharedPreference.STREET_CODE,"");

        birth.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        address.setOnClickListener(this);
        picture.setOnClickListener(this);

        handler = new Handler();
    }

    @Override
    public void onClick(View v) {
        if(v==birth){
            int year = Integer.parseInt(birth.getText().subSequence(0,4).toString());
            int month = Integer.parseInt(birth.getText().subSequence(6,8).toString())-1;
            int day = Integer.parseInt(birth.getText().subSequence(10,12).toString());
            DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    StringBuilder sb = new StringBuilder(String.valueOf(year));
                    if (month < 9) {
                        sb.append("년 0"+(month+1)+"월 ");
                    }else{
                        sb.append("년 "+(month+1)+"월 ");
                    }
                    if(day < 10){
                        sb.append("0"+day+"일");
                    }else{
                        sb.append(day+"일");
                    }
                    birth.setText(sb.toString());
                }
            };
            DatePickerDialog oDialog = new DatePickerDialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog,mDateSetListener,year,month,day);
            oDialog.show();
        } else if(v == address){
            init_webView();
        } else if(v == picture) {
            List<CharSequence> items = new ArrayList<>();
            items.add("사진 삭제");
            items.add("앨범에서 선택");
            CharSequence[] charSequences = items.toArray(new CharSequence[items.size()]);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("프로필 사진 변경");
            alertDialog.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (charSequences[which] == "앨범에서 선택") {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        launcher.launch(intent);
                    } else if (charSequences[which] == "사진 삭제") {
                        picture.setImageResource(R.drawable.person);
                        bitmap = null;
                    }
                }
            });
            alertDialog.show();
        } else if (v == saveButton) {
            if (name.getText().length() == 0) {
                Toast.makeText(getApplicationContext(), "이름을 입력하세요", Toast.LENGTH_SHORT).show();
            } else if (phone.getText().length() < 11) {
                Toast.makeText(getApplicationContext(), "전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
            } else if (email.getText().length() == 0) {
                Toast.makeText(getApplicationContext(), "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                String tmpGender;
                if (gender.getSelectedItem() == "남") {
                    tmpGender = "M";
                } else {
                    tmpGender = "F";
                }
                try {
                    // 프로필 사진 저장
                    saveProfilePic();
                } catch (Exception e) { // 기본 프로필 사진일 경우
                    e.printStackTrace();
                }
                int year = Integer.parseInt(birth.getText().toString().substring(0, 4));
                String month = birth.getText().subSequence(6, 8).toString();
                String day = birth.getText().subSequence(10, 12).toString();
                Calendar calendar = new GregorianCalendar();
                pref.editor.putString(SharedPreference.NAME, name.getText().toString());
                pref.editor.putString(SharedPreference.AGE, String.valueOf(calendar.get(Calendar.YEAR) - year + 1));
                pref.editor.putString(SharedPreference.GENDER, tmpGender);
                pref.editor.putString(SharedPreference.PHONE, phone.getText().toString());
                pref.editor.putString(SharedPreference.EMAIL, email.getText().toString());
                pref.editor.putString(SharedPreference.BIRTH, year + month + day);
                pref.editor.putString(SharedPreference.STREET_CODE, streetCode);
                pref.editor.putString(SharedPreference.ADDRESS, address.getText().toString());
                pref.editor.commit();
                String kakaoApi = getResources().getString(R.string.serverIP) + "kakao_api.php?" +
                        "street_code=" + pref.preferences.getString(SharedPreference.STREET_CODE, "") +
                        "&address=" + pref.preferences.getString(SharedPreference.ADDRESS, "");
                URLConnector urlConnector = new URLConnector(kakaoApi);
                urlConnector.start();
                try {
                    urlConnector.join();
                } catch (InterruptedException e) {
                }
                String result = urlConnector.getResult();
                Log.d("TAG", "onClick: " + result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String x = jsonObject1.getString("x");
                        String y = jsonObject1.getString("y");
                        String main_no = jsonObject1.getString("main_no");
                        String additional_no = jsonObject1.getString("additional_no");
                        pref.editor.putString(SharedPreference.X, x);
                        pref.editor.putString(SharedPreference.Y, y);
                        pref.editor.putString(SharedPreference.MAIN_NO, main_no);
                        pref.editor.putString(SharedPreference.ADDITIONAL_NO, additional_no);
                        pref.editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("TAG", "user_id=" + pref.preferences.getString(SharedPreference.USER_ID, "") +
                        "&street_code=" + pref.preferences.getString(SharedPreference.STREET_CODE, "") +
                        "&main_no=" + pref.preferences.getString(SharedPreference.MAIN_NO, "") +
                        "&additional_no=" + pref.preferences.getString(SharedPreference.ADDITIONAL_NO, "") +
                        "&name=" + pref.preferences.getString(SharedPreference.NAME, "") +
                        "&age=" + pref.preferences.getString(SharedPreference.AGE, "") +
                        "&gender=" + pref.preferences.getString(SharedPreference.GENDER, "") +
                        "&phone_number=" + pref.preferences.getString(SharedPreference.PHONE, "") +
                        "&email=" + pref.preferences.getString(SharedPreference.EMAIL, "none") +
                        "&address=" + pref.preferences.getString(SharedPreference.ADDRESS, "") +
                        "&birthday=" + pref.preferences.getString(SharedPreference.BIRTH, ""));

                String php = getResources().getString(R.string.serverIP) + "user_update.php?" +
                        "user_id=" + pref.preferences.getString(SharedPreference.USER_ID, "") +
                        "&street_code=" + pref.preferences.getString(SharedPreference.STREET_CODE, "") +
                        "&main_no=" + pref.preferences.getString(SharedPreference.MAIN_NO, "") +
                        "&additional_no=" + pref.preferences.getString(SharedPreference.ADDITIONAL_NO, "") +
                        "&name=" + pref.preferences.getString(SharedPreference.NAME, "") +
                        "&age=" + pref.preferences.getString(SharedPreference.AGE, "") +
                        "&gender=" + pref.preferences.getString(SharedPreference.GENDER, "") +
                        "&phone_number=" + pref.preferences.getString(SharedPreference.PHONE, "") +
                        "&email=" + pref.preferences.getString(SharedPreference.EMAIL, "none") +
                        "&address=" + pref.preferences.getString(SharedPreference.ADDRESS, "") +
                        "&birthday=" + pref.preferences.getString(SharedPreference.BIRTH, "");
                URLConnector urlConnectorUser = new URLConnector(php);
                urlConnectorUser.start();
                try {
                    urlConnectorUser.join();
                } catch (InterruptedException e) {
                }
                finish();
                Log.d("TAG", "내 정보 수정 성공");
            }
        }
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Uri uri = intent.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            picture.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    public void init_webView(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.addJavascriptInterface(new AndroidBridge(),"hanium");
        webView.setWebViewClient(client);
        webView.setWebChromeClient(chromeClient);
        webView.loadUrl(getResources().getString(R.string.serverIP)+"/address_api.php");
    }

    private class AndroidBridge{
        @JavascriptInterface
        public void getAddress(String sigunguCode, String roadnameCode, String roadAddress, String buildingName){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(buildingName.equals("")){
                        address.setText(roadAddress);
                    }else{
                        address.setText(roadAddress+", "+buildingName);
                    }
                    streetCode = sigunguCode + roadnameCode;
                    Log.d("TAG", "run: "+sigunguCode + " " +  roadnameCode);
                    Log.d("TAG", "run: " + roadAddress +  buildingName);
                }
            });
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
//        String temp = byteArrayToBinaryString(b);
        return temp;
    }

    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<b.length; i++) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for(int bit = 0; bit<8; bit++) {
            if(((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    public Bitmap StringToBitMap(String encodedString) {
        Log.e("StringToBitMap",encodedString);
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Log.e("StringToBitMap",encodeByte.toString());
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Log.e("StringToBitMap","bitmap : "+bitmap);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            Log.e("StringToBitMap",e.getMessage());
            return null;
        }
    }

    private void saveProfilePic() {
        try {
            File storageDir = new File(getFilesDir() + "/" + pref.preferences.getString(SharedPreference.USER_ID, ""));
            if(!storageDir.exists()) {
                storageDir.mkdirs();
            }
            String filename = "profile_pic.jpg";

            File file = new File(storageDir, filename);
            boolean deleted = file.delete();
            Log.d("TAG", "Delete duplication check :"+deleted);
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(file);
                BitmapDrawable drawable = (BitmapDrawable) picture.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert output != null;
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.e("TAG","Profile picture saved");
            String[] fileList = storageDir.list();
            for(int i=0;i< fileList().length;i++) {
                Log.e("profile Dir",fileList[i]);
            }
        } catch (Exception e) {
            Log.e("TAG","Profile picture saving error");
        }

    }

    WebViewClient client = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    };

    WebChromeClient chromeClient = new WebChromeClient(){
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(MyInfoActivity.this);
            newWebView.getSettings().setJavaScriptEnabled(true);
            Dialog dialog = new Dialog(MyInfoActivity.this);
            dialog.setContentView(newWebView);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(params);
            dialog.show();

            newWebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    super.onJsAlert(view, url, message, result);
                    return true;
                }

                @Override
                public void onCloseWindow(WebView window) {
                    dialog.dismiss();
                }
            });
            ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);
            resultMsg.sendToTarget();

            return true;
        }
    };
}