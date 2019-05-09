package com.smasher.draw;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


/**
 * @author matao
 * @date 2019/5/7
 */
public class BlurMaskFilterView extends ViewGroup {

    private static final String TAG = "BlurMaskFilterView";
    private Paint mPaint;
    private RectF rectF;
    private int mColor;

    private int invented;
    private int radius;

    private MaskFilter mMaskFilter;
    private AppCompatTextView mTextView;
    private int mTextPadding;
    private String mText;


    public BlurMaskFilterView(Context context) {
        super(context);
        init(context, null);
    }

    public BlurMaskFilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BlurMaskFilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @TargetApi(21)
    public BlurMaskFilterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);

        int defaultPadding = 26;
        int defaultColor = Color.parseColor("#ED424B");
        int defaultInvented = 53;

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BlurMaskFilterView);
        mTextPadding = array.getDimensionPixelSize(R.styleable.BlurMaskFilterView_blur_text_padding, defaultPadding);
        mColor = array.getColor(R.styleable.BlurMaskFilterView_blur_color, defaultColor);
        invented = array.getDimensionPixelSize(R.styleable.BlurMaskFilterView_blur_width, defaultInvented);
        radius = array.getDimensionPixelSize(R.styleable.BlurMaskFilterView_blur_radius, defaultInvented);
        mText = array.getString(R.styleable.BlurMaskFilterView_blur_text);
        array.recycle();


        mTextView = new AppCompatTextView(context);
        mTextView.setPadding(mTextPadding, mTextPadding, mTextPadding, mTextPadding);
        addView(mTextView);


        mPaint = new Paint();
        rectF = new RectF();
        mMaskFilter = new BlurMaskFilter(invented, BlurMaskFilter.Blur.OUTER);

    }


    public void setTextBackground(Drawable drawable) {
        if (mTextView != null) {
            mTextView.setBackground(drawable);
        }
    }


    public void setTextColor(int color) {
        if (mTextView != null) {
            mTextView.setTextColor(color);
        }

    }


    public void setTextColor(ColorStateList colorStateList) {
        if (mTextView != null) {
            mTextView.setTextColor(colorStateList);
        }
    }


    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (mTextView != null) {
            mTextView.setCompoundDrawables(left, top, right, bottom);
        }
    }


    public void setCompoundDrawablePadding(int padding) {
        if (mTextView != null) {
            mTextView.setCompoundDrawablePadding(padding);
        }
    }


    public void setText(CharSequence text) {
        mText = text.toString();
        if (mTextView != null) {
            mTextView.setText(mText);
            invalidate();
        }
    }


    @Override
    public void setOnClickListener(@Nullable View.OnClickListener l) {
        if (mTextView != null) {
            mTextView.setOnClickListener(l);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(mTextView, widthMeasureSpec, heightMeasureSpec);
        int width = onMeasureIWidth(widthMeasureSpec);
        int height = onMeasureIHeight(heightMeasureSpec);
        Log.d(TAG, "onMeasure: ");
        setMeasuredDimension(width, height);
    }

    private int onMeasureIWidth(int oldMeasure) {
        int width = 0;
        int mode = MeasureSpec.getMode(oldMeasure);
        int size = MeasureSpec.getSize(oldMeasure);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                width = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                int childWidth = mTextView.getMeasuredWidth();
                width = (getPaddingLeft() + invented * 2 + childWidth + getPaddingRight());
                break;
            default:
                break;
        }
        return width;
    }


    private int onMeasureIHeight(int oldMeasure) {
        int height = 0;
        int mode = MeasureSpec.getMode(oldMeasure);
        int size = MeasureSpec.getSize(oldMeasure);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                height = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                int childHeight = mTextView.getMeasuredHeight();
                height = (getPaddingTop() + invented * 2 + childHeight + getPaddingBottom());
                break;
            default:
                break;
        }
        return height;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        Log.d(TAG, "onLayout: ");
        int childWidth = mTextView.getMeasuredWidth();
        int childHeight = mTextView.getMeasuredHeight();

        int childLeft = (getMeasuredWidth() - childWidth) / 2;
        int childTop = (getMeasuredHeight() - childHeight) / 2;
        int childRight = (getMeasuredWidth() + childWidth) / 2;
        int childBottom = (getMeasuredHeight() + childHeight) / 2;

        Log.d(TAG, "onLayout: " + childLeft + "--" + childTop
                + "--" + childRight + "--" + childBottom);

        mTextView.layout(childLeft, childTop, childRight, childBottom);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: ");

        int childWidth = mTextView.getMeasuredWidth();
        int childHeight = mTextView.getMeasuredHeight();
//        Log.d(TAG, "onDraw: " + childWidth + "--" + childHeight);

        int childLeft = (getMeasuredWidth() - childWidth) / 2;
        int childTop = (getMeasuredHeight() - childHeight) / 2;
        int childRight = (getMeasuredWidth() + childWidth) / 2;
        int childBottom = (getMeasuredHeight() + childHeight) / 2;
//        Log.d(TAG, "onDraw: " + childLeft + "--" + childTop
//                + "--" + childRight + "--" + childBottom);
        rectF.set(childLeft, childTop, childRight, childBottom);

        mPaint.setColor(mColor);
        mPaint.setMaskFilter(mMaskFilter);
        canvas.drawRoundRect(rectF, radius, radius, mPaint);
    }
}
