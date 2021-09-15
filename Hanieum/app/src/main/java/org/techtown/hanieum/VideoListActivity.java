package org.techtown.hanieum;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class VideoListActivity extends AppCompatActivity implements View.OnClickListener {

    Button startRecord, introduceRecord, motiveRecord, careerRecord;
    ImageView introduceThumb, motiveThumb, careerThumb;
    TextView introduceNotice, motiveNotice, careerNotice;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int REQUEST_PERMISSIONS = 1;
    private static final String[] MY_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        startRecord = findViewById(R.id.start_record);
        introduceRecord = findViewById(R.id.introduce_retake);
        motiveRecord = findViewById(R.id.motive_retake);
        careerRecord = findViewById(R.id.career_retake);
        introduceThumb = findViewById(R.id.introduce_thumb);
        motiveThumb = findViewById(R.id.motive_thumb);
        careerThumb = findViewById(R.id.career_thumb);
        introduceNotice = findViewById(R.id.introduce_notice);
        motiveNotice = findViewById(R.id.motive_notice);
        careerNotice = findViewById(R.id.career_notice);

        if(ActivityCompat.checkSelfPermission(VideoListActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            boolean permission = hasAllPermissionGranted();
            Log.e("permission", "permission" + permission);
            if(!permission) {

            }
            getGallery();
        } else {
            getGallery();
        }

        startRecord.setOnClickListener(this);
        introduceRecord.setOnClickListener(this);
        motiveRecord.setOnClickListener(this);
        careerRecord.setOnClickListener(this);
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e("activity result","in");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri returnImg = data.getData();
                            if("com.google.android.apps.photos.contentprovider".equals(returnImg.getAuthority())) {
                                for (int i=0;i<returnImg.getPathSegments().size();i++) {
                                    String temp = returnImg.getPathSegments().get(i);
                                    Log.e("filepath",temp);
                                    if(temp.startsWith("content://")) {
                                        returnImg = Uri.parse(temp);
                                        break;
                                    }
                                }
                            }
                            Bitmap bm = getThumbNail(returnImg);
                            introduceThumb.setImageBitmap(bm);
                            introduceNotice.setText("");

                        }
                    }
                }
            });

    @Override
    public void onClick(View v) {
        if (v == startRecord) {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivity(intent);
        } else if (v == introduceRecord) {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivity(intent);
        } else if (v == motiveRecord) {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivity(intent);
        } else if (v == careerRecord) {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            int index = 0;
            for(String permission : permissions) {
                if(permission.equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    getGallery();
                    break;
                }
                index++;
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private Bitmap getThumbNail(Uri uri) {
        Log.e("uri", "uri " + uri);
        MediaMetadataRetriever mediaMetadataRetriever = null;
        Bitmap bitmap = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(this, uri);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1000000,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    private void getGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        launcher.launch(intent);
    }

    public boolean hasAllPermissionGranted() {
        for (String permission : MY_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, MY_PERMISSIONS, REQUEST_PERMISSIONS);
                return false;
            }
        }
        return true;
    }
}
