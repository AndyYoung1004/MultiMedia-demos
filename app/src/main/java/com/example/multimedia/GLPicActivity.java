package com.example.multimedia;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLPicActivity extends Activity {
    // 数据中有多少个顶点，管线就调用多少次顶点着色器
    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" + // 顶点着色器的顶点坐标,由外部程序传入
            "attribute vec4 inputTextureCoordinate;\n" + // 传入的纹理坐标
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" + // 最终顶点位置
            "}";

    // 光栅化后产生了多少个片段，就会插值计算出多少个varying变量，同时渲染管线就会调用多少次片段着色器
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" + // 最终顶点位置，上面顶点着色器的varying变量会传递到这里
            " \n" +
            "uniform sampler2D inputImageTexture;\n" + // 外部传入的图片纹理 即代表整张图片的数据
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +  // 调用函数 进行纹理贴图
            "}";

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
    private FloatBuffer mGLCubeBuffer, mGLCubeBuffer2, mGLTextureBuffer;
    private int mGLProgramId;
    private int mPosHandle;
    private int mTextureId, mTextureId2;
    private int mTextureCoordHandle;
    private String mVertexShader, mFragmentShader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glpicture);
        mVertexShader = NO_FILTER_VERTEX_SHADER;
        mFragmentShader = NO_FILTER_FRAGMENT_SHADER;
        mGLSurfaceView = findViewById(R.id.glSfView);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(new MyRender());
    }

    private class MyRender implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 1);
            GLES20.glDisable(GLES20.GL_DEPTH_TEST); // 当我们需要绘制透明图片时，就需要关闭它
            mGLProgramId = OpenGlUtils.loadProgram(mVertexShader, mFragmentShader);
            mPosHandle = GLES20.glGetAttribLocation(mGLProgramId, "position");
            mTextureCoordHandle = GLES20.glGetAttribLocation(mGLProgramId, "inputTextureCoordinate");
            // 需要显示的图片
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thelittleprince);
            Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.dog);

            int textures[] = new int[] {-1, -1};
            GLES20.glGenTextures(2, textures, 0);

            mTextureId = textures[0];
            mTextureId2 = textures[1];

            GLES20.glActiveTexture(GLES20.GL_TEXTURE);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT); // S轴的拉伸方式为重复，决定采样值的坐标超出图片范围时的采样方式
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT); // T轴的拉伸方式为重复
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId2);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT); // S轴的拉伸方式为重复，决定采样值的坐标超出图片范围时的采样方式
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT); // T轴的拉伸方式为重复
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap2, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);

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
            // 根据纹理id，顶点和纹理坐标数据绘制图片
            GLES20.glUseProgram(mGLProgramId);
            drawBackGround();
            drawWatermark();
        }

        void drawBackGround() {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
            GLES20.glEnableVertexAttribArray(mPosHandle);
            GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
            mGLCubeBuffer.position(0);
            GLES20.glVertexAttribPointer(mPosHandle, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
            mGLTextureBuffer.position(0);
            GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);
            GLES20.glDisableVertexAttribArray(mPosHandle);
            GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        }

        void drawWatermark() {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId2);
            GLES20.glEnableVertexAttribArray(mPosHandle);
            GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
            mGLCubeBuffer2.position(0);
            GLES20.glVertexAttribPointer(mPosHandle, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer2);
            mGLTextureBuffer.position(0);
            GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);
            GLES20.glDisableVertexAttribArray(mPosHandle);
            GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        }
    }

}
