package org.techtown.hanieum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
    private AppCompatButton mCaptureButton;
    private TextView guideline;
    private String recordType;
    private String dirName;
    private GradientDrawable buttonShape;

    private boolean mIsRecordingVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mPreviewView = findViewById(R.id.previewView);
        mCaptureButton = findViewById(R.id.camera_capture_button);
        guideline = findViewById(R.id.textView10);
        buttonShape = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.record_button);
        levelCount = 1;

        Intent intent = getIntent();
        recordType = intent.getStringExtra("recordType");
        dirName = intent.getStringExtra("dirName");
        Log.e("dirName", intent.getStringExtra("dirName"));
        if (recordType.equals("full")) {
            guideline.setText("전체 촬영");
        } else if (recordType.equals("introduce")) {
            guideline.setText("자기소개 촬영");
            levelCount = 4;
        } else if (recordType.equals("motive")) {
            guideline.setText("지원동기 촬영");
            levelCount = 5;
        } else if (recordType.equals("career")) { // recordType == "career"
            guideline.setText("경력소개 촬영");
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

                buttonShape.setColor(Color.parseColor("#4CAF50"));
                mCaptureButton.setBackground(buttonShape);
//                mCaptureButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                mCaptureButton.setText("종료");

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
                buttonShape.setColor(Color.parseColor("#FF9800"));
                mCaptureButton.setBackground(buttonShape);
//                mCaptureButton.setBackgroundColor(Color.parseColor("#FF9800"));
                mCaptureButton.setText("시작");
                videoCapture.stopRecording();
                Log.d("tag", "Video stopped");
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