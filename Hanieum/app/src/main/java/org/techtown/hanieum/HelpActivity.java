package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HelpActivity extends AppCompatActivity {

    Button prevButton;
    Button nextButton;
    TabLayout tabLayout;

    public static String from;
    private int numPages;
    //The pager widget, which handles animation and allows swiping horizontally to access previous and next wizard steps.
    ViewPager2 viewPager;
    // The pager adapter, which provides the pages to the view pager widget.
    FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        viewPager = findViewById(R.id.myPager);
        pagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        tabLayout = findViewById(R.id.tab_layout);

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        if (from.equals("RecommendFragment")) {
            numPages = 5;
        } else {    // FilteringActivity
            numPages = 5;
        }

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
        }).attach();
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = (viewPager.getCurrentItem() - 1);
                if (current < 0) {
                    finish();
                } else if (current < numPages) {
                    // 이전 화면으로 이동
                    viewPager.setCurrentItem(current);
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = (viewPager.getCurrentItem() + 1);
                if (current < numPages) {
                    // 다음 화면으로 이동
                    viewPager.setCurrentItem(current);
                } else {
                    finish();
                }
            }
        });
    }

    /*
    뒤로가기 버튼 눌렀을 때
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.d
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
    */

    public class MyPagerAdapter extends FragmentStateAdapter {

        public MyPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int pos) {
            switch (pos) {
                case 0:
                    return new HelpFragment(1);
                case 1:
                    return new HelpFragment(2);
                case 2:
                    return new HelpFragment(3);
                case 3:
                    return new HelpFragment(4);
                case 4:
                    return new HelpFragment(5);
                default:
                    return new HelpFragment(1);
            }
        }

        @Override
        public int getItemCount() {
            return numPages;
        }
    }
}