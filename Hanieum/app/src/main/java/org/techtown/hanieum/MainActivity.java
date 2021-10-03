package org.techtown.hanieum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().add(R.id.mainFrame,
                new RecommendFragment()).commit(); // 처음 화면 설정
        bottomNavigationView.setSelectedItemId(R.id.listItem); // 처음 아이템 설정

        bottomNavigationView.setOnNavigationItemSelectedListener( // 아이템 선택시 실행
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.listItem:
                                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,
                                        new RecommendFragment()).commit();
                                break;
                            case R.id.resumeItem:
                                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,
                                        new ResumeFragment()).commit();
                                break;
                            case R.id.recommendItem:
                                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,
                                        new HomeFragment()).commit();
                                break;
                            case R.id.starItem:
                                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,
                                        new BookmarkFragment()).commit();
                                break;
                            case R.id.myItem:
                                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,
                                        new InformationFragment()).commit();
                                break;
                        }
                        return true;
                    }
                });
    }
}