package org.techtown.hanieum;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.CoverLetter;
import org.techtown.hanieum.db.entity.CvInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
    VideoView videoView;
    LinearLayout videoLayout;

    Spinner spinner;
    ArrayList<String> spinnerArray = new ArrayList<>();
    ArrayList<HashMap<String, String>> items = new ArrayList<>();
    LinearLayout coverLetterLayout;
    TextView coverLetter1, coverLetter2, coverLetter3;
    CoverLetter selectedCL;

    String name, gender, age, address, phone, email, school, userInfo;
    ArrayList<String> career, certificate;
    StringBuilder recruit;

    AppDatabase db;
    SharedPreference pref;

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
        coverLetter1 = findViewById(R.id.coverLetter1);
        coverLetter2 = findViewById(R.id.coverLetter2);
        coverLetter3 = findViewById(R.id.coverLetter3);
        coverLetterLayout = findViewById(R.id.coverLetterLayout);
        videoView = findViewById(R.id.video_view);
        videoLayout = findViewById(R.id.coverVideoLayout);

        db = AppDatabase.getInstance(this);
        pref = new SharedPreference(this);

        name = pref.preferences.getString(SharedPreference.NAME, "");
        if (pref.preferences.getString(SharedPreference.GENDER, "").equals("F")) {
            gender = "여";
            age = pref.preferences.getString(SharedPreference.AGE, "") + "세";
        } else {
            gender = "남";
            age = pref.preferences.getString(SharedPreference.AGE, "") + "세";
        }
        address = pref.preferences.getString(SharedPreference.ADDRESS, "");
        phone = pref.preferences.getString(SharedPreference.PHONE, "");
        email = pref.preferences.getString(SharedPreference.EMAIL, "");

        // 학력사항
        String education = null;
        try {
            education = new Query.CvInfoGetInfoCodeAsyncTask(db.CvInfoDao()).execute(pref.preferences.getString(SharedPreference.USER_ID,""),"E").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (education != null) {
            if (education.equals("01")) {
                school = "초등학교 졸업 이하";
            } else if (education.equals("02")) {
                school = "중학교 졸업";
            } else if (education.equals("03")) {
                school = "고등학교 졸업";
            } else if (education.equals("04")) {
                school = "대학(2,3년제) 졸업";
            } else if (education.equals("05")) {
                school = "대학(4년제) 졸업";
            } else if (education.equals("06")) {
                school = "석사";
            } else if (education.equals("07")) {
                school = "박사";
            }
        }

        // 경력사항
        List<CvInfo> cv = null;
        try {
            cv = new Query.CvInfoGetAsyncTask(db.CvInfoDao()).execute(pref.preferences.getString(SharedPreference.USER_ID,""),"CA").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cv != null) {
            career = new ArrayList<>();
            for (CvInfo cvInfo : cv) {
                if (cvInfo.job_position.equals("")) {
                    career.add((cvInfo.info_no + 1) + ". " + cvInfo.info + " / " + cvInfo.company_name + " / " + cvInfo.period + "개월");
                } else {
                    career.add((cvInfo.info_no + 1) + ". " + cvInfo.info + " / " + cvInfo.company_name + " / " + cvInfo.job_position + " / " + cvInfo.period + "개월");
                }
            }
        }

        // 보유자격증
        cv = null;
        try {
            cv = new Query.CvInfoGetAsyncTask(db.CvInfoDao()).execute(pref.preferences.getString(SharedPreference.USER_ID,""),"CE").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cv != null) {
            certificate = new ArrayList<>();
            for (CvInfo cvInfo : cv) {
                certificate.add((cvInfo.info_no + 1) + ". " + cvInfo.info);
            }
        }

        recruit = new StringBuilder("이름 : " + name + "\n" +
                "성별 : " + gender + "\n" +
                "나이 : " + age + "\n" +
                "주소 : " + address + "\n" +
                "전화번호 : " + phone + "\n" +
                "이메일 : " + email + "\n");
        if (school != null) {
            recruit.append("학력 : " + school + "\n");
        }
        if (career.size() == 0) {
            recruit.append("경력 : 없음\n");
        } else {
            recruit.append("경력 : \n");
            for (String s : career) {
                recruit.append(s + "\n");
            }
        }
        if (certificate.size() != 0) {
            recruit.append("보유 자격증 : \n");
            for (String s : certificate) {
                recruit.append(s + "\n");
            }
        }
        userInfo = recruit.toString();

        Log.d("TAG", "onCreate: " + recruit.toString());

        spinnerArray.add("선택");
        HashMap<String, String> item = new HashMap<>();
        item.put("dist", "null");
        db.CoverLetterDao().getAll().observe((LifecycleOwner) this, new Observer<List<CoverLetter>>() {
            @Override
            public void onChanged(List<CoverLetter> coverLetters) {
                items.clear();
                items.add(item);
                for (CoverLetter c : coverLetters) {
                    if(c.cover_dist_code.equals("0")){
                        spinnerArray.add("영상_" + c.cover_letter_no);
                    }else{
                        spinnerArray.add("일반_" + c.cover_letter_no);
                    }
                    HashMap<String, String> dbitem = new HashMap<>();
                    dbitem.put("dist", c.cover_dist_code);
                    if (c.cover_dist_code.equals("0")) {
                        dbitem.put("dirname", c.first_item);
                    }
                    items.add(dbitem);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    coverLetterLayout.setVisibility(View.GONE);
                    videoLayout.setVisibility(View.GONE);
                } else {
                    if (items.get(i).get("dist").equals("0")) {
                        String dirName = items.get(i).get("dirname");
                        String url = ApplyActivity.this.getFilesDir().toString() + "/videocv_" + dirName + "/cv.mp4";
                        Uri videoUri = Uri.parse(url);

                        coverLetterLayout.setVisibility(View.GONE);
                        videoLayout.setVisibility(View.VISIBLE);

                        //비디오뷰의 재생, 일시정지 등을 할 수 있는 '컨트롤바'를 붙여주는 작업
                        videoView.setMediaController(new MediaController(ApplyActivity.this));

                        //VideoView가 보여줄 동영상의 경로 주소(Uri) 설정하기
                        videoView.setVideoURI(videoUri);

                        getThumbNail(videoView, url);

                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                videoView.setBackground(null);
                            }
                        });
                    } else if (items.get(i).get("dist").equals("1")) {
                        String no = spinnerArray.get(i).substring(3);
                        try {
                            selectedCL = new Query.CoverLetterGetSelectedAsyncTask(db.CoverLetterDao()).execute(pref.preferences.getString(SharedPreference.USER_ID,""), no).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (selectedCL.cover_dist_code.equals("1")) {
                            coverLetter1.setText(selectedCL.first_item);
                            coverLetter2.setText(selectedCL.second_item);
                            coverLetter3.setText(selectedCL.third_item);
                            coverLetterLayout.setVisibility(View.VISIBLE);
                            videoLayout.setVisibility(View.GONE);
                        }
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
            String cvCode = items.get(spinner.getSelectedItemPosition()).get("dist");
            if (i.getStringExtra("type").equals("sms")) {
                Intent intent;
                if (cvCode.equals("0")) { // 영상 자기소개서 첨부
                    recruit.append("하고싶은 말 :\n" + textMsg.getText().toString());
                    intent = new Intent(Intent.ACTION_SEND, Uri.parse("tel:010-0000-0000"));
                    intent.setType("vnd.android-dir/mms-sms");
                    intent.putExtra("address", "010-0000-0000");
                    String dirName = items.get(spinner.getSelectedItemPosition()).get("dirname");
                    String url = ApplyActivity.this.getFilesDir().toString() + "/videocv_" + dirName + "/cv.mp4";
                    File f = new File(url);
                    intent.setType("video/*");
                    intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, "org.techtown.hanieum.fileprovider", f));
                    intent.putExtra("sms_body", recruit.toString());
                } else { // 글 자기소개서 첨부
                    recruit.append("자기소개 :\n" + selectedCL.first_item + "\n지원동기 :\n" + selectedCL.second_item + "\n경력 및 경험 :\n" + selectedCL.third_item + "\n하고싶은 말 :\n" + textMsg.getText().toString());
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:010-0000-0000"));
                    intent.setType("vnd.android-dir/mms-sms");
                    intent.putExtra("address", "010-0000-0000");
                    intent.putExtra("sms_body", recruit.toString());
                }
                startActivity(intent);
                recruit = new StringBuilder(userInfo);
            } else {    // email
                Intent intent;
                if(cvCode.equals("0")) { // 영상 자기소개서 첨부
                    if(!textMsg.getText().toString().equals("")) {
                        recruit.append("하고싶은 말 :\n" + textMsg.getText().toString());
                    }
                    intent = new Intent(Intent.ACTION_SEND);
                    String dirName = items.get(spinner.getSelectedItemPosition()).get("dirname");
                    String url = ApplyActivity.this.getFilesDir().toString() + "/videocv_" + dirName + "/cv.mp4";
                    File f = new File(url);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_SUBJECT, i.getStringExtra("company") + "에 지원합니다."); //메일 제목
                    intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"mailto:8bangwomen@hanium.com"}); // 메일 주소
                    intent.putExtra(Intent.EXTRA_TEXT, recruit.toString()); // 메일 내용
                    intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, "org.techtown.hanieum.fileprovider", f)); // 첨부파일
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 첨부 권한 설정
                } else {
                    recruit.append("자기소개 :\n" + selectedCL.first_item + "\n지원동기 :\n" + selectedCL.second_item + "\n경력 및 경험 :\n" + selectedCL.third_item + "\n하고싶은 말 :\n" + textMsg.getText().toString());
                    String uriText = "mailto:8bangwomen@hanium.com" + "?subject=" +
                            Uri.encode(i.getStringExtra("company") + "에 지원합니다.") + "&body=" + Uri.encode(recruit.toString());
                    Uri uri = Uri.parse(uriText);

                    intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(uri);
                }
                startActivity(Intent.createChooser(intent, "이메일 앱을 선택하세요"));
                recruit = new StringBuilder(userInfo);
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

    private void getThumbNail(VideoView videoView, String path) {
        videoView.seekTo(1);
        videoView.setAlpha(1);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
        videoView.setBackground(bitmapDrawable);
    }
}