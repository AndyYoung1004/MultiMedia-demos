package com.example.multimedia;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.multimedia.player.AVPlayer2;


public class MediaCodecMediaExtractorActivity extends Activity {
    private String filePath = "/sdcard/DCIM/HEVC.mp4";
    private SurfaceView surfaceView;
    AVPlayer2 avplayer;
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
            public void surfaceDestroyed(SurfaceHolder holder) {}

        });
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlayer();
            }
        });
    }

    private void startPlayer() {
        avplayer = new AVPlayer2();
        avplayer.setDataSource(filePath);
        avplayer.setDisplay(surfaceView.getHolder().getSurface());
        avplayer.prepare();
        avplayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
