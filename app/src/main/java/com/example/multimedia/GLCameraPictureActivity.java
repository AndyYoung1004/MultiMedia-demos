package com.example.multimedia;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class GLCameraPictureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_camera_pic);
        findViewById(R.id.shot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraTextureView textureView = findViewById(R.id.texture_view);
                textureView.take();
            }
        });
    }
}
