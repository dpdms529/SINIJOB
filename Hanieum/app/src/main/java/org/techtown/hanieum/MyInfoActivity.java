package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {
    EditText name, address, phone, email;
    TextView birth;
    Spinner gender;
    Button saveButton;
    WebView webView;

    Handler handler;

    ArrayList<String> items = new ArrayList<>();

    SharedPreference pref;

    String streetCode;

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

        name.setText(pref.preferences.getString(SharedPreference.NAME,""));
        birth.setText(pref.preferences.getString(SharedPreference.BIRTH,""));
        address.setText(pref.preferences.getString(SharedPreference.ADDRESS,""));
        phone.setText(pref.preferences.getString(SharedPreference.PHONE,""));
        email.setText(pref.preferences.getString(SharedPreference.EMAIL,""));
        if(pref.preferences.getString(SharedPreference.GENDER,"").equals("M")){
            gender.setSelection(0);
        }else{
            gender.setSelection(1);
        }

        birth.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        address.setOnClickListener(this);

        handler = new Handler();
    }

    @Override
    public void onClick(View v) {
        if(v==birth){
            int year = Integer.parseInt(birth.getText().subSequence(0,4).toString());
            int month = Integer.parseInt(birth.getText().subSequence(4,6).toString())-1;
            int day = Integer.parseInt(birth.getText().subSequence(6,8).toString());
            DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    StringBuilder sb = new StringBuilder(String.valueOf(year));
                    if (month < 9) {
                        sb.append("0"+(month+1));
                    }else{
                        sb.append(month+1);
                    }
                    if(day < 10){
                        sb.append("0"+day);
                    }else{
                        sb.append(day);
                    }
                    birth.setText(sb.toString());
                }
            };
            DatePickerDialog oDialog = new DatePickerDialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog,mDateSetListener,year,month,day);
            oDialog.show();
        }else if(v == address){
            init_webView();
        } else if (v == saveButton) {
            String tmpGender;
            if (gender.getSelectedItem() == "남") {
                tmpGender = "M";
            } else {
                tmpGender = "F";
            }
            int year = Integer.parseInt(birth.getText().toString().substring(0, 4));
            Calendar calendar = new GregorianCalendar();
            pref.editor.putString(SharedPreference.NAME, name.getText().toString());
            pref.editor.putInt(SharedPreference.AGE, calendar.get(Calendar.YEAR) - year + 1);
            pref.editor.putString(SharedPreference.GENDER, tmpGender);
            pref.editor.putString(SharedPreference.PHONE, phone.getText().toString());
            pref.editor.putString(SharedPreference.EMAIL, email.getText().toString());
            pref.editor.putString(SharedPreference.BIRTH, birth.getText().toString());
            pref.editor.putString(SharedPreference.STREET_CODE,streetCode);
            pref.editor.putString(SharedPreference.ADDRESS, address.getText().toString());
            pref.editor.commit();
            String kakaoApi = getResources().getString(R.string.serverIP) + "kakao_api.php?" +
                    "street_code="+pref.preferences.getString(SharedPreference.STREET_CODE,"")+
                    "&address="+pref.preferences.getString(SharedPreference.ADDRESS,"");
            URLConnector urlConnector = new URLConnector(kakaoApi);
            urlConnector.start();
            try {
                urlConnector.join();
            } catch (InterruptedException e) {
            }
            String result = urlConnector.getResult();
            Log.d("TAG", "onClick: "+result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String x = jsonObject1.getString("x");
                    String y = jsonObject1.getString("y");
                    String main_no = jsonObject1.getString("main_no");
                    String additional_no = jsonObject1.getString("additional_no");
                    pref.editor.putString(SharedPreference.X,x);
                    pref.editor.putString(SharedPreference.Y,y);
                    pref.editor.putString(SharedPreference.MAIN_NO,main_no);
                    pref.editor.putString(SharedPreference.ADDITIONAL_NO,additional_no);
                    pref.editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("TAG","user_id="+pref.preferences.getString(SharedPreference.USER_ID, "")+
                    "&street_code="+pref.preferences.getString(SharedPreference.STREET_CODE,"")+
                    "&main_no="+pref.preferences.getString(SharedPreference.MAIN_NO,"")+
                    "&additional_no="+pref.preferences.getString(SharedPreference.ADDITIONAL_NO,"") +
                    "&name="+pref.preferences.getString(SharedPreference.NAME, "")+
                    "&age="+pref.preferences.getInt(SharedPreference.AGE, 0)+
                    "&gender="+pref.preferences.getString(SharedPreference.GENDER, "")+
                    "&phone_number="+pref.preferences.getString(SharedPreference.PHONE, "")+
                    "&email="+pref.preferences.getString(SharedPreference.EMAIL, "none")+
                    "&address="+pref.preferences.getString(SharedPreference.ADDRESS,"")  +
                    "&birthday="+pref.preferences.getString(SharedPreference.BIRTH, "") );

            String php = getResources().getString(R.string.serverIP) + "user_update.php?" +
                    "user_id="+pref.preferences.getString(SharedPreference.USER_ID, "")+
                    "&street_code="+pref.preferences.getString(SharedPreference.STREET_CODE,"")+
                    "&main_no="+pref.preferences.getString(SharedPreference.MAIN_NO,"")+
                    "&additional_no="+pref.preferences.getString(SharedPreference.ADDITIONAL_NO,"") +
                    "&name="+pref.preferences.getString(SharedPreference.NAME, "")+
                    "&age="+pref.preferences.getInt(SharedPreference.AGE, 0)+
                    "&gender="+pref.preferences.getString(SharedPreference.GENDER, "")+
                    "&phone_number="+pref.preferences.getString(SharedPreference.PHONE, "")+
                    "&email="+pref.preferences.getString(SharedPreference.EMAIL, "none")+
                    "&address="+pref.preferences.getString(SharedPreference.ADDRESS,"")  +
                    "&birthday="+pref.preferences.getString(SharedPreference.BIRTH, "");
            URLConnector urlConnectorBookmark = new URLConnector(php);
            urlConnectorBookmark.start();
            try {
                urlConnectorBookmark.join();
            } catch (InterruptedException e) {
            }
            finish();
            Log.d("TAG", "내 정보 수정 성공");
        }
    }

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
                    Log.d("TAG", "run: "+sigunguCode + roadnameCode);
                }
            });
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