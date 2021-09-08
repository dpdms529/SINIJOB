package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.CoverLetterDao;
import org.techtown.hanieum.db.entity.CoverLetter;

public class CoverLetterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView selfIntro1, selfIntro2, selfIntro3;
    Button saveButton;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_letter);

        selfIntro1 = findViewById(R.id.selfIntroCont1);
        selfIntro2 = findViewById(R.id.selfIntroCont2);
        selfIntro3 = findViewById(R.id.selfIntroCont3);
        saveButton = findViewById(R.id.saveButton);

        db = AppDatabase.getInstance(this);

        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == saveButton){
            CoverLetter coverLetter = new CoverLetter("1",selfIntro1.getText().toString(), selfIntro2.getText().toString(), selfIntro3.getText().toString());
            new CoverLetterInsertAsyncTask(db.CoverLetterDao()).execute(coverLetter);
            Toast.makeText(this, "자기소개서가 저장되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public static class CoverLetterInsertAsyncTask extends AsyncTask<CoverLetter, Void, Void> {
        private CoverLetterDao mCoverLetterDao;

        public CoverLetterInsertAsyncTask(CoverLetterDao coverLetterDao){
            this.mCoverLetterDao = coverLetterDao;
        }
        @Override
        protected Void doInBackground(CoverLetter... coverLetters) {
            mCoverLetterDao.insertCoverLetter(coverLetters[0]);
            return null;
        }
    }
}