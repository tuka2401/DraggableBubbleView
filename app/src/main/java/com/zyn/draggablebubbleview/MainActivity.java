package com.zyn.draggablebubbleview;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.tip);
        DraggableBubbleListener listener = new DraggableBubbleListener(this, view, Color.RED, new DraggableBubbleView.OnDraggbleListener()
        {
            @Override
            public void OnBubbleDismiss(PointF point)
            {

            }

            @Override
            public void OnBubbleReset(PointF point)
            {

            }
        });
        view.setOnTouchListener(listener);
    }
}
