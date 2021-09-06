package org.techtown.hanieum;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.text.SimpleDateFormat;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        MapView.POIItemEventListener, MapView.MapViewEventListener {
    //    Toolbar toolbar;
    MapView mapView;
    ImageButton helpButton; // 도움말 버튼
    Button applyButton;
    Button findWay;
    Button goWorknetBtn;
    Button shareButton;
    Button summaryButton;
    ImageView goWorknetImg;
    TextView companyNameDetail;
    TextView titleDetail;
    TextView workFormDetail;
    TextView schoolDetail;
    TextView careerDetail;
    ScrollView scrollView;
    SeekBar textSizeChangeBar;
    TextView textSize;
    Switch voiceTf;

    String epX;
    String epY;
    String url;
    String contact;
    Context context;
    SharedPreference pref;

    //음성 출력용
    TextToSpeech tts;

    ArrayList<TextView> attachFileUrl = new ArrayList<>(); //접수방법_제출서류양식

    HashMap<String, TextView> address = new HashMap<String, TextView>() {{ //근무예정지
        put("addressT", null);
        put("addressDetailT", null);
        put("addressDetail", null);  //주소
    }};

    HashMap<String, TextView> recruitCd = new HashMap<String, TextView>() {{   //모집조건
        put("recruitCdT", null);
        put("jobsNmT", null);
        put("jobsNm", null); //모집직종
        put("jobsContT", null);
        put("jobCont", null); //직무내용
        put("enterTpNmT", null);
        put("enterTpNm", null); //경력조건
        put("eduNmT", null);
        put("eduNm", null); //학력조건
        put("empTpNmT", null);
        put("empTpNm", null); //고용형태
        put("collectPsncntT", null);
        put("collectPsncnt", null); //모집인원
        put("etcHopeContT", null);
        put("etcHopeCont", null); //기타안내
    }};

    HashMap<String, TextView> workCd = new HashMap<String, TextView>() {{  //근무조건
        put("workCdT", null);
        put("salaryT", null);
        put("salaryTypeCode", null); //임금조건(연,월,일,시)
        put("salary", null); //임금조건(금액)
        put("workTimeT", null);
        put("workTime", null); //근무시간
        put("workDayT", null);
        put("workDay", null); //근무형태
        put("retirepayT", null);
        put("retirepay", null); //퇴직급여
        put("fourInsT", null);
        put("fourIns", null); //연금4대보험
        put("etcWelfareT", null);
        put("etcWelfare", null); //기타복리후생
    }};

    HashMap<String, TextView> apply = new HashMap<String, TextView>() {{   //접수방법
        put("applyT", null);
        put("receiptCloseDtT", null);
        put("receiptCloseDt", null); //접수마감일
        put("selMthdT", null);
        put("selMthd", null); //전형방법
        put("rcptMthdT", null);
        put("rcptMthd", null); //접수방법
        put("submitDocT", null);
        put("submitDoc", null); //제출서류 준비물
        put("attachFileUrlT", null);
    }};

    HashMap<String, TextView> prefer = new HashMap<String, TextView>() {{  //우대사항
        put("preferT", null);
        put("pfCondT", null);
        put("pfCond", null); //우대조건
        put("etcPfCondT", null);
        put("etcPfCond", null); //기타우대사항
        put("certificateT", null);
        put("certificate", null); //자격면허
        put("compAblT", null);
        put("compAbl", null); //컴퓨터활용능력
    }};

    HashMap<String, TextView> corp = new HashMap<String, TextView>() {{    //기업정보
        put("corpT", null);
        put("corpNmT", null);
        put("corpNm", null); //기업명
        put("reperNmT", null);
        put("reperNm", null); //대표자명
        put("indTpCdNmT", null);
        put("indTpCdNm", null); //업종
        put("corpAddrT", null);
        put("corpAddr", null); //주소
        put("totPsncntT", null);
        put("totPsncnt", null); //근로자수
        put("yrSalesAmtT", null);
        put("yrSalesAmt", null); //연매출액
    }};

    String[] recruitStr = {"jobsNm", "jobCont", "enterTpNm", "eduNm", "empTpNm", "collectPsncnt", "etcHopeCont"};
    String[] workStr = {"salaryTypeCode", "salary", "workTime", "workDay", "retirepay", "fourIns", "etcWelfare"};
    String[] applyStr = {"receiptCloseDt", "selMthd", "rcptMthd", "submitDoc"};
    String[] preferStr = {"pfCond", "etcPfCond", "compAbl"};
    String[] corpStr = {"corpNm", "reperNm", "indTpCdNm", "corpAddr", "totPsncnt", "yrSalesAmt"};

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        context = this;

//        toolbar = findViewById(R.id.toolbar0201);
        helpButton = findViewById(R.id.helpButton);
        applyButton = findViewById(R.id.applyButton);
        findWay = findViewById(R.id.findWay);
        goWorknetBtn = findViewById(R.id.goWorknetBtn);
        shareButton = findViewById(R.id.shareButton);
        goWorknetImg = findViewById(R.id.goWorknetImg);
        companyNameDetail = findViewById(R.id.companyNameDetail);
        titleDetail = findViewById(R.id.titleDetail);
        workFormDetail = findViewById(R.id.workFormDetail);
        schoolDetail = findViewById(R.id.schoolDetail);
        careerDetail = findViewById(R.id.careerDetail);
        scrollView = findViewById(R.id.detailScrollView);
        textSizeChangeBar = findViewById(R.id.textSizeChangeBar);
        textSize = findViewById(R.id.textSize);
        voiceTf = findViewById(R.id.voiceTf);
        summaryButton = findViewById(R.id.voice_summary);

        pref = new SharedPreference(getApplicationContext());

        // 음성출력 생성, 리스너 초기화
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        for (String key : address.keySet()) {    //동적으로 ID부여
            String addressId = key;
            int resId = getResources().getIdentifier(addressId, "id", getApplicationContext().getPackageName());
            address.replace(key, findViewById(resId));
        }
        for (String key : recruitCd.keySet()) {    //동적으로 ID부여
            String recruitCdId = key;
            int resId = getResources().getIdentifier(recruitCdId, "id", getApplicationContext().getPackageName());
            recruitCd.replace(key, findViewById(resId));
        }
        for (String key : workCd.keySet()) {    //동적으로 ID부여
            String workCdId = key;
            int resId = getResources().getIdentifier(workCdId, "id", getApplicationContext().getPackageName());
            workCd.replace(key, findViewById(resId));
        }
        for (String key : apply.keySet()) {    //동적으로 ID부여
            String applyId = key;
            int resId = getResources().getIdentifier(applyId, "id", getApplicationContext().getPackageName());
            apply.replace(key, findViewById(resId));
        }
        for (int i = 1; i <= 5; i++) {    //동적으로 ID부여
            String attachFileUrlId = "attachFileUrl" + i;
            int resId = getResources().getIdentifier(attachFileUrlId, "id", getApplicationContext().getPackageName());
            attachFileUrl.add(findViewById(resId));
        }
        for (String key : prefer.keySet()) {    //동적으로 ID부여
            String preferId = key;
            int resId = getResources().getIdentifier(preferId, "id", getApplicationContext().getPackageName());
            prefer.replace(key, findViewById(resId));
        }
        for (String key : corp.keySet()) {    //동적으로 ID부여
            String corpId = key;
            int resId = getResources().getIdentifier(corpId, "id", getApplicationContext().getPackageName());
            corp.replace(key, findViewById(resId));
        }

        // 글씨 크기 설정
        int size = pref.preferences.getInt(SharedPreference.TEXT_SIZE, 20);
        textSizeChangeBar.setProgress(size);
        textSize.setText(String.valueOf(size));
        for (String key : address.keySet()) {
            address.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }
        for (String key : recruitCd.keySet()) {
            recruitCd.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }
        for (String key : workCd.keySet()) {
            workCd.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }
        for (String key : apply.keySet()) {
            apply.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }
        for (int i = 0; i < attachFileUrl.size(); i++) {
            attachFileUrl.get(i).setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }
        for (String key : prefer.keySet()) {
            prefer.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }
        for (String key : corp.keySet()) {
            corp.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map);
        mapViewContainer.addView(mapView);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        loadData(id);

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);

        helpButton.setOnClickListener(this);
        applyButton.setOnClickListener(this);
        findWay.setOnClickListener(this);
        goWorknetBtn.setOnClickListener(this);
        goWorknetImg.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        summaryButton.setOnClickListener(this);
        companyNameDetail.setOnClickListener(this);
        titleDetail.setOnClickListener(this);
        workFormDetail.setOnClickListener(this);
        schoolDetail.setOnClickListener(this);
        careerDetail.setOnClickListener(this);
        address.get("addressDetail").setOnClickListener(this);
        for (int i = 0; i < recruitStr.length; i++) {
            recruitCd.get(recruitStr[i]).setOnClickListener(this);
        }
        for (int i = 0; i < workStr.length; i++) {
            workCd.get(workStr[i]).setOnClickListener(this);
        }
        for (int i = 0; i < applyStr.length; i++) {
            apply.get(applyStr[i]).setOnClickListener(this);
        }
        for (int i = 0; i < preferStr.length; i++) {
            prefer.get(preferStr[i]).setOnClickListener(this);
        }
        for (int i = 0; i < corpStr.length; i++) {
            corp.get(corpStr[i]).setOnClickListener(this);
        }

        textSizeChangeBar.setOnSeekBarChangeListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == applyButton) {
            // 접수 방법 알림 다이얼로그
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("접수 방법을 확인해주세요\n");
            alertDialog.setMessage(apply.get("rcptMthd").getText());
            alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 아이템 추가
                    List<CharSequence> items = new ArrayList<>();
                    if (!contact.equals("")) {      // 전화번호 있을 경우
                        items.add("전화");
                    }
                    items.add("문자");
                    items.add("이메일");
                    items.add("워크넷");
                    CharSequence[] charSequences = items.toArray(new CharSequence[items.size()]);

                    AlertDialog.Builder applyDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
                    applyDialog.setTitle("지원 유형을 선택하세요");
                    applyDialog.setItems(charSequences, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (charSequences[which] == "전화") {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + contact));
                                startActivity(intent);
                            } else if (charSequences[which] == "문자") {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"));
//                                intent.setType("vnd.android-dir/mms-sms");
//                                intent.putExtra("address", "");
//                                intent.putExtra("sms_body", companyNameDetail.getText()+"에 지원합니다.");
//                                startActivity(intent);

                                Intent intent = new Intent(context, ApplyActivity.class);
                                intent.putExtra("type", "sms");
                                intent.putExtra("company", companyNameDetail.getText());
                                startActivity(intent);
                            } else if (charSequences[which] == "이메일") {
//                                String uriText = "mailto:" + "?subject=" +
//                                        Uri.encode(companyNameDetail.getText()+"에 지원합니다.") + "&body=" + Uri.encode("");
//                                Uri uri = Uri.parse(uriText);
//
//                                Intent intent = new Intent(Intent.ACTION_SENDTO);
//                                intent.setData(uri);
//                                startActivity(Intent.createChooser(intent, "이메일 앱을 선택하세요"));

                                Intent intent = new Intent(context, ApplyActivity.class);
                                intent.putExtra("type", "email");
                                intent.putExtra("company", companyNameDetail.getText());
                                startActivity(intent);
                            } else if (charSequences[which] == "워크넷") {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            }
                        }
                    });
                    applyDialog.show();
                }
            });
            alertDialog.show();
        } else if (v == findWay) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://route?sp=37.537229,127.005515&ep=" + epY + "," + epX + "&by=CAR"));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.daum.android.map")));
            }
        } else if (v == goWorknetBtn) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else if (v == goWorknetImg) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.work.go.kr"));
            startActivity(intent);
        } else if (v == shareButton) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String shareContent = "제목 : " + titleDetail.getText().toString() + "\n기업명 : " + companyNameDetail.getText().toString() + "\n접수마감일 : " + apply.get("receiptCloseDt").getText().toString() + "\n" + url;
            intent.putExtra(Intent.EXTRA_TEXT, shareContent);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, null));
        } else if (v == summaryButton) {
            String date = apply.get(applyStr[0]).getText().toString(); // 지원 마감일
            String dueDate = date.replaceAll("[^0-9]", ""); // 지원 마감일 yymmdd 형태로 바꿈
            String msg; // 음성출력 메세지

            try { // 지원 마감일 yyyy년 mm월 dd일 형태로 바꿈
                SimpleDateFormat sdf = new SimpleDateFormat("yymmdd");
                SimpleDateFormat sdfVoice = new SimpleDateFormat("yyyy년 mm월 dd일");

                Date formatDate = sdf.parse(dueDate);
                dueDate = sdfVoice.format(formatDate);
            } catch (Exception e) {
            }

            if (workCd.get("workDay").getText().toString().equals("")) { // 근무형태 정보가 존재하지 않는 경우
                msg = corp.get(corpStr[0]).getText().toString() + ", 에서," +
                        recruitCd.get("jobsNm").getText().toString() + "룰 모집합니다." +
                        "근무지는 " + address.get("addressDetail").getText().toString() + " 입니다." +
                        "임금은 " + workCd.get(workStr[0]).getText().toString() + " " + workCd.get(workStr[1]).getText().toString() + " 입니다." +
                        "지원 마감일은 " + dueDate + ", 입니다.";
            } else {
                msg = corp.get(corpStr[0]).getText().toString() + ", 에서," +
                        recruitCd.get("jobsNm").getText().toString() + "룰 모집합니다." +
                        "근무지는 " + address.get("addressDetail").getText().toString() + " 입니다." +
                        "임금은 " + workCd.get(workStr[0]).getText().toString() + " " + workCd.get(workStr[1]).getText().toString() + " 입니다." +
                        "근무형태는 " + workCd.get("workDay").getText().toString() + " 입니다." +
                        "지원 마감일은 " + dueDate + ", 입니다.";
            }

            voiceOut(msg);
        } else if (v == helpButton) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra("from", "DetailActivity");
            startActivity(intent);
        }
        if (voiceTf.isChecked()) {
            if (v == companyNameDetail) {
                String msg = companyNameDetail.getText().toString();
                voiceOut(msg);
            }
            if (v == titleDetail) {
                String msg = titleDetail.getText().toString();
                voiceOut(msg);
            }
            if (v == workFormDetail) {
                String msg = workFormDetail.getText().toString();
                voiceOut(msg);
            }
            if (v == schoolDetail) {
                String msg = schoolDetail.getText().toString();
                voiceOut(msg);
            }
            if (v == careerDetail) {
                String msg = careerDetail.getText().toString();
                voiceOut(msg);
            }
            if (v == address.get("addressDetail")) {
                String msg = address.get("addressDetail").getText().toString();
                voiceOut(msg);
            }
            for (int i = 0; i < recruitStr.length; i++) {
                if (v == recruitCd.get(recruitStr[i])) {
                    String msg = recruitCd.get(recruitStr[i]).getText().toString();
                    voiceOut(msg);
                }
            }
            for (int i = 0; i < workStr.length; i++) {
                if (v == workCd.get(workStr[i])) {
                    String msg = workCd.get(workStr[i]).getText().toString();
                    voiceOut(msg);
                }
            }
            for (int i = 0; i < applyStr.length; i++) {
                if (v == apply.get(applyStr[i])) {
                    String msg = apply.get(applyStr[i]).getText().toString();
                    voiceOut(msg);
                }
            }
            for (int i = 0; i < preferStr.length; i++) {
                if (v == prefer.get(preferStr[i])) {
                    String msg = prefer.get(preferStr[i]).getText().toString();
                    voiceOut(msg);
                }
            }
            for (int i = 0; i < corpStr.length; i++) {
                if (v == corp.get(corpStr[i])) {
                    String msg = corp.get(corpStr[i]).getText().toString();
                    voiceOut(msg);
                }
            }
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem,
                                                 MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
    }

    // 음성인식 어플이 종료되지 않아 계속 실행되는 경우를 막기위해 어플 종료 함수
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    // 음성 메세지 출력용
    private void voiceOut(String msg) {
        if (msg.length() < 1) return;

        // 음성 출력
        tts.setPitch(0.8f); //목소리 톤1.0
        tts.setSpeechRate(0.9f);    //목소리 속도
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void loadData(String id) {
        String detailPhp = getResources().getString(R.string.serverIP) + "detail.php?recruit_id=" + id;
        String certificatePhp = getResources().getString(R.string.serverIP) + "certificate.php?recruit_id=" + id;
        String recruitFilesPhp = getResources().getString(R.string.serverIP) + "recruit_files.php?recruit_id=" + id;
        String recruitAddrPhp = getResources().getString(R.string.serverIP) + "recruit_address.php?recruit_id=" + id;
        URLConnector urlConnectorDetail = new URLConnector(detailPhp);
        URLConnector urlConnectorCertificate = new URLConnector(certificatePhp);
        URLConnector urlConnectorRecruitFiles = new URLConnector(recruitFilesPhp);
        URLConnector urlConnectorRecruitAddr = new URLConnector(recruitAddrPhp);

        urlConnectorDetail.start();
        try {
            urlConnectorDetail.join();
        } catch (InterruptedException e) {
        }
        String detailResult = urlConnectorDetail.getResult();

        try {
            JSONObject jsonObject = new JSONObject(detailResult);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            JSONObject jsonObject1 = jsonArray.getJSONObject(0);

            String careerMin = "";
            String careerRequired = "";

            String organization = jsonObject1.getString("organization");
            String title = jsonObject1.getString("title");
            String enrollment_name = jsonObject1.getString("enrollment_name");
            String education_scope = jsonObject1.getString("education_scope");
            String required = jsonObject1.getString("required");
            String career_min = jsonObject1.getString("career_min");
            String basic_address = jsonObject1.getString("basic_address");
            String detail_address = jsonObject1.getString("detail_address");
            String category_name = jsonObject1.getString("category_name");
            String content = jsonObject1.getString("content");
            String num_of_people = jsonObject1.getString("num_of_people");
            String etc_info = jsonObject1.getString("etc_info");
            String salary_type_name = jsonObject1.getString("salary_type_name");
            String salary_ = jsonObject1.getString("salary");
            String work_time = jsonObject1.getString("work_time");
            String work_day = jsonObject1.getString("work_day");
            String retire_pay = jsonObject1.getString("retire_pay");
            String four_insurence = jsonObject1.getString("four_insurence");
            String etc_welfare = jsonObject1.getString("etc_welfare");
            String close_date = jsonObject1.getString("close_date");
            String screening_process = jsonObject1.getString("screening_process");
            String register_method = jsonObject1.getString("register_method");
            String submission_doc = jsonObject1.getString("submission_doc");
            String preference_cond = jsonObject1.getString("preference_cond");
            String etc_preference_cond = jsonObject1.getString("etc_preference_cond");
            String computer_able = jsonObject1.getString("computer_able");
            String representative = jsonObject1.getString("representative");
            String industry = jsonObject1.getString("industry");
            String corp_address = jsonObject1.getString("corp_address");
            String total_worker = jsonObject1.getString("total_worker");
            String sales_amount = jsonObject1.getString("sales_amount");
            url = jsonObject1.getString("url");
            contact = jsonObject1.getString("contact");

            // 경력조건
            switch (required) {
                case "무관":
                    careerRequired = "경력 무관";
                    careerMin = "경력 무관";
                    break;
                case "우대":
                    careerRequired = "경력 우대";
                    careerMin = "경력 " + career_min + "개월 우대";
                    break;
                case "필수":
                    careerRequired = "경력 필수";
                    careerMin = "경력 " + career_min + "개월 필수";
                    break;
            }
            // 우대조건
            preference_cond = preference_cond.replace("(준)고령자(50세이상), ", ""); // 두 개 이상
            preference_cond = preference_cond.replace("(준)고령자(50세이상)", ""); // 한 개

            companyNameDetail.setText(organization);
            titleDetail.setText(title);
            workFormDetail.setText(enrollment_name);
            schoolDetail.setText(education_scope);
            careerDetail.setText(careerRequired);
            address.get("addressDetail").setText(basic_address + " " + detail_address);
            recruitCd.get("jobsNm").setText(category_name);
            recruitCd.get("jobCont").setText(content);
            recruitCd.get("enterTpNm").setText(careerMin);
            recruitCd.get("eduNm").setText(education_scope);
            recruitCd.get("empTpNm").setText(enrollment_name);
            recruitCd.get("collectPsncnt").setText(num_of_people + "명 모집");
            recruitCd.get("etcHopeCont").setText(etc_info);
            workCd.get("salaryTypeCode").setText(salary_type_name);
            workCd.get("salary").setText(salary_);
            workCd.get("workTime").setText(work_time);
            workCd.get("workDay").setText(work_day);
            workCd.get("retirepay").setText(retire_pay);
            workCd.get("fourIns").setText(four_insurence);
            workCd.get("etcWelfare").setText(etc_welfare);
            apply.get("receiptCloseDt").setText(close_date);
            apply.get("selMthd").setText(screening_process);
            apply.get("rcptMthd").setText(register_method);
            apply.get("submitDoc").setText(submission_doc);
            prefer.get("pfCond").setText(preference_cond);
            prefer.get("etcPfCond").setText(etc_preference_cond);
            prefer.get("compAbl").setText(computer_able);
            corp.get("corpNm").setText(organization);
            corp.get("reperNm").setText(representative);
            corp.get("indTpCdNm").setText(industry);
            corp.get("corpAddr").setText(corp_address);
            corp.get("totPsncnt").setText(total_worker);
            corp.get("yrSalesAmt").setText(sales_amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 자격증 데이터 불러오기
        urlConnectorCertificate.start();
        try {
            urlConnectorCertificate.join();
        } catch (InterruptedException e) {
        }

        String certificateResult = urlConnectorCertificate.getResult();

        try {
            JSONObject jsonObject = new JSONObject(certificateResult);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            String certificate_name = "";

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                if (i == 0) {
                    certificate_name = jsonObject1.getString("certificate_name");
                } else {
                    certificate_name = certificate_name + ", " + jsonObject1.getString("certificate_name");
                }
            }

            prefer.get("certificate").setText(certificate_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 제출서류양식 데이터 불러오기
        urlConnectorRecruitFiles.start();
        try {
            urlConnectorRecruitFiles.join();
        } catch (InterruptedException e) {
        }

        String recruitFilesResult = urlConnectorRecruitFiles.getResult();

        try {
            JSONObject jsonObject = new JSONObject(recruitFilesResult);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                TextView item = attachFileUrl.get(i);
                item.setVisibility(View.VISIBLE);
                Linkify.TransformFilter tf = new Linkify.TransformFilter() {
                    @Override
                    public String transformUrl(Matcher matcher, String s) {
                        try {
                            return jsonObject1.getString("file_url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return "";
                    }
                };
                Pattern pattern = Pattern.compile(item.getText().toString());
                Linkify.addLinks(item, pattern, "", null, tf);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 좌표 데이터 불러오기
        urlConnectorRecruitAddr.start();
        try {
            urlConnectorRecruitAddr.join();
        } catch (InterruptedException e) {
        }

        String recruitAddrResult = urlConnectorRecruitAddr.getResult();

        try {
            JSONObject jsonObject = new JSONObject(recruitAddrResult);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            JSONObject jsonObject1 = jsonArray.getJSONObject(0);

            epX = jsonObject1.getString("x");
            epY = jsonObject1.getString("y");

            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(epY), Double.parseDouble(epX));

            mapView.setMapCenterPoint(mapPoint, true);

            MapPOIItem marker = new MapPOIItem();
            marker.setItemName("");
            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양
            marker.setShowCalloutBalloonOnTouch(false); // 말풍선 표시 x

            mapView.addPOIItem(marker);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        pref.editor.putInt(SharedPreference.TEXT_SIZE, progress);
        pref.editor.commit();
        textSize.setText(String.valueOf(progress));
        for (String key : address.keySet()) {
            address.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, progress);
        }
        for (String key : recruitCd.keySet()) {
            recruitCd.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, progress);
        }
        for (String key : workCd.keySet()) {
            workCd.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, progress);
        }
        for (String key : apply.keySet()) {
            apply.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, progress);
        }
        for (int i = 0; i < attachFileUrl.size(); i++) {
            attachFileUrl.get(i).setTextSize(TypedValue.COMPLEX_UNIT_DIP, progress);
        }
        for (String key : prefer.keySet()) {
            prefer.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, progress);
        }
        for (String key : corp.keySet()) {
            corp.get(key).setTextSize(TypedValue.COMPLEX_UNIT_DIP, progress);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
