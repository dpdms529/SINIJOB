package org.techtown.hanieum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.dao.CoverLetterDao;
import org.techtown.hanieum.db.entity.CoverLetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ApplyActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;

    //음성 인식용
    Intent intent;
    SpeechRecognizer speechRecognizer;
    //음성 출력용
    TextToSpeech tts;

    ImageButton helpButton; // 도움말 버튼
    ImageButton micButton;
    Switch voiceTf;
    EditText textMsg;
    TextView textSys;
    Button finishButton;

    Spinner spinner;
    ArrayList<String> items = new ArrayList<>();
    TextView coverLetter;
    CoverLetter selectedCL;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        context = this;
        helpButton = findViewById(R.id.helpButton);
        micButton = findViewById(R.id.micButton);
        voiceTf = findViewById(R.id.voiceTf);
        textMsg = findViewById(R.id.textMsg);
        textSys = findViewById(R.id.textSys);
        finishButton = findViewById(R.id.finishButton);
        spinner = findViewById(R.id.spinner);
        coverLetter = findViewById(R.id.coverLetter);

        db = AppDatabase.getInstance(this);
        List<CoverLetter> coverLetters = db.CoverLetterDao().getAll();
        items.add("선택");
        for(CoverLetter c : coverLetters){
            items.add("자기소개서 " + c.cover_letter_no);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    coverLetter.setVisibility(View.GONE);
                }else{
                    int no = Integer.parseInt(items.get(i).substring(6));
                    try {
                        selectedCL = new CoverLetterGetSelectedAsyncTask(db.CoverLetterDao()).execute(no).get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(selectedCL.cover_dist_code.equals("1")){
                        coverLetter.setText(selectedCL.first_item + "\n" + selectedCL.second_item + "\n" + selectedCL.third_item);
                        coverLetter.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 동그란 모양으로 변경
        micButton.setBackground(new ShapeDrawable(new OvalShape()));
        micButton.setClipToOutline(true);

        // 음성인식
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");   //한국어 사용
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(listener);

        // 음성출력 생성, 리스너 초기화
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        helpButton.setOnClickListener(this);
        micButton.setOnClickListener(this);
        textMsg.setOnClickListener(this);
        finishButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == micButton) {
            System.out.println("음성인식 시작!");
            // 권한을 허용하지 않는 경우
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ApplyActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            } else { // 권한을 허용한 경우
                try {
                    speechRecognizer.startListening(intent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == textMsg) {
            if (voiceTf.isChecked()) {
                String msg = textMsg.getText().toString();
                voiceOut(msg);
            }
        } else if (v == finishButton) {
            Intent i = getIntent();
            if (i.getStringExtra("type").equals("sms")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"));
                intent.setType("vnd.android-dir/mms-sms");
                intent.putExtra("address", "");
                intent.putExtra("sms_body", textMsg.getText().toString());
                startActivity(intent);
            } else {    // email
                String uriText = "mailto:8bangwomen@hanium.com" + "?subject=" +
                        Uri.encode(i.getStringExtra("company") + "에 지원합니다.") + "&body=" + Uri.encode(textMsg.getText().toString() + "\n자기소개 : " + selectedCL.first_item + "\n지원동기 : " + selectedCL.second_item + "\n경력 및 경험 : " + selectedCL.third_item);
                Uri uri = Uri.parse(uriText);

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(uri);
                startActivity(Intent.createChooser(intent, "이메일 앱을 선택하세요"));
            }
        } else if (v == helpButton) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra("from", "ApplyActivity");
            startActivity(intent);
        }
    }

    // 음성인식을 위한 메소드
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            textSys.setText("지금부터 말을 해주세요");
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
            textSys.setText("천천히 다시 말해주세요");
        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            textMsg.setText(textMsg.getText() + " " + rs[0]);
            textSys.setText("");
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
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer.cancel();
            speechRecognizer = null;
        }
    }

    // 음성 메세지 출력용
    private void voiceOut(String msg) {
        if (msg.length() < 1) return;

        // 음성 출력
        tts.setPitch(0.8f); //목소리 톤1.0
        tts.setSpeechRate(0.9f);    //목소리 속도
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public static class CoverLetterGetSelectedAsyncTask extends AsyncTask<Integer, Void, CoverLetter> {
        private CoverLetterDao mCoverLetterDao;

        public CoverLetterGetSelectedAsyncTask(CoverLetterDao coverLetterDao){
            this.mCoverLetterDao = coverLetterDao;
        }

        @Override
        protected CoverLetter doInBackground(Integer... integers) {
            return mCoverLetterDao.getSelected(integers[0]);
        }
    }
}