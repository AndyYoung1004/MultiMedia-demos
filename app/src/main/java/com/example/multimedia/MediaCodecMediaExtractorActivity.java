package com.example.multimedia;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.multimedia.player.AVPlayer;


public class MediaCodecMediaExtractorActivity extends Activity {
    private String filePath = "/sdcard/DCIM/HEVC.mp4";
    private SurfaceView surfaceView;
    AVPlayer avplayer;
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
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlayer();
            }
        });
    }

    private void startPlayer() {
        avplayer = new AVPlayer();
        avplayer.setDataSource(filePath);
        avplayer.setDisplay(surfaceView.getHolder().getSurface());
        avplayer.prepare();
        avplayer.start();
    }

    private void releasePlayer() {
        if (avplayer != null) {
            avplayer.releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
