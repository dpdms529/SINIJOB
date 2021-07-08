package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class RegionActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    Button regionSearchButton;
    ChipGroup chipGroup;
    Chip chip6, chip7, chip8, chip9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);

        toolbar = findViewById(R.id.toolbar3);
        regionSearchButton = findViewById(R.id.regionSearchButton);
        chipGroup = findViewById(R.id.regionChipGroup);




        chip6 = findViewById(R.id.chip6);
        chip7 = findViewById(R.id.chip7);
        chip8 = findViewById(R.id.chip8);
        chip9 = findViewById(R.id.chip9);
        chip6.setCloseIconResource(R.drawable.close);
        chip6.setCloseIconVisible(true);
        chip6.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip6);
            }
        });
        chip7.setCloseIconResource(R.drawable.close);
        chip7.setCloseIconVisible(true);
        chip7.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip7);
            }
        });
        chip8.setCloseIconResource(R.drawable.close);
        chip8.setCloseIconVisible(true);
        chip8.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip8);
            }
        });
        chip9.setCloseIconResource(R.drawable.close);
        chip9.setCloseIconVisible(true);
        chip9.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip9);
            }
        });




        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        regionSearchButton.setOnClickListener(this);
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
        if (v == regionSearchButton) {
            Intent intent = new Intent(this, RegionSearchActivity.class);
            startActivity(intent);
        }
    }
}