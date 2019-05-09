package com.smasher.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.smasher.draw.AliPaySuccessView;
import com.smasher.draw.BlurMaskFilterView;
import com.smasher.draw.CanvasView;
import com.smasher.draw.R;
import com.smasher.util.DensityUtil;
import com.smasher.util.TintUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matao
 */
public class MainActivity extends AppCompatActivity {


    @BindView(R.id.blurMask)
    BlurMaskFilterView blurMask;

    Unbinder mUnbinder;
    @BindView(R.id.canvasView)
    CanvasView canvasView;
    @BindView(R.id.success)
    AliPaySuccessView success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);


        Drawable arrowOrigin = ContextCompat.getDrawable(this, R.drawable.icon_arrow_down_black);
        Drawable arrow = null;
        if (arrowOrigin != null) {
            arrow = TintUtil.tintDrawable(arrowOrigin, Color.WHITE);
        }
        if (arrow != null) {
            int width = DensityUtil.dip2px(12);
            int height = DensityUtil.dip2px(8);
            arrow.setBounds(0, 0, width, height);
        }
        Drawable background = ContextCompat.getDrawable(this, R.drawable.selector_blurmask_button);

        blurMask.setCompoundDrawablePadding(DensityUtil.dip2px(10));
        blurMask.setCompoundDrawables(null, null, arrow, null);
        blurMask.setTextBackground(background);
        blurMask.setTextColor(Color.WHITE);
        blurMask.setText("测试一下");

    }

    @OnClick(R.id.blurMask)
    public void onViewClicked() {
        success.showAnimation();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
