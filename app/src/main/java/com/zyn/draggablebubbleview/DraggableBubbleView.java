package com.zyn.draggablebubbleview;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import com.wangjie.androidbucket.utils.ABAppUtil;

public class DraggableBubbleView extends View
{
    private static final String TAG = DraggableBubbleView.class.getSimpleName();
    //LayoutParams
    RelativeLayout.LayoutParams mLp; // 实际的layoutparams
    RelativeLayout.LayoutParams mTouchLp; // 触摸时候的LayoutParams
    Path path = new Path();
    private Context mContext;
    private OnDraggbleListener mListener;//回调
    //point
    private PointF mStartPoint = new PointF();//初始point
    private PointF mEndPoint = new PointF();//触摸point
    //半径
    private float mStartRadius = 60;
    private float mEndRadius = 60;
    //勾股
    private Triangle mTriangle = new Triangle();//计算两圆之间的勾股关系
    //可拖动最长距离
    private int mMaxDragLength;
    //Paint
    private Paint mPaint; // 绘制圆形图形
    //属性
    private
    @ColorInt
    int mColor;
    private int mWidth;//初始宽
    private int mHeight;//初始高
    //Boolean
    private boolean mIsTouched; // 是否是触摸状态
    private boolean mIsFirst = true;//是否第一次绘制
    private ValueAnimator mAnimator;
    private boolean mIsDismiss;

    public DraggableBubbleView(Context context)
    {
        this(context, null);
    }

    public DraggableBubbleView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public DraggableBubbleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //初始化属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DraggableBubbleView);
        mColor = a.getColor(R.styleable.DraggableBubbleView_dbv_color, Color.RED);
        a.recycle();
        //初始化
        init();
    }

    /**
     * 初始化
     */
    private void init()
    {
        setBackgroundColor(Color.TRANSPARENT);
        mMaxDragLength = ABAppUtil.getDeviceHeight(mContext) / 10;
        // 设置绘制flag的paint
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        //回弹动画
        final float delay = 0.2f;
        final float unit = 1.0f;
        mAnimator = ValueAnimator.ofFloat(unit);
        mAnimator.setInterpolator(new OvershootInterpolator(4));
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            float triangleX = 0.0f;
            float triangleY = 0.0f;

            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float percent = animation.getAnimatedFraction();
                if (percent == 0.0f)
                {
                    triangleX = mTriangle.x;
                    triangleY = mTriangle.y;
                }
                float x = triangleX * (unit - percent);
                float y = triangleY * (unit - percent);
                setEndPoint(mStartPoint.x + x, mStartPoint.y + y);
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mListener != null)
                {
                    mListener.OnBubbleReset(mStartPoint);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                if (mListener != null)
                {
                    mListener.OnBubbleReset(mStartPoint);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });
    }

    public void initParams(PointF point, float radius)
    {
        setPoint(point);
        setRadius(radius);
    }

    public void setRadius(float radius)
    {
        mStartRadius = radius;
        mEndRadius = radius;
        invalidate();
    }

    public void setPoint(PointF point)
    {
        setStartPoint(point.x, point.y);
        resetEndPoint();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (!mIsDismiss)
        {
            float curRadius = (int) (mStartRadius * (1 - mTriangle.getZ() / mMaxDragLength));
            if (curRadius < mStartRadius * 0.2)
            {
                curRadius = (float) (mStartRadius * 0.2);
            }

            float startX = curRadius * mTriangle.getYos();
            float startY = curRadius * mTriangle.getXos();
            float endX = mEndRadius * mTriangle.getYos();
            float endY = mEndRadius * mTriangle.getXos();
            float centerX = (mStartPoint.x + mEndPoint.x) / 2;
            float centerY = (mStartPoint.y + mEndPoint.y) / 2;

            //触摸圆
            canvas.drawCircle(mEndPoint.x, mEndPoint.y, mEndRadius, mPaint);

            if (mTriangle.getZ() <= mMaxDragLength)
            { //初始圆
                canvas.drawCircle(mStartPoint.x, mStartPoint.y, curRadius, mPaint);
                //连接部分
                path.reset();
                path.moveTo(mStartPoint.x + startX, mStartPoint.y - startY);
                path.quadTo(centerX, centerY, mEndPoint.x + endX, mEndPoint.y - endY);
                path.lineTo(mEndPoint.x - endX, mEndPoint.y + endY);
                path.quadTo(centerX, centerY, mStartPoint.x - startX, mStartPoint.y + startY);
                path.close();
                canvas.drawPath(path, mPaint);
            }
        }
    }

    public OnDraggbleListener getListener()
    {
        return mListener;
    }

    public void setListener(OnDraggbleListener listener)
    {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                mIsDismiss = false;
            }
            case MotionEvent.ACTION_MOVE:
            {
                setEndPoint(event.getRawX(), event.getRawY());
                invalidate();
            }
            return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            {
                if (mTriangle.getZ() <= mMaxDragLength)
                {
                    mAnimator.start();
                } else
                {
                    mIsDismiss = true;
                    resetEndPoint();
                    if (mListener != null)
                        mListener.OnBubbleDismiss(mEndPoint);
                }
            }
            return true;
        }
        return false;
    }

    private void setEndPoint(float x, float y)
    {
        mEndPoint.set(x, y);
        calculateTriangle();
        invalidate();
    }

    private void setStartPoint(float x, float y)
    {
        mStartPoint.set(x, y);
        calculateTriangle();
        invalidate();
    }

    public void setColor(@ColorInt int color)
    {
        mColor = color;
        mPaint.setColor(color);
        invalidate();
    }

    private void calculateTriangle()
    {
        float x = mEndPoint.x - mStartPoint.x;
        float y = mEndPoint.y - mStartPoint.y;
        mTriangle.set(x, y);
    }

    private void resetEndPoint()
    {
        setEndPoint(mStartPoint.x, mStartPoint.y);
    }

    public interface OnDraggbleListener
    {
        void OnBubbleDismiss(PointF point);

        void OnBubbleReset(PointF point);
    }

    public class Triangle
    {
        private float x;
        private float y;
        private float z;//斜边
        private float xos;//x/z
        private float yos;//y/z

        public void set(float x, float y)
        {
            this.x = x;
            this.y = y;
            z = (float) Math.sqrt(x * x + y * y);
            xos = x / z;
            yos = y / z;
        }

        public float getXos()
        {
            return xos;
        }

        public float getYos()
        {
            return yos;
        }

        public float getZ()
        {
            return z;
        }
    }
}
