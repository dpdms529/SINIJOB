package org.techtown.hanieum;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.CoverLetterDao;
import org.techtown.hanieum.db.entity.CoverLetter;

import java.util.HashMap;

public class CoverLetterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView title, selfIntro1, selfIntro2, selfIntro3;
    Button saveBtn;
    ImageButton delBtn;

    AppDatabase db;

    SelfInfo item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_letter);

        Intent intent = getIntent();
        item = (SelfInfo) intent.getSerializableExtra("edit");

        title = findViewById(R.id.title);
        selfIntro1 = findViewById(R.id.selfIntroCont1);
        selfIntro2 = findViewById(R.id.selfIntroCont2);
        selfIntro3 = findViewById(R.id.selfIntroCont3);
        saveBtn = findViewById(R.id.saveButton);
        delBtn = findViewById(R.id.delBtn);

        db = AppDatabase.getInstance(this);

        if (item != null) {
            title.setText("글 자기소개서 수정");
            selfIntro1.setText(item.getFirst_item());
            selfIntro2.setText(item.getSecond_item());
            selfIntro3.setText(item.getThird_item());
            delBtn.setVisibility(View.VISIBLE);
            delBtn.setOnClickListener(this);
        }

        saveBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SelfInfoActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == saveBtn) {
            if (item != null) {
                HashMap<Integer, String> hm = new HashMap<>();
                hm.put(1, selfIntro1.getText().toString());
                hm.put(2, selfIntro2.getText().toString());
                hm.put(3, selfIntro3.getText().toString());
                hm.put(4, Integer.toString(item.getNo()));
                new Query.CoverLetterUpdateAsyncTask(db.CoverLetterDao()).execute(hm);
                Toast.makeText(this, "자기소개서가 수정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                CoverLetter coverLetter = new CoverLetter("1", selfIntro1.getText().toString(), selfIntro2.getText().toString(), selfIntro3.getText().toString());
                new Query.CoverLetterInsertAsyncTask(db.CoverLetterDao()).execute(coverLetter);
                Toast.makeText(this, "자기소개서가 저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(this, SelfInfoActivity.class);
            startActivity(intent);
            finish();
        } else if (v == delBtn) {
            AlertDialog.Builder msgBuilder = new AlertDialog.Builder(this, R.style.MaterialAlertDialog_OK_color)
                    .setTitle("삭제").setMessage("삭제하시겠습니까?")
                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Query.CoverLetterDeleteAsyncTask(db.CoverLetterDao()).execute(item.getNo());
                            Toast.makeText(getApplicationContext(), "자기소개서가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), SelfInfoActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            AlertDialog msgDlg = msgBuilder.create();
            msgDlg.show();
        }
    }

//    public static class CoverLetterInsertAsyncTask extends AsyncTask<CoverLetter, Void, Void> {
//        private CoverLetterDao mCoverLetterDao;
//
//        public CoverLetterInsertAsyncTask(CoverLetterDao coverLetterDao) {
//            this.mCoverLetterDao = coverLetterDao;
//        }
//
//        @Override
//        protected Void doInBackground(CoverLetter... coverLetters) {
//            mCoverLetterDao.insertCoverLetter(coverLetters[0]);
//            return null;
//        }
//    }
//
//    public static class CoverLetterUpdateAsyncTask extends AsyncTask<HashMap<Integer, String>, Void, Void> {
//        private CoverLetterDao mCoverLetterDao;
//
//        public CoverLetterUpdateAsyncTask(CoverLetterDao coverLetterDao) {
//            this.mCoverLetterDao = coverLetterDao;
//        }
//
//        @Override
//        protected Void doInBackground(HashMap<Integer, String>... hashMaps) {
//            mCoverLetterDao.updateCoverLetter(hashMaps[0].get(1), hashMaps[0].get(2), hashMaps[0].get(3), Integer.parseInt(hashMaps[0].get(4)));
//            return null;
//        }
//    }
//
//    public static class CoverLetterDeleteAsyncTask extends AsyncTask<Integer, Void, Void> {
//        private CoverLetterDao mCoverLetterDao;
//
//        public CoverLetterDeleteAsyncTask(CoverLetterDao coverLetterDao) {
//            this.mCoverLetterDao = coverLetterDao;
//        }
//
//        @Override
//        protected Void doInBackground(Integer... integers) {
//            mCoverLetterDao.deleteCoverLetter(integers[0]);
//            return null;
//        }
//    }
}