package com.example.multimedia.sticker;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;

public class TextShape extends BaseShape {
    private String mText;
    private Matrix mMatrix;
    private Paint mPaint;
    private RectF mRange;

    public TextShape() {
        mMatrix = new Matrix();
    }

    public void setText(String text) {
        this.mText = text;
    }

    @Override
    public void translate(float dx, float dy) {
        mMatrix.postTranslate(dx, dy);
    }

    @Override
    public void scale(float scaleXRatio, float scaleYRatio, float scaleCenterX, float scaleCenterY) {
        mMatrix.postScale(scaleXRatio, scaleYRatio, scaleCenterX, scaleCenterY);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mText != null && mPaint != null) {
            float matrix[] = new float[9];
            mMatrix.getValues(matrix);
            canvas.save();
            canvas.translate(matrix[2], matrix[5]);
            canvas.scale(matrix[0], matrix[4]);
            canvas.drawText(mText, 0, 0, mPaint);
            canvas.restore();
            //debug code:
//            Paint paint = new Paint();
//            paint.setStrokeWidth(5f);
//            paint.setColor(Color.BLUE);
//            paint.setStyle(Paint.Style.STROKE);
//            canvas.drawRect(getRange(), paint);
        }

    }

    @Override
    public RectF getRange() {
        //cjz: you can see this to know why I do that:https://www.jianshu.com/p/c83f59613c18
        if (mText != null && mPaint != null) {
            float matrix[] = new float[9];
            mMatrix.getValues(matrix);
            mRange = new RectF(
                    matrix[2],
                    matrix[5] - mPaint.getTextSize() * matrix[4],
                    matrix[2] + (mText.length() * mPaint.getTextSize()) * matrix[0],
                    matrix[5] + mPaint.getTextSize() / 2f * matrix[4]);
            return mRange;
        }
        return null;
    }

    @Override
    public void setPaint(Paint paint, Shader shader) {
        this.mPaint = new Paint(paint);
        if (shader != null) {
            this.mPaint.setShader(shader);
        }
    }

    @Override
    public PointF getScale() {
        float matrix[] = new float[9];
        mMatrix.getValues(matrix);
        return new PointF(matrix[0], matrix[4]);
    }

    @Override
    public PointF getXY() {
        float matrix[] = new float[9];
        mMatrix.getValues(matrix);
        return new PointF(matrix[2],
                matrix[5] - mPaint.getTextSize() * matrix[4]);
    }
}
