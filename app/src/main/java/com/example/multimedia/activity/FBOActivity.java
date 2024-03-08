package com.example.multimedia.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.widget.ImageView;
import com.example.multimedia.BaseEGLSurface;
import com.example.multimedia.R;
import com.example.multimedia.utils.FileUtils;
import com.example.multimedia.utils.OpenGlUtils;
import com.example.multimedia.utils.ShaderUtils;

import java.io.BufferedOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class FBOActivity extends Activity {
    private ImageView imageView;
    private BaseEGLSurface baseEGLSurface;
    private MyRender render;
    private int outputWidth = 1000;
    private int outputHeight = 1000;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbo);
        imageView = findViewById(R.id.imageView);
        baseEGLSurface = new BaseEGLSurface(this);
        render = new MyRender();
        baseEGLSurface.setRenderer(render);
        baseEGLSurface.createEGLEnv();
        baseEGLSurface.requestRender();
    }

    private class MyRender implements BaseEGLSurface.Renderer {
        private static final int TEXTURE_DIMENSION = 2; // 纹理坐标维度
        private static final int VERTEX_DIMENSION = 3; // 顶点坐标维度
        private Bitmap outputBitmap;
        private Bitmap inputBitmap;
        private float mVertex[] = {-1.0f, 1.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f};
        private float[] mFboTexture = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f};
        protected FloatBuffer mVertexBuffer;
        protected FloatBuffer mFboTextureBuffer;
        private int[] mFrameBufferId = new int[1];
        private int[] mTextureId = new int[2];
        private int mProgramId;

        public MyRender() {
            mVertexBuffer = ByteBuffer.allocateDirect(mVertex.length * Float.BYTES)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mVertexBuffer.put(mVertex);
            mVertexBuffer.position(0);
            mFboTextureBuffer = ByteBuffer.allocateDirect(mFboTexture.length * Float.BYTES)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mFboTextureBuffer.put(mFboTexture);
            mFboTextureBuffer.position(0);
            inputBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thelittleprince);
        }

        @Override
        public void onSurfaceCreated() {
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            //启动深度测试
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //创建程序id
            String vertexShader = ShaderUtils.readRawTextFile(getApplicationContext(), R.raw.pic_vertex_shader);
            String fragmentShader = ShaderUtils.readRawTextFile(getApplicationContext(), R.raw.pic_fragment_shader);
            mProgramId = ShaderUtils.createProgram(vertexShader, fragmentShader);

            GLES30.glGenTextures(2, mTextureId, 0);
            {
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId[0]);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT); // S轴的拉伸方式为重复，决定采样值的坐标超出图片范围时的采样方式
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT); // T轴的拉伸方式为重复

                GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, inputBitmap, 0);
            }
            {
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId[1]);//接下来所有对2D操作的，都会作用到这个id上面
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, outputWidth, outputHeight,
                        0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GLES30.GL_NONE);
                // 创建帧缓存id
                GLES30.glGenFramebuffers(1, mFrameBufferId, 0);
                // 绑定帧缓存，表示这个fbo是一个framebuffer，后面操作的framebuffer都是这个id
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferId[0]);
                // 将第二个纹理附着在帧缓存的颜色附着点上
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, mTextureId[1], 0);
                // 取消绑定帧缓存
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_NONE);
            }
        }

        @Override
        public void onSurfaceChanged(int width, int height) {

        }

        @Override
        public void onDrawFrame() {
            GLES30.glClearColor(0.5f, 0.7f, 0.3f, 1.0f);
            // 将颜色缓存区设置为预设的颜色
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
            GLES30.glViewport(0, 0, outputWidth, outputHeight);
            // 启用顶点的数组句柄
            GLES30.glEnableVertexAttribArray(0);
            GLES30.glEnableVertexAttribArray(1);
            // 绘制模型
            GLES30.glUseProgram(mProgramId);
            // 准备顶点坐标和纹理坐标
            GLES30.glVertexAttribPointer(0, VERTEX_DIMENSION, GLES30.GL_FLOAT, false, 0, mVertexBuffer);
            GLES30.glVertexAttribPointer(1, TEXTURE_DIMENSION, GLES30.GL_FLOAT, false, 0, mFboTextureBuffer);
            // 激活纹理
            GLES30.glActiveTexture(GLES30.GL_TEXTURE);
            // 绑定纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId[0]);
            // 绑定缓存
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferId[0]);
            // 绘制贴图
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
            showBitmap(outputWidth, outputHeight);
            // 禁止顶点数组句柄
            GLES30.glDisableVertexAttribArray(0);
            GLES30.glDisableVertexAttribArray(1);
        }
        private void showBitmap(int width, int height) {
            // 分配字节缓区大小， 一个像素4个字节
            ByteBuffer byteBuffer = ByteBuffer.allocate(width * height * Integer.BYTES);
            GLES30.glReadPixels(0, 0, width, height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, byteBuffer);
            outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 从缓存区读二进制缓冲数据
            outputBitmap.copyPixelsFromBuffer(byteBuffer);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(outputBitmap);
                }
            });
            FileUtils.savePic("/sdcard/DCIM/123.jpeg", outputBitmap);
        }
    }
}