package com.example.multimedia;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

public class GLSurfaceViewActivity extends Activity {
    private static final int REQUEST_CODE_PICK_VIDEO = 2;
    private String filePath;
    private GLSurfaceView glView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurfaceview);
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
                glView = findViewById(R.id.glSfView);
                glView.setEGLContextClientVersion(2);
                GLSurfaceViewRenderer glVideoRenderer = new GLSurfaceViewRenderer(getApplicationContext(), filePath);//创建renderer
                glView.setRenderer(glVideoRenderer);//设置renderer
            }
        });
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
