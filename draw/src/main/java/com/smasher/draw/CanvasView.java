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
import android.util.Log;
import android.view.View;

/**
 * @author matao
 * @date 2019/5/9
 */
public class CanvasView extends View {
    private static final String TAG = "CanvasView";
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private Path mPath;
    private Path mPath1;
    private Path mDstPath;

    private Path mCirclePath;
    private Path mSuccessPath;
    private float mCurAnimValue;
    private float mSuccessValue;

    public CanvasView(Context context) {
        super(context);
        init(context);
    }


    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {

        setLayerType(LAYER_TYPE_SOFTWARE, null);


        mPaint = new Paint();
        mPathMeasure = new PathMeasure();
        mPath = new Path();
        mPath1 = new Path();
        mDstPath = new Path();

        mCirclePath = new Path();
        mCirclePath.addCircle(300, 300, 50, Path.Direction.CW);
        mPathMeasure.setPath(mCirclePath, true);

        mSuccessPath = new Path();


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


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.translate(10, 10);
//
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);


        canvas.translate(270, 160);
        //添加闭合路径矩形   Path.Direction.CW 顺时针
        mPath1.addRect(-50, -50, 50, 50, Path.Direction.CW);
        mPath1.addRect(-100, -100, 100, 100, Path.Direction.CW);
        mPath1.addRect(-120, -120, 120, 120, Path.Direction.CW);
        canvas.drawPath(mPath1, mPaint);


        mPathMeasure.setPath(mCirclePath, true);
        float stop = mPathMeasure.getLength() * mCurAnimValue;
        mDstPath.reset();
        mPathMeasure.getSegment(0, stop, mDstPath, true);
        canvas.drawPath(mDstPath, mPaint);


        mSuccessPath.moveTo(300 - 20, 300 - 3);
        mSuccessPath.lineTo(300 - 3, 300 + 15);
        mSuccessPath.lineTo(300 + 20, 300 - 15);
        mPathMeasure.nextContour();
        mPathMeasure.setPath(mSuccessPath, false);
        mDstPath.reset();
        float current = mPathMeasure.getLength() * mSuccessValue;
        mPathMeasure.getSegment(0, current, mDstPath, true);
        canvas.drawPath(mDstPath, mPaint);


    }
}
