package com.example.multimedia;

import android.content.Context;

public class MyEGLSurface extends BaseEGLSurface {
    public MyEGLSurface(Context context) {
        super(context);
    }

    public MyEGLSurface(Context context, int width, int height) {
        super(context, width, height);
    }

    public void init(Renderer renderer) {
        setRenderer(renderer);
        createEGLEnv();
    }
}
