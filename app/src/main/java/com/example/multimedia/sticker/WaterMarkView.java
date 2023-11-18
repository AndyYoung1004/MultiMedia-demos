package com.example.multimedia.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import androidx.annotation.RequiresApi;

import java.util.LinkedList;
import java.util.List;


public class WaterMarkView extends View {
    private Bitmap mBitmap;
    private List<BaseShape> mShapesList = new LinkedList<>();
    private BitmapShape mBitmapShape;
    private BaseShape mCurrentTouchShape;


    public WaterMarkView(Context context) {
        super(context);
        init();
    }

    public WaterMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaterMarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), android.R.mipmap.sym_def_app_icon);
        for(int i = 0; i < 3; i++) {
            BitmapShape bs  = new BitmapShape();
            bs.setmBitmap(mBitmap);
            bs.translate(50 * i, 50 * i);
            mShapesList.add(bs);
        }
        TextShape textShape = new TextShape();
        Paint paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(150f);
        textShape.setPaint(paint, null);
        textShape.setText("测试测试测试");
        textShape.translate(200, 200);
        mShapesList.add(textShape);

        textShape = new TextShape();
        paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(Color.argb(0.5f, 0.1f, 0.5f, 0.5f));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(100f);
        textShape.setPaint(paint, null);
        textShape.setText("水印编辑器Demo");
        textShape.translate(400, 500);
        mShapesList.add(textShape);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                for (int i = mShapesList.size() - 1; i >=0; i--) { //从顶传递到底
                    BaseShape shape = mShapesList.get(i);
                    RectF shapeRange = shape.getRange();
                    if (shapeRange != null && shapeRange.contains(event.getX(), event.getY())) {
                        mCurrentTouchShape = shape;
                        //置顶选中图案
                        mShapesList.remove(mCurrentTouchShape);
                        mShapesList.add(mShapesList.size(), mCurrentTouchShape);
                        mCurrentTouchShape.touchEvent(event);
                        break;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mCurrentTouchShape != null) {
                    mCurrentTouchShape.touchEvent(event);
                    break;
                }
            }
            case MotionEvent.ACTION_UP: {
                if (mCurrentTouchShape != null) {
                    mCurrentTouchShape.touchEvent(event);
                    mCurrentTouchShape = null;
                    break;
                }
            }

        }
        invalidate();
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (BaseShape view : mShapesList) {
            view.draw(canvas);
        }
    }
}
