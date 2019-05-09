package com.smasher.draw;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author matao
 * @date 2019/5/9
 */
public class AliPaySuccessView extends View {

    private static final String TAG = "AliPaySuccessView";
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private Path mCirclePath;
    private Path mSuccessPath;
    private Path mDstPath;

    private float mCurAnimValue;
    private float mSuccessValue;


    public AliPaySuccessView(Context context) {
        super(context);
        init(context);
    }

    public AliPaySuccessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AliPaySuccessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public AliPaySuccessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);


        mPaint = new Paint();
        mPathMeasure = new PathMeasure();
        mCirclePath = new Path();
        mSuccessPath = new Path();
        mDstPath = new Path();
    }


    public void showAnimation() {

        clear();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurAnimValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(1000);


        ValueAnimator animator1 = ValueAnimator.ofFloat(0, 1);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSuccessValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator1.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animator, animator1);
        animatorSet.start();
    }


    boolean clear;

    public void clear() {
        clear = true;
        mCurAnimValue = 0f;
        mSuccessValue = 0f;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);

        mCirclePath.addCircle(300, 300, 50, Path.Direction.CW);
        mPathMeasure.setPath(mCirclePath, true);

        mDstPath.reset();
        float stop = mPathMeasure.getLength() * mCurAnimValue;
        mPathMeasure.getSegment(0, stop, mDstPath, true);
        canvas.drawPath(mDstPath, mPaint);


        mSuccessPath.moveTo(300 - 20, 300 - 3);
        mSuccessPath.lineTo(300 - 3, 300 + 15);
        mSuccessPath.lineTo(300 + 20, 300 - 15);

        mDstPath.reset();
        mPathMeasure.nextContour();
        mPathMeasure.setPath(mSuccessPath, false);
        float current = mPathMeasure.getLength() * mSuccessValue;
        mPathMeasure.getSegment(0, current, mDstPath, true);
        canvas.drawPath(mDstPath, mPaint);

    }
}
