package com.example.multimedia.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.multimedia.GLSimpleRenderer;
import com.example.multimedia.GLSurfaceViewRenderer;
import com.example.multimedia.R;

public class GLSurfaceViewActivity extends Activity {
    private static final int REQUEST_CODE_PICK_VIDEO = 2;
    private String filePath = "/sdcard/DCIM/HEVC.mp4";
    private GLSurfaceView glView;
    private GLSurfaceViewRenderer renderer;
    private GLSimpleRenderer renderer1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurfaceview);
        glView = findViewById(R.id.glSfView);
        glView.setEGLContextClientVersion(2);
//        renderer = new GLSurfaceViewRenderer(getApplicationContext(), filePath);//创建renderer
//        glView.setRenderer(renderer);//设置renderer
        renderer1 = new GLSimpleRenderer(getApplicationContext(), filePath);
        glView.setRenderer(renderer1);
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

    @Override
    protected void onPause() {
        super.onPause();
        renderer1.release();
    }
}
