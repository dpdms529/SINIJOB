package org.techtown.hanieum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    private final int NETWORK_STATE_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, NETWORK_STATE_CODE);
            } else {

            }
        } else {
            // 권한 설정 허용한 경우
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().add(R.id.mainFrame,
                new HomeFragment()).commit(); // 처음 화면 설정
        bottomNavigationView.setSelectedItemId(R.id.recommendItem); // 처음 아이템 설정

        bottomNavigationView.setOnNavigationItemSelectedListener( // 아이템 선택시 실행
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.listItem :
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,
                                new RecommendFragment()).commit();
                        break;
                    case R.id.resumeItem :
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,
                                new ResumeFragment()).commit();
                        break;
                    case R.id.recommendItem :
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,
                                new HomeFragment()).commit();
                        break;
                    case R.id.starItem :
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,
                                new BookmarkFragment()).commit();
                        break;
                    case R.id.myItem :
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,
                                new InformationFragment()).commit();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case NETWORK_STATE_CODE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // startUsingSpeechSDK();
                } else {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}