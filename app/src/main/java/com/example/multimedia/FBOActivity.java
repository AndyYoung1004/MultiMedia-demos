package com.example.multimedia;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class FBOActivity extends Activity implements Model.Callback {
    private ImageView mImageView;
    private MyEGLSurface mEGlSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbo);
        mImageView = findViewById(R.id.imageView);
        initEGLSurface();
        mEGlSurface.requestRender();
    }

    private void initEGLSurface() {
        mEGlSurface = new MyEGLSurface(this);
        MyRender render = new MyRender(getResources());
        render.setCallback(this);
        mEGlSurface.init(render);
    }

    @Override
    public void onCall(final Bitmap bitmap) {
        runOnUiThread(() -> {
            mImageView.setImageBitmap(bitmap);
        });
    }
}