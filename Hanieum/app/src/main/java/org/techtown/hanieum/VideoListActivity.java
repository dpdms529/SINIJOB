package org.techtown.hanieum;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.CoverLetter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class VideoListActivity extends AppCompatActivity implements View.OnClickListener {

    Button startRecord, introduceRecord, motiveRecord, careerRecord, saveBtn;
    ImageButton delBtn;
    VideoView introducePlayer, motivePlayer, careerPlayer;
    TextView introduceNotice, motiveNotice, careerNotice, title;
    String cv1Path, cv2Path, cv3Path, dirName;

    AppDatabase db;

    SelfInfo item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        Intent intent = getIntent();
        item = (SelfInfo) intent.getSerializableExtra("edit");

        startRecord = findViewById(R.id.start_record);
        introduceRecord = findViewById(R.id.introduce_retake);
        motiveRecord = findViewById(R.id.motive_retake);
        careerRecord = findViewById(R.id.career_retake);
        introducePlayer = findViewById(R.id.introduce_player);
        motivePlayer = findViewById(R.id.motive_player);
        careerPlayer = findViewById(R.id.career_player);
        introduceNotice = findViewById(R.id.introduce_notice);
        motiveNotice = findViewById(R.id.motive_notice);
        careerNotice = findViewById(R.id.career_notice);
        title = findViewById(R.id.title);
        delBtn = findViewById(R.id.delBtn);
        saveBtn = findViewById(R.id.saveButton);

        MediaController introduceController = new MediaController(this);
        introducePlayer.setMediaController(introduceController);
        introducePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
            }
        });

        MediaController motiveController = new MediaController(this);
        motivePlayer.setMediaController(motiveController);
        motivePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
            }
        });

        MediaController careerController = new MediaController(this);
        careerPlayer.setMediaController(careerController);
        careerPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
            }
        });

        if (item != null) {
            title.setText("글 자기소개서 수정");

            dirName = item.getFirst_item();

            cv1Path = this.getFilesDir().toString() + "/videocv_" + dirName + "/cv_1.mp4";
            cv2Path = this.getFilesDir().toString() + "/videocv_" + dirName + "/cv_2.mp4";
            cv3Path = this.getFilesDir().toString() + "/videocv_" + dirName + "/cv_3.mp4";

            Uri uri = Uri.parse(cv1Path);
            introducePlayer.setVideoURI(uri);
            getThumbNail(introducePlayer, cv1Path);

            uri = Uri.parse(cv2Path);
            motivePlayer.setVideoURI(uri);
            getThumbNail(motivePlayer, cv2Path);

            uri = Uri.parse(cv3Path);
            careerPlayer.setVideoURI(uri);
            getThumbNail(careerPlayer, cv3Path);

            saveBtn.setVisibility(View.VISIBLE);
            introduceRecord.setVisibility(View.VISIBLE);
            motiveRecord.setVisibility(View.VISIBLE);
            careerRecord.setVisibility(View.VISIBLE);
            introduceNotice.setVisibility(View.GONE);
            motiveNotice.setVisibility(View.GONE);
            careerNotice.setVisibility(View.GONE);
            delBtn.setVisibility(View.VISIBLE);
            delBtn.setOnClickListener(this);
        } else {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
            dirName = sdf.format(date);

            cv1Path = this.getFilesDir().toString() + "/videocv_" + dirName + "/cv_1.mp4";
            cv2Path = this.getFilesDir().toString() + "/videocv_" + dirName + "/cv_2.mp4";
            cv3Path = this.getFilesDir().toString() + "/videocv_" + dirName + "/cv_3.mp4";
        }




        db = AppDatabase.getInstance(this);

        startRecord.setOnClickListener(this);
        introduceRecord.setOnClickListener(this);
        motiveRecord.setOnClickListener(this);
        careerRecord.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data != null) {
                            String fileName = data.getStringExtra("filename");
                            Uri uri;
                            Log.e("filename",fileName);
                            if(fileName.equals("full")) {
                                uri = Uri.parse(cv1Path);
                                introducePlayer.setVideoURI(uri);
                                introduceRecord.setVisibility(View.VISIBLE);
                                introduceNotice.setVisibility(View.GONE);
                                getThumbNail(introducePlayer, cv1Path);

                                uri = Uri.parse(cv2Path);
                                motivePlayer.setVideoURI(uri);
                                motiveRecord.setVisibility(View.VISIBLE);
                                motiveNotice.setVisibility(View.GONE);
                                getThumbNail(motivePlayer, cv2Path);

                                uri = Uri.parse(cv3Path);
                                careerPlayer.setVideoURI(uri);
                                careerRecord.setVisibility(View.VISIBLE);
                                careerNotice.setVisibility(View.GONE);
                                getThumbNail(careerPlayer, cv3Path);

                                saveBtn.setVisibility(View.VISIBLE);
                            } else if(fileName.equals("introduce")) {

                                uri = Uri.parse(cv1Path);
                                introducePlayer.setVideoURI(uri);
                                getThumbNail(introducePlayer, cv1Path);

                            } else if(fileName.equals("motive")) {

                                uri = Uri.parse(cv2Path);
                                motivePlayer.setVideoURI(uri);
                                getThumbNail(motivePlayer, cv2Path);

                            } else if(fileName.equals("career")) {

                                uri = Uri.parse(cv3Path);
                                careerPlayer.setVideoURI(uri);
                                getThumbNail(careerPlayer, cv3Path);

                            }
                        }
                    }
                }
            });

    @Override
    public void onClick(View v) {
        if (v == startRecord) {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            intent.putExtra("recordType", "full");
            intent.putExtra("dirName", dirName);
            launcher.launch(intent);
        } else if (v == introduceRecord) {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            intent.putExtra("recordType", "introduce");
            intent.putExtra("dirName", dirName);
            launcher.launch(intent);
        } else if (v == motiveRecord) {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            intent.putExtra("recordType", "motive");
            intent.putExtra("dirName", dirName);
            launcher.launch(intent);
        } else if (v == careerRecord) {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            intent.putExtra("recordType", "career");
            intent.putExtra("dirName", dirName);
            launcher.launch(intent);
        } else if (v == saveBtn) {
            if (item != null) {
                HashMap<Integer, String> hm = new HashMap<>();
                hm.put(1, dirName);
                hm.put(2, null);
                hm.put(3, null);
                hm.put(4, Integer.toString(item.getNo()));
                new CoverLetterActivity.CoverLetterUpdateAsyncTask(db.CoverLetterDao()).execute(hm);
                Toast.makeText(this, "자기소개서가 수정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                CoverLetter coverLetter = new CoverLetter("0", dirName, null, null);
                new CoverLetterActivity.CoverLetterInsertAsyncTask(db.CoverLetterDao()).execute(coverLetter);
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
                            new CoverLetterActivity.CoverLetterDeleteAsyncTask(db.CoverLetterDao()).execute(item.getNo());
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SelfInfoActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        Uri filepathUri = Uri.parse(cv1Path);
        introduceThumb.setVideoURI(filepathUri);

    }
     */
    public void getThumbNail(VideoView videoView, String path) {
        videoView.seekTo(1);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
        videoView.setBackgroundDrawable(bitmapDrawable);
    }

}