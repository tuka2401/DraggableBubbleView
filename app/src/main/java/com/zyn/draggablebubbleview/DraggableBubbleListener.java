package com.zyn.draggablebubbleview;


import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;

import com.wangjie.androidbucket.utils.ABAppUtil;
import com.wangjie.androidbucket.utils.ABViewUtil;

public class DraggableBubbleListener implements View.OnTouchListener,DraggableBubbleView.OnDraggbleListener
{
    private View mView;
    private DraggableBubbleView mDraggableBubbleView;
    private DraggableBubbleView.OnDraggbleListener mListener;
    private Context mContext;
    private WindowManager.LayoutParams mLayoutParams;
    private ViewGroup mDecorView;

    public DraggableBubbleListener(Context context, View view,@ColorInt int color,DraggableBubbleView.OnDraggbleListener listener)
    {
        mContext = context;
        mView = view;
        mDraggableBubbleView = new DraggableBubbleView(context);
        mDraggableBubbleView.setColor(color);
        mListener = listener;
        mDraggableBubbleView.setListener(this);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mDecorView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView();
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
                float radius = Math.min(mView.getWidth()/2,mView.getHeight()/2);
                int[] position = new int[2];
                mView.getLocationInWindow(position);
                float x = position[0]+radius;
                float y = position[1]+radius;
                mDraggableBubbleView.initParams(new PointF(x, y), radius);
                mDecorView.addView(mDraggableBubbleView,mLayoutParams);
                mView.setVisibility(View.INVISIBLE);
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
        mView.setVisibility(View.GONE);
        mDecorView.removeView(mDraggableBubbleView);
        if(mListener!=null)
        {
            mListener.OnBubbleDismiss(point);
        }
    }
    @Override
    public void OnBubbleReset(final PointF point)
    {
        mView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mView.setVisibility(View.VISIBLE);
                try
                {
                    mDecorView.removeView(mDraggableBubbleView);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                if (mListener != null)
                {
                    mListener.OnBubbleReset(point);
                }
            }
        }, 200);
    }
}
