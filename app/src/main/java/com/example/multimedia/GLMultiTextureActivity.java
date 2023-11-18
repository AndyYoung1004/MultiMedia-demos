package com.example.multimedia;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;


import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLMultiTextureActivity extends Activity {
    // 绘制图片的原理：定义一组矩形区域的顶点，然后根据纹理坐标把图片作为纹理贴在该矩形区域内。
    // 原始的矩形区域的顶点坐标，因为后面使用了顶点法绘制顶点，所以不用定义绘制顶点的索引。无论窗口的大小为多少，在OpenGL二维坐标系中都是为下面表示的矩形区域
    static final float CUBE[] = { // 窗口中心为OpenGL二维坐标系的原点（0,0）
            -1.0f, -1.0f, // v1
            1.0f, -1.0f,  // v2
            -1.0f, 1.0f,  // v3
            1.0f, 1.0f,   // v4
    };
    static final float CUBE2[] = { // 窗口中心为OpenGL二维坐标系的原点（0,0）
            -1.0f, 0.5f, // v1
            0f, 0.5f,  // v2
            -1.0f, 1.0f,  // v3
            0f, 1.0f,   // v4
    };
    // 纹理也有坐标系，称UV坐标，或者ST坐标。UV坐标定义为左上角（0，0），右下角（1，1），一张图片无论大小为多少，在UV坐标系中都是图片左上角为（0，0），右下角（1，1）
    // 纹理坐标，每个坐标的纹理采样对应上面顶点坐标。
    public static final float TEXTURE_NO_ROTATION[] = {
            0.0f, 1.0f, // v1
            1.0f, 1.0f, // v2
            0.0f, 0.0f, // v3
            1.0f, 0.0f, // v4
    };

    private GLSurfaceView mGLSurfaceView;
    private int mGLTextureId = OpenGlUtils.NO_TEXTURE; // 纹理id
    private int mWatermarkTextureId = OpenGlUtils.NO_TEXTURE; // 纹理id
    private GLImageHandler mGLImageHandler = new GLImageHandler();

    private FloatBuffer mGLCubeBuffer, mGLCubeBuffer2;
    private FloatBuffer mGLTextureBuffer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glpicture);
        mGLSurfaceView = findViewById(R.id.glSfView);
        mGLSurfaceView.setEGLContextClientVersion(2); // 创建OpenGL ES 2.0 的上下文环境

        mGLSurfaceView.setRenderer(new MyRender());
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); // 手动刷新
    }

    private class MyRender implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 1);
            GLES20.glDisable(GLES20.GL_DEPTH_TEST); // 当我们需要绘制透明图片时，就需要关闭它
            mGLImageHandler.init();

            // 需要显示的图片
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thelittleprince);
            Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.dog);
            // 把图片数据加载进GPU，生成对应的纹理id
            mGLTextureId = OpenGlUtils.loadTexture(bitmap, mGLTextureId, true); // 加载背景纹理
            mWatermarkTextureId = OpenGlUtils.loadTexture(bitmap2, mWatermarkTextureId, true); // 加载水印纹理

            // 顶点数组缓冲器
            mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGLCubeBuffer.put(CUBE).position(0);

            mGLCubeBuffer2 = ByteBuffer.allocateDirect(CUBE2.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGLCubeBuffer2.put(CUBE2).position(0);

            // 纹理数组缓冲器
            mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGLTextureBuffer.put(TEXTURE_NO_ROTATION).position(0);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height); // 设置窗口大小
        }

        @Override
        public void onDrawFrame(GL10 gl) { // 绘制
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            drawBackground();
            drawWatermark();
        }

        private void drawBackground() {
            // 根据纹理id，顶点和纹理坐标数据绘制图片
            mGLImageHandler.onDraw(mGLCubeBuffer, mGLTextureBuffer);
        }

        private void drawWatermark() {
            mGLImageHandler.onDraw(mGLCubeBuffer2, mGLTextureBuffer);
        }
    }
}
