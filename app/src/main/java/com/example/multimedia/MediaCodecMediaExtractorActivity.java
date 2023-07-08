package com.example.multimedia;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class MediaCodecMediaExtractorActivity extends Activity {
    private static final int REQUEST_CODE_PICK_VIDEO = 2;
    private String filePath = "/sdcard/DCIM/HEVC.mp4";
    private SurfaceView surfaceView;
    AVplayer avplayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediacodecmediaextractor);
        surfaceView = findViewById(R.id.sfView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {}

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releasePlayer();
            }

        });
        findViewById(R.id.selectvideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK_VIDEO);
            }
        });
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlayer(filePath);
            }
        });
        findViewById(R.id.release).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releasePlayer();
            }
        });
    }

    private void startPlayer(String filepath) {
        avplayer = new AVplayer(filepath, surfaceView.getHolder().getSurface());
        avplayer.playMediaFile();
    }

    private void releasePlayer() {
        if (avplayer != null) {
            avplayer.releasePlayer();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_CODE_PICK_VIDEO:
                if (resultCode == RESULT_OK && data != null) {
                    ContentResolver contentResolver = getContentResolver();
                    Uri uri = data.getData();
                    Cursor cursor = contentResolver.query(uri, null, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                            filePath = "/sdcard/DCIM/HEVC.mp4";
                        }
                    }
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
