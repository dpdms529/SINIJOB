package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {

    TextView title;
    EditText addressText;
    Button yesBtn;
    Button noBtn;
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        title = findViewById(R.id.title);
        addressText = findViewById(R.id.addressText);
        yesBtn = findViewById(R.id.yesBtn);
        noBtn = findViewById(R.id.noBtn);
        nextBtn = findViewById(R.id.nextBtn);



        double latitude  = 35.839921367;
        double longitude = 127.132943834;

        Geocoder geocoder = new Geocoder(this);
        List<Address> gList = null;
        try {
            gList = geocoder.getFromLocation(latitude,longitude,5);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("address", "setMaskLocation() - 서버에서 주소변환시 에러발생");
        }
        if (gList != null) {
            if (gList.size() == 0) {
                Toast.makeText(this, " 현재위치에서 검색된 주소정보가 없습니다. ", Toast.LENGTH_SHORT).show();
            } else {
                Address address = gList.get(0);
                String sido = address.getAdminArea();       // 대구광역시
                String gugun = address.getSubLocality();    // 수성구

                addressText.setText(sido + " " + gugun);
                Log.d("dddddddddddddddddddd", sido + " " + gugun);
//                Log.d("getFeatureName", address.getFeatureName());    // 248-5
                Log.d("getLocality", address.getLocality());    // 전주시, 대구광역시는 에러남
//                Log.d("getPremises", address.getPremises());  // 248-5
//                Log.d("getSubAdminArea", address.getSubAdminArea());  // 안됨
                Log.d("getThoroughfare", address.getThoroughfare());    // 금암1동
                Log.d("getSubThoroughfare", address.getSubThoroughfare());  // 248-5
//                Log.d("getPhone", address.getPhone());
            }
        }



        yesBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == yesBtn) {

        } else if (v == noBtn) {
            title.setText("올바른 주소를 입력해주세요");
            addressText.setEnabled(true);
//            addressText.setText("");
            yesBtn.setVisibility(View.GONE);
            noBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        } else if (v == nextBtn) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}