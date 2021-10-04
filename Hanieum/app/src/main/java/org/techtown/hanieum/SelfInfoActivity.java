package org.techtown.hanieum;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.CoverLetter;

import java.util.ArrayList;
import java.util.List;

public class SelfInfoActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    SelfInfoAdapter adapter;

    Button addButton;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_info);

        db = AppDatabase.getInstance(this);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 1)); // 구분선
        adapter = new SelfInfoAdapter();
        recyclerView.setAdapter(adapter);

        addButton = findViewById(R.id.addButton);

        db.CoverLetterDao().getAll().observe((LifecycleOwner) this, new Observer<List<CoverLetter>>() {
            @Override
            public void onChanged(List<CoverLetter> coverLetters) {
                adapter.clearItems();
                for (CoverLetter c : coverLetters) {
                    adapter.addItem(new SelfInfo(c));
                }
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setOnItemClickListener(new OnSelfInfoItemClickListener() {
            @Override
            public void OnItemClick(SelfInfoAdapter.ViewHolder holder, View view, int position) {
                SelfInfo item = adapter.getItem(position);
                Intent intent;
                if(item.getCode().equals("0")) { // 영상 자기소개서
                    intent = new Intent(getApplicationContext(), VideoListActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), CoverLetterActivity.class);
                }
                intent.putExtra("edit", item);
                startActivity(intent);
            }
        });
        addButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == addButton) {
            if (adapter.getItemCount() == 10) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알림")
                        .setMessage("자기소개서는 최대 10개까지만 등록 가능합니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                List<CharSequence> items = new ArrayList<>();
                items.add("영상");
                items.add("일반(글)");
                CharSequence[] charSequences = items.toArray(new CharSequence[items.size()]);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("자기소개서 유형을 선택하세요");
                alertDialog.setItems(charSequences, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (charSequences[which] == "영상") {
                            Intent intent = new Intent(getApplicationContext(), VideoListActivity.class);
                            startActivity(intent);
                        } else if (charSequences[which] == "일반(글)") {
                            Intent intent = new Intent(getApplicationContext(), CoverLetterActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                alertDialog.show();
            }
        }
    }
}