package com.example.multimedia.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


import com.example.multimedia.R;

import java.io.IOException;

public class MediaPlayerActivity extends Activity {
    private static final int REQUEST_CODE_PICK_VIDEO = 2;
    private String filePath;
    private SurfaceView surfaceView;
    private MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplayer);
        surfaceView = findViewById(R.id.sfView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {}


            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}


            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {}

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    private void startPlayer(String filepath) {
        mp = new MediaPlayer();
        try {
            mp.setDataSource(filepath);
            mp.setDisplay(surfaceView.getHolder());
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
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
                        }
                    }
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
