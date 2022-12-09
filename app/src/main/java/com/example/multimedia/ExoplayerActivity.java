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

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;


public class ExoplayerActivity extends Activity {
    private static final int REQUEST_CODE_PICK_VIDEO = 2;
    private String filePath;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private SimpleExoPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplayer);
        surfaceView = findViewById(R.id.sfView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                surfaceHolder = holder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

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
                startPlayer();
            }
        });
    }

    private void startPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                getApplicationContext(),
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(),
                new DefaultLoadControl()
        );
        player.setVideoSurfaceHolder(surfaceHolder);
        player.prepare(buildMediaSource(Uri.parse(filePath)), true, false);
        player.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, "AndyYoung");
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        return videoSource;
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
