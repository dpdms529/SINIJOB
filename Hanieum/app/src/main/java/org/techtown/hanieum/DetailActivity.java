package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener,
        MapView.POIItemEventListener, MapView.MapViewEventListener {
    Toolbar toolbar;
    MapView mapView;
    Button applyButton;
    Button findWay;
    Button goWorknetBtn;
    ImageView goWorknetImg;
    TextView companyNameDetail;
    TextView titleDetail;
    TextView workFormDetail;
    TextView schoolDetail;
    TextView careerDetail;
    TextView addressDetail;
    TextView jobsNm, jobCont, enterTpNm, eduNm, empTpNm, collectPsncnt, etcHopeCont; // 모집조건
    TextView salaryTypeCode, salary, workTime, workDay, retirepay, fourIns, etcWelfare; // 근무조건
    TextView receiptCloseDt, selMthd, rcptMthd, submitDoc; //접수방법
    ArrayList<TextView> attachFileUrl = new ArrayList<>(); //접수방법_제출서류양식
    TextView pfCond, etcPfCond, certificate, compAbl; // 우대사항
    TextView corpNm, reperNm, indTpCdNm, corpAddr, totPsncnt, yrSalesAmt; // 기업정보
    ScrollView scrollView;
    Button shareButton;

    String epX;
    String epY;
    String url;
    String contact;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        context = this;

        toolbar = findViewById(R.id.toolbar0201);
        applyButton = findViewById(R.id.applyButton);
        findWay = findViewById(R.id.findWay);
        goWorknetBtn = findViewById(R.id.goWorknetBtn);
        goWorknetImg = findViewById(R.id.goWorknetImg);
        companyNameDetail = findViewById(R.id.companyNameDetail);
        titleDetail = findViewById(R.id.titleDetail);
        workFormDetail = findViewById(R.id.workFormDetail);
        schoolDetail = findViewById(R.id.schoolDetail);
        careerDetail = findViewById(R.id.careerDetail);
        addressDetail = findViewById(R.id.addressDetail);
        jobsNm = findViewById(R.id.jobsNm);
        jobCont = findViewById(R.id.jobCont);
        enterTpNm = findViewById(R.id.enterTpNm);
        eduNm = findViewById(R.id.eduNm);
        empTpNm = findViewById(R.id.empTpNm);
        collectPsncnt = findViewById(R.id.collectPsncnt);
        etcHopeCont = findViewById(R.id.etcHopeCont);
        salaryTypeCode = findViewById(R.id.salaryTypeCode);
        salary = findViewById(R.id.salary);
        workTime = findViewById(R.id.workTime);
        workDay = findViewById(R.id.workDay);
        retirepay = findViewById(R.id.retirepay);
        fourIns = findViewById(R.id.fourIns);
        etcWelfare = findViewById(R.id.etcWelfare);
        receiptCloseDt = findViewById(R.id.receiptCloseDt);
        selMthd = findViewById(R.id.selMthd);
        rcptMthd = findViewById(R.id.rcptMthd);
        submitDoc = findViewById(R.id.submitDoc);

        for(int i = 1;i<=5;i++){    //동적으로 ID부여
            String attachFileUrlId = "attachFileUrl" + i;
            int resId = getResources().getIdentifier(attachFileUrlId,"id",getApplicationContext().getPackageName());
            attachFileUrl.add(findViewById(resId));
        }

        pfCond = findViewById(R.id.pfCond);
        etcPfCond = findViewById(R.id.etcPfCond);
        certificate = findViewById(R.id.certificate);
        compAbl = findViewById(R.id.compAbl);
        corpNm = findViewById(R.id.corpNm);
        reperNm = findViewById(R.id.reperNm);
        indTpCdNm = findViewById(R.id.indTpCdNm);
        corpAddr = findViewById(R.id.corpAddr);
        totPsncnt = findViewById(R.id.totPsncnt);
        yrSalesAmt = findViewById(R.id.yrSalesAmt);
        scrollView = findViewById(R.id.detailScrollView);
        shareButton = findViewById(R.id.shareButton);

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map);
        mapViewContainer.addView(mapView);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        loadData(id);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);

        applyButton.setOnClickListener(this);
        findWay.setOnClickListener(this);
        goWorknetBtn.setOnClickListener(this);
        goWorknetImg.setOnClickListener(this);

        shareButton.setOnClickListener(this);
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
            alertDialog.setMessage(rcptMthd.getText());
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
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+contact));
                                startActivity(intent);
                            } else if (charSequences[which] == "문자") {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"));
                                intent.setType("vnd.android-dir/mms-sms");
                                intent.putExtra("address", "");
                                intent.putExtra("sms_body", companyNameDetail.getText()+"에 지원합니다.");
                                startActivity(intent);
                            } else if (charSequences[which] == "이메일") {
                                String uriText = "mailto:" + "?subject=" +
                                        Uri.encode(companyNameDetail.getText()+"에 지원합니다.") + "&body=" + Uri.encode("");
                                Uri uri = Uri.parse(uriText);

                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(uri);
                                startActivity(Intent.createChooser(intent, "이메일 앱을 선택하세요"));
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
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://route?sp=37.537229,127.005515&ep="+epY+","+epX+"&by=CAR"));
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
        } else if(v == shareButton){
            Intent intent = new Intent(Intent.ACTION_SEND);
            String shareContent = "제목 : " + titleDetail.getText().toString() + "\n기업명 : " + corpNm.getText().toString() + "\n접수마감일 : " + receiptCloseDt.getText().toString() + "\n" + url;
            intent.putExtra(Intent.EXTRA_TEXT,shareContent);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent,null));

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
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    public void loadData(String id) {
        String detailPhp = getResources().getString(R.string.serverIP)+"detail.php?recruit_id=" + id;
        String certificatePhp = getResources().getString(R.string.serverIP)+"certificate.php?recruit_id=" + id;
        String recruitFilesPhp = getResources().getString(R.string.serverIP)+"recruit_files.php?recruit_id=" + id;
        String recruitAddrPhp = getResources().getString(R.string.serverIP)+"recruit_address.php?recruit_id=" + id;
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
            String job_name = jsonObject1.getString("job_name");
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
            addressDetail.setText(basic_address + " " + detail_address);
            jobsNm.setText(job_name);
            jobCont.setText(content);
            enterTpNm.setText(careerMin);
            eduNm.setText(education_scope);
            empTpNm.setText(enrollment_name);
            collectPsncnt.setText(num_of_people);
            etcHopeCont.setText(etc_info);
            salaryTypeCode.setText(salary_type_name);
            salary.setText(salary_);
            workTime.setText(work_time);
            workDay.setText(work_day);
            retirepay.setText(retire_pay);
            fourIns.setText(four_insurence);
            etcWelfare.setText(etc_welfare);
            receiptCloseDt.setText(close_date);
            selMthd.setText(screening_process);
            rcptMthd.setText(register_method);
            submitDoc.setText(submission_doc);
            pfCond.setText(preference_cond);
            etcPfCond.setText(etc_preference_cond);
            compAbl.setText(computer_able);
            corpNm.setText(organization);
            reperNm.setText(representative);
            indTpCdNm.setText(industry);
            corpAddr.setText(corp_address);
            totPsncnt.setText(total_worker);
            yrSalesAmt.setText(sales_amount);
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

            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                if (i == 0) {
                    certificate_name = jsonObject1.getString("certificate_name");
                } else {
                    certificate_name = certificate_name + ", " + jsonObject1.getString("certificate_name");
                }
            }

            certificate.setText(certificate_name);
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

           for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                TextView item = attachFileUrl.get(i);
                item.setVisibility(View.VISIBLE);
                Linkify.TransformFilter tf = new Linkify.TransformFilter(){
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
                Linkify.addLinks(item,pattern,"",null,tf);
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
}
