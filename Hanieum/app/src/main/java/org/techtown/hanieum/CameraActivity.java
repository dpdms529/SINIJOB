package org.techtown.hanieum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final int REQUEST_CODE_PERMISSIONS = 1001; //arbitrary number, can be changed accordingly
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.RECORD_AUDIO"}; //array w/ permissions from manifest

    private int levelCount;
    private PreviewView mPreviewView;
    private ImageView mCaptureButton;
    private TextView guideline;
    private String recordType;
    private String dirName;
    private VideoView interviewer;

    private boolean mIsRecordingVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mPreviewView = findViewById(R.id.previewView);
        mCaptureButton = findViewById(R.id.camera_capture_button);
        guideline = findViewById(R.id.textView10);
        interviewer = findViewById(R.id.interviewer);
        levelCount = 1;

        Intent intent = getIntent();
        recordType = intent.getStringExtra("recordType");
        dirName = intent.getStringExtra("dirName");

        Log.e("dirName", intent.getStringExtra("dirName"));
        if (recordType.equals("full")) {
            guideline.setText(" 지금부터 영상 자기소개 촬영 방법을 안내해드리겠습니다 ");
            new Handler().postDelayed(() -> guideline.setText(" 제가 묻는 질문에 대한 답변을 생각해두시고, "), 6500);
            new Handler().postDelayed(() -> guideline.setText(" 준비가 끝나면, 오른쪽의 버튼을 누르고 말씀해주시면 됩니다 "), 10000);
            new Handler().postDelayed(() -> guideline.setText(" 대답이 다 끝나면, 오른쪽의 버튼을 다시 눌러 종료해주시면 됩니다 "), 16000);
            new Handler().postDelayed(() -> guideline.setText(" 그럼, 지금부터 영상 자기소개를 시작하겠습니다 "), 22500);
            interviewerVideoStart("android.resource://" + getPackageName() + "/raw/cv_intro");
        } else if (recordType.equals("introduce")) {
            guideline.setText(" 자기소개를 1분 동안 해주세요 ");
            interviewerVideoStart("android.resource://" + getPackageName() + "/raw/cv_first");
            levelCount = 4;
        } else if (recordType.equals("motive")) {
            guideline.setText(" 이번 공고에 지원하신 동기를 말씀해주세요 ");
            interviewerVideoStart("android.resource://" + getPackageName() + "/raw/cv_second");
            levelCount = 5;
        } else if (recordType.equals("career")) { // recordType == "career"
            guideline.setText(" 관련된 경험을 2가지만 말씀해주세요 ");
            interviewerVideoStart("android.resource://" + getPackageName() + "/raw/cv_third");
            levelCount = 6;
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void interviewerVideoStart(String interviewerURL) {
        Uri videoUri = Uri.parse(interviewerURL);
        //비디오뷰의 재생, 일시정지 등을 할 수 있는 '컨트롤바'를 붙여주는 작업
        interviewer.setMediaController(new MediaController(this));
        //VideoView가 보여줄 동영상의 경로 주소(Uri) 설정하기
        interviewer.setVideoURI(videoUri);
        boolean[] flag = {false};

        //동영상을 읽어오는데 시간이 걸리므로..
        //비디오 로딩 준비가 끝났을 때 실행하도록..
        //리스너 설정
        interviewer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //비디오 시작
                interviewer.start();
            }
        });

        interviewer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (levelCount == 1 && !flag[0]) {
                    // intro 영상 종료 후, 1번 항목도 재생
                    Uri cvfirstUri = Uri.parse("android.resource://" + getPackageName() + "/raw/cv_first");
                    guideline.setText(" 자기소개를 1분 동안 해주세요 ");
                    interviewer.setVideoURI(cvfirstUri);
                    flag[0] = true;
                }
            }
        });
    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {

                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);

            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @SuppressLint("RestrictedApi")
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        // Create a configuration object for the video use case
        VideoCapture.Builder builder = new VideoCapture.Builder();

        final VideoCapture videoCapture = builder
//                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation()) // orientation에 맞추어 촬영 방향 결정
                .setTargetRotation(Surface.ROTATION_90) // 무조건 화면 표시 방향으로 촬영
                .build();

        Intent intent = new Intent();

        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, videoCapture);

        mCaptureButton.setOnClickListener(v -> { //촬영f 시작

            if (!mIsRecordingVideo) {
                mIsRecordingVideo = true;

                File file = null;
                switch (levelCount) {
                    case 1:
                        file = new File(getBatchDirectoryName(), "cv_1.mp4");
                        levelCount = 2;
                        break;
                    case 2:
                        file = new File(getBatchDirectoryName(), "cv_2.mp4");
                        levelCount = 3;
                        break;
                    case 3:
                        file = new File(getBatchDirectoryName(), "cv_3.mp4");
                        intent.putExtra("filename", "full");
                        levelCount = 0;
                        break;
                    case 4:
                        file = new File(getBatchDirectoryName(), "cv_1.mp4");
                        intent.putExtra("filename", "introduce");
                        levelCount = 0;
                        Log.e("file", "4");
                        break;
                    case 5:
                        file = new File(getBatchDirectoryName(), "cv_2.mp4");
                        intent.putExtra("filename", "motive");
                        levelCount = 0;
                        Log.e("file", "5");
                        break;
                    case 6:
                        file = new File(getBatchDirectoryName(), "cv_3.mp4");
                        intent.putExtra("filename", "career");
                        levelCount = 0;
                        Log.e("file", "6");
                        break;
                }

                Drawable camera_stop = getResources().getDrawable(R.drawable.camera_stop, null);
                mCaptureButton.setImageDrawable(camera_stop);

                String[] files = this.fileList();

                VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(file).build();
                videoCapture.startRecording(outputFileOptions, executor, new VideoCapture.OnVideoSavedCallback() {
                    @Override
                    public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                Log.d("tag", "Video Saved Successfully" + Arrays.toString(files)));
                        if (levelCount == 0) {
                            setResult(Activity.RESULT_OK, intent);
                            finishActivity();
                        }

                    }

                    @Override
                    public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                        Log.i("tag", "Video Error: " + message);
                    }
                });
            } else {
                mIsRecordingVideo = false;
                Drawable camera_start = getResources().getDrawable(R.drawable.camera_start, null);
                mCaptureButton.setImageDrawable(camera_start);
                videoCapture.stopRecording();
                Log.d("tag", "Video stopped");
                switch (levelCount) {
                    case 2:
                        guideline.setText(" 이번 공고에 지원하신 동기를 말씀해주세요 ");
                        interviewerVideoStart("android.resource://" + getPackageName() + "/raw/cv_second");
                        break;
                    case 3:
                        guideline.setText(" 관련된 경험을 2가지만 말씀해주세요 ");
                        interviewerVideoStart("android.resource://" + getPackageName() + "/raw/cv_third");
                        break;
                }
            }
        });
    }

    private void finishActivity() {
        this.finish();
    }

    public String getBatchDirectoryName() {

        String app_folder_path = this.getFilesDir().toString() + "/videocv_" + dirName;
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {
        }
        Log.d("TAG", "getBatchDirectoryName: " + app_folder_path);
        String[] testDir = dir.list();
        for (int i = 0; i < testDir.length; i++) {
            Log.e("testDirfilepath", testDir[i]);
        }
        return app_folder_path;
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            Log.d("permission check", permission);
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

}