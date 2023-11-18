package com.example.multimedia.sticker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;

public class BitmapShape extends BaseShape {

    private Matrix mMatrix;
    private Bitmap mBitmap;
    private RectF mRange;

    public BitmapShape() {
        setmShapeType(ShapeType.BITMAP);
        mMatrix = new Matrix();
    }

    public void setmBitmap(Bitmap b) {
        this.mBitmap = b;
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
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, mMatrix, null);
            //debug code:
//            Paint paint = new Paint();
//            paint.setStrokeWidth(5f);
//            paint.setColor(Color.RED);
//            paint.setStyle(Paint.Style.STROKE);
//            canvas.drawRect(getRange(), paint);
        }

    }

    @Override
    public RectF getRange() {
        //cjz: you can see this to know why I do that:https://www.jianshu.com/p/c83f59613c18
        float matrix[] = new float[9];
        mMatrix.getValues(matrix);
        mRange = new RectF(
                matrix[2],
                matrix[5],
                matrix[2] + mBitmap.getWidth() * matrix[0],
                matrix[5] + mBitmap.getHeight() * matrix[4]);
        return mRange;
    }

    @Override
    public void setPaint(Paint paint, Shader shader) {

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
        return new PointF(matrix[2] + (mBitmap.getWidth() * matrix[0]) / 2f, matrix[5] + (mBitmap.getHeight() * matrix[4]) / 2f);
    }

}