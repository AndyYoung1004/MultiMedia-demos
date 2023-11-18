package com.example.multimedia.sticker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BaseShape {
    private PointF mCurrentCenter = new PointF();
    private PointF mPrevCurrentCenter = null;
    private float mPrevDistance = Float.MIN_VALUE;
    private float mAvergeX = 0, mAvergeY = 0;
    private int mPrevPointCount = 0;

    /**是否已经缩放过**/
    private boolean mIsScaled = false;
    private Queue<Float> mTouchDistanceQueue = new LinkedBlockingQueue<>();
    private PointF mPrevLocation;

    /**水印类型**/
    public enum ShapeType {
        BITMAP,
        TEXT
    }
    private ShapeType mShapeType = null;

    public void setmShapeType(ShapeType mShapeType) {
        this.mShapeType = mShapeType;
    }

    public ShapeType getmShapeType() {
        Paint paint;
        return mShapeType;
    }

    /**移动代码实现**/
    public abstract void translate(float dx, float dy);

    /**缩放代码实现**/
    public abstract void scale(float scaleXRatio, float scaleYRatio, float scaleCenterX, float scaleCenterY);

    /**绘制代码实现**/
    public abstract void draw(Canvas canvas);

    /**了解显示范围**/
    public abstract RectF getRange();

    /**设置显示效果**/
    public abstract void setPaint(Paint paint, Shader shader);

    /**了解缩放量**/
    public abstract PointF getScale();

    /**了解位置**/
    public abstract PointF getXY();

    /**触摸处理**/
    public boolean touchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevDistance = 0;
                mPrevPointCount = event.getPointerCount();
                //算出移动中心坐标、点间距离
                for (int i = 0; i < event.getPointerCount(); i++) {
                    mAvergeX += event.getX(i);
                    mAvergeY += event.getY(i);
                    if (i + 1 < event.getPointerCount()) {
                        mPrevDistance += Math.sqrt(Math.pow(event.getX(i + 1) - event.getX(i), 2) + Math.pow(event.getY(i + 1) - event.getY(i), 2));
                    }
                }
                mAvergeX /= event.getPointerCount();
                mAvergeY /= event.getPointerCount();
                mPrevLocation = getXY();
                if (mPrevCurrentCenter == null) {
                    mPrevCurrentCenter = new PointF(mAvergeX, mAvergeY);
                } else {
                    mPrevCurrentCenter.set(mAvergeX, mAvergeY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mAvergeX = 0;
                mAvergeY = 0;
                float nowDistance = 0;
                //算出移动中心坐标、点间距离
                for (int i = 0; i < event.getPointerCount(); i++) {
                    mAvergeX += event.getX(i);
                    mAvergeY += event.getY(i);
                    if (i + 1 < event.getPointerCount()) {
                        nowDistance += Math.sqrt(Math.pow(event.getX(i + 1) - event.getX(i), 2) + Math.pow(event.getY(i + 1) - event.getY(i), 2));
                    }
                }
                //现在的点间距离 除以 上次点间距离 这次得到缩放比例
                mAvergeX /= event.getPointerCount();
                mAvergeY /= event.getPointerCount();
                if ((mPrevPointCount != event.getPointerCount()) || event.getPointerCount() <= 1 || mPrevPointCount <= 1) { //触摸点数突然改变 或者 触摸点不超过2，不允许缩放
                    mPrevDistance = nowDistance = 0;
                }
                //如果缩放数据有效，则进行平均平滑化并且进行缩放
                if (mPrevDistance > 0 && nowDistance > 0) {
                    mTouchDistanceQueue.add(nowDistance / mPrevDistance);
                    if (mTouchDistanceQueue.size() >= 6) {
                        Float point[] = new Float[mTouchDistanceQueue.size()];
                        mTouchDistanceQueue.toArray(point);
                        float avergDistance = 0;
                        for (int i = 0; i < point.length; i++) {
                            avergDistance += point[i];
                        }
                        avergDistance /= point.length;
//                        scale((float) Math.sqrt(avergDistance), (float) Math.sqrt(avergDistance), mAvergeX, mAvergeY);
                        scale((float) Math.sqrt(avergDistance), (float) Math.sqrt(avergDistance), event.getX(0), event.getY(0));
                        mIsScaled = true;
                        while (mTouchDistanceQueue.size() > 6) {
                            mTouchDistanceQueue.poll();
                        }
                    }
                }
                mPrevPointCount = event.getPointerCount();
                mPrevDistance = nowDistance;
                //当前坐标 - 上次坐标 = 偏移值，然后进行位置偏移
                if (mPrevCurrentCenter == null) {
                    mPrevCurrentCenter = new PointF(mAvergeX, mAvergeY);
                } else {
                    if (!mIsScaled && event.getPointerCount() == 1) {
                        translate(mAvergeX - mPrevCurrentCenter.x, mAvergeY - mPrevCurrentCenter.y);
                    }
                    mPrevCurrentCenter.set(mAvergeX, mAvergeY);
                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起，清理干净数据
                mAvergeX = 0;
                mAvergeY = 0;
                mTouchDistanceQueue.clear();
                mIsScaled = false;
                break;
        }
        return true;
    }
}