package com.iamlarry.snaphelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author larryycliu on 2018/1/3.
 */

public class TimelineBlackMaskView extends View {

    private Paint mPaint;

    private int mColor;

    public TimelineBlackMaskView(Context context) {
        super(context);
        init(context);
    }

    public TimelineBlackMaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimelineBlackMaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mColor = context.getResources().getColor(R.color.timeline_black_mask);
        mPaint = new Paint();
        mPaint.setColor(mColor);

//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mColor);
    }
}
