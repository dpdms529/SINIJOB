package org.techtown.hanieum;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.CoverLetter;

import java.util.ArrayList;
import java.util.HashMap;

public class CoverLetterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView title, selfIntro1, selfIntro2, selfIntro3;
    Button saveBtn;
    ImageButton delBtn;
    ImageButton micBtn1, micBtn2, micBtn3;

    AppDatabase db;

    SelfInfo item;

    //음성 인식용
    Intent i;
    SpeechRecognizer speechRecognizer;

    int selectedMic = 0;

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
        micBtn1 = findViewById(R.id.micBtn1);
        micBtn2 = findViewById(R.id.micBtn2);
        micBtn3 = findViewById(R.id.micBtn3);

        db = AppDatabase.getInstance(this);

        if (item != null) {
            title.setText("글 자기소개서 수정");
            selfIntro1.setText(item.getFirst_item());
            selfIntro2.setText(item.getSecond_item());
            selfIntro3.setText(item.getThird_item());
            delBtn.setVisibility(View.VISIBLE);
            delBtn.setOnClickListener(this);
        }

        // 동그란 모양으로 변경
        micBtn1.setBackground(new ShapeDrawable(new OvalShape()));
        micBtn1.setClipToOutline(true);
        micBtn2.setBackground(new ShapeDrawable(new OvalShape()));
        micBtn2.setClipToOutline(true);
        micBtn3.setBackground(new ShapeDrawable(new OvalShape()));
        micBtn3.setClipToOutline(true);

        // 음성인식
        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");   //한국어 사용
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(listener);

        saveBtn.setOnClickListener(this);
        micBtn1.setOnClickListener(this);
        micBtn2.setOnClickListener(this);
        micBtn3.setOnClickListener(this);
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
        } else if (v == micBtn1) {
            permissionCheck();
            selectedMic = 1;
        } else if (v == micBtn2) {
            permissionCheck();
            selectedMic = 2;
        } else if (v == micBtn3) {
            permissionCheck();
            selectedMic = 3;
        }
    }

    // 음성인식을 위한 메소드
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Toast.makeText(getApplicationContext(), "지금부터 말을 해주세요", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int i) {
            Toast.makeText(getApplicationContext(), "천천히 다시 말해주세요", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            if (selectedMic == 1) {
                if (selfIntro1.getText().toString().equals("")) {
                    selfIntro1.setText(rs[0]);
                } else {
                    selfIntro1.setText(selfIntro1.getText() + " " + rs[0]);
                }
            } else if (selectedMic == 2) {
                if (selfIntro2.getText().toString().equals("")) {
                    selfIntro2.setText(rs[0]);
                } else {
                    selfIntro2.setText(selfIntro2.getText() + " " + rs[0]);
                }
            } else if (selectedMic == 3) {
                if (selfIntro3.getText().toString().equals("")) {
                    selfIntro3.setText(rs[0]);
                } else {
                    selfIntro3.setText(selfIntro3.getText() + " " + rs[0]);
                }
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };

    // 음성인식 어플이 종료되지 않아 계속 실행되는 경우를 막기위해 어플 종료 함수
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer.cancel();
            speechRecognizer = null;
        }
    }

    private void permissionCheck() {
        Log.d("TAG", "CoverLetterActivity 음성인식 시작");
        // 권한을 허용하지 않는 경우
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CoverLetterActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else { // 권한을 허용한 경우
            try {
                speechRecognizer.startListening(i);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

}