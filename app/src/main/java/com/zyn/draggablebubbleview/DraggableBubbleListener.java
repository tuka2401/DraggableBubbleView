package com.zyn.draggablebubbleview;


import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class DraggableBubbleListener implements View.OnTouchListener,DraggableBubbleView.OnDraggbleListener
{
    private View mView;
    private DraggableBubbleView mDraggableBubbleView;
    private DraggableBubbleView.OnDraggbleListener mListener;
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    public DraggableBubbleListener(Context context, View view,@ColorInt int color,DraggableBubbleView.OnDraggbleListener listener)
    {
        mContext = context;
        mView = view;
        mDraggableBubbleView = new DraggableBubbleView(context);
        mDraggableBubbleView.setColor(color);
        mListener = listener;
        mDraggableBubbleView.setListener(this);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
//                ViewParent parent = v.getParent();
//                // 请求其父级View不拦截Touch事件
//                parent.requestDisallowInterceptTouchEvent(true);
                mView.setVisibility(View.INVISIBLE);
                float radius = Math.min(mView.getWidth()/2,mView.getHeight()/2);
                int[] position = new int[2];
                mView.getLocationInWindow(position);
                float x = position[0]+radius;
                float y = position[1]+radius;
                mDraggableBubbleView.initParams(new PointF(x,y),radius);
                mWindowManager.addView(mDraggableBubbleView,mLayoutParams);
            }
            break;
            case MotionEvent.ACTION_MOVE:
            {

            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            {

            }
            break;
        }

        return mDraggableBubbleView.onTouchEvent(event);
    }

    @Override
    public void OnBubbleDismiss(PointF point)
    {
        mWindowManager.removeView(mDraggableBubbleView);
        mView.setVisibility(View.GONE);
        if(mListener!=null)
        {
            mListener.OnBubbleDismiss(point);
        }
    }

    @Override
    public void OnBubbleReset(PointF point)
    {
        mWindowManager.removeView(mDraggableBubbleView);
        mView.setVisibility(View.VISIBLE);
        if(mListener!=null)
        {
            mListener.OnBubbleReset(point);
        }
    }
}
