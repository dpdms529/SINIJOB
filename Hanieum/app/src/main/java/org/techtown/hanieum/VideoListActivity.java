package org.techtown.hanieum;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;

import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.CoverLetter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class VideoListActivity extends AppCompatActivity implements View.OnClickListener {

    Button startRecord, introduceRecord, motiveRecord, careerRecord, saveBtn;
    ImageButton delBtn;
    VideoView introducePlayer, motivePlayer, careerPlayer;
    View background1, background2, background3;
    TextView introduceNotice, motiveNotice, careerNotice, title;
    String cv1Path, cv2Path, cv3Path, dirName;
    private ProgressDialog progressDialog;

    AppDatabase db;

    SelfInfo item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        Intent intent = getIntent();
        item = (SelfInfo) intent.getSerializableExtra("edit");

        // creating the progress dialog
        progressDialog = new ProgressDialog(VideoListActivity.this);
        progressDialog.setMessage("동영상을 생성하는 중입니다.\n잠시만 기다려주세요...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

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

        background1 = findViewById(R.id.video_background_1);
        background2 = findViewById(R.id.video_background_2);
        background3 = findViewById(R.id.video_background_3);

        MediaController introduceController = new MediaController(this);
        introducePlayer.setMediaController(introduceController);
        introducePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                introducePlayer.setBackground(null);
            }
        });

        MediaController motiveController = new MediaController(this);
        motivePlayer.setMediaController(motiveController);
        motivePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                motivePlayer.setBackground(null);
            }
        });

        MediaController careerController = new MediaController(this);
        careerPlayer.setMediaController(careerController);
        careerPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                careerPlayer.setBackground(null);
            }
        });

        if (item != null) {
            title.setText("영상 자기소개서 수정");

            dirName = item.getFirst_item();

            cv1Path = this.getFilesDir().toString() + "/videocv_" + dirName + "/cv_1.mp4";
            cv2Path = this.getFilesDir().toString() + "/videocv_" + dirName + "/cv_2.mp4";
            cv3Path = this.getFilesDir().toString() + "/videocv_" + dirName + "/cv_3.mp4";

            Uri uri = Uri.parse(cv1Path);
            introducePlayer.setVideoURI(uri);
            background1.setVisibility(View.GONE);
            getThumbNail(introducePlayer, cv1Path);

            uri = Uri.parse(cv2Path);
            motivePlayer.setVideoURI(uri);
            background2.setVisibility(View.GONE);
            getThumbNail(motivePlayer, cv2Path);

            uri = Uri.parse(cv3Path);
            careerPlayer.setVideoURI(uri);
            background3.setVisibility(View.GONE);
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
                        if (data != null) {
                            String fileName = data.getStringExtra("filename");
                            Uri uri;
                            Log.e("filename", fileName);
                            if (fileName.equals("full")) {
                                uri = Uri.parse(cv1Path);
                                introducePlayer.setVideoURI(uri);
                                introduceRecord.setVisibility(View.VISIBLE);
                                introduceNotice.setVisibility(View.GONE);
                                background1.setVisibility(View.GONE);
                                getThumbNail(introducePlayer, cv1Path);

                                uri = Uri.parse(cv2Path);
                                motivePlayer.setVideoURI(uri);
                                motiveRecord.setVisibility(View.VISIBLE);
                                motiveNotice.setVisibility(View.GONE);
                                background2.setVisibility(View.GONE);
                                getThumbNail(motivePlayer, cv2Path);

                                uri = Uri.parse(cv3Path);
                                careerPlayer.setVideoURI(uri);
                                careerRecord.setVisibility(View.VISIBLE);
                                careerNotice.setVisibility(View.GONE);
                                background3.setVisibility(View.GONE);
                                getThumbNail(careerPlayer, cv3Path);

                                saveBtn.setVisibility(View.VISIBLE);
                            } else if (fileName.equals("introduce")) {

                                uri = Uri.parse(cv1Path);
                                introducePlayer.setVideoURI(uri);
                                getThumbNail(introducePlayer, cv1Path);
                                getThumbNail(motivePlayer, cv2Path);
                                getThumbNail(careerPlayer, cv3Path);

                            } else if (fileName.equals("motive")) {

                                uri = Uri.parse(cv2Path);
                                motivePlayer.setVideoURI(uri);
                                getThumbNail(introducePlayer, cv1Path);
                                getThumbNail(motivePlayer, cv2Path);
                                getThumbNail(careerPlayer, cv3Path);

                            } else if (fileName.equals("career")) {

                                uri = Uri.parse(cv3Path);
                                careerPlayer.setVideoURI(uri);
                                getThumbNail(introducePlayer, cv1Path);
                                getThumbNail(motivePlayer, cv2Path);
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
                new Query.CoverLetterUpdateAsyncTask(db.CoverLetterDao()).execute(hm);
                Toast.makeText(this, "자기소개서가 수정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                CoverLetter coverLetter = new CoverLetter("0", dirName, null, null);
                new Query.CoverLetterInsertAsyncTask(db.CoverLetterDao()).execute(coverLetter);
                Toast.makeText(this, "자기소개서가 저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
            try {
                concatVideos();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (v == delBtn) {

            AlertDialog.Builder msgBuilder = new AlertDialog.Builder(this, R.style.MaterialAlertDialog_OK_color)
                    .setTitle("삭제").setMessage("삭제하시겠습니까?")
                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Query.CoverLetterDeleteAsyncTask(db.CoverLetterDao()).execute(item.getNo());
                            Toast.makeText(getApplicationContext(), "자기소개서가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
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

//    @Override
//    public void onBackPressed() {
//        if(item == null) {
//            fileDelete();
//        }
//        finishActivity();
//    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void concatVideos() throws Exception {
        progressDialog.show();
        String dir = this.getFilesDir().toString() + "/videocv_" + dirName;
        File dest = new File(dir, "cv.mp4");
        String filePath = dest.getAbsolutePath();

        String exe;
        // the "exe" string contains the command to process video.The details of command are discussed later in this post.
        // "video_url" is the url of video which you want to edit. You can get this url from intent by selecting any video from gallery.
        exe = "-y -i " + dir + "/cv_1.mp4" + " -i " + dir + "/cv_2.mp4" + " -i " + dir + "/cv_3.mp4"
                + " -filter_complex \"[0:v]setpts=PTS-STARTPTS,scale=1920x1080,fps=24,format=yuv420p[video0];" +
                "[0:a]aformat=sample_fmts=fltp:sample_rates=48000:channel_layouts=stereo[audio0];" +
                "[1:v]setpts=PTS-STARTPTS,scale=1920x1080,fps=24,format=yuv420p[video1];" +
                "[1:a]aformat=sample_fmts=fltp:sample_rates=48000:channel_layouts=stereo[audio1];" +
                "[2:v]setpts=PTS-STARTPTS,scale=1920x1080,fps=24,format=yuv420p[video2];" +
                "[2:a]aformat=sample_fmts=fltp:sample_rates=48000:channel_layouts=stereo[audio2];" +
                "[video0][audio0][video1][audio1][video2][audio2]" +
                "concat=n=3:v=1:a=1[outv][outa]\" -map \"[outv]\" -map \"[outa]\" " + filePath;

        long executionId = FFmpeg.executeAsync(exe, new ExecuteCallback() {

            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {

                    progressDialog.dismiss();
                    finishActivity();

                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                    progressDialog.dismiss();
                    finishActivity();

                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                    progressDialog.dismiss();
                    finishActivity();

                }
            }
        });
    }

    private void finishActivity() {
        this.finish();
    }

    public void getThumbNail(VideoView videoView, String path) {
        videoView.seekTo(1);
        videoView.setAlpha(1);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
        videoView.setBackground(bitmapDrawable);
    }

    private void fileDelete() {
        File file = new File(this.getFilesDir().toString() + "/videocv_" + dirName);
        File[] childFileList = file.listFiles();

//        String[] testDir = file.list();
//
//        for (int i = 0; i < testDir.length; i++) {
//            Log.e("testDirfilepath", testDir[i]);
//        }
        if(childFileList != null) {
            for(File childFile : childFileList) {
                childFile.delete();
            }
        }


        file.delete();

    }

}