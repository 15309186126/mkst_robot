package com.mkst.robot.push.activitys;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.util.Property;
import android.view.View;

import com.mkst.robot.push.R;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.helper.RobotDBHelper;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/7/27
 * 描述: 闪屏页
 */

public class LaunchActivity extends Activity {
    // Drawable
    private ColorDrawable mBgDrawable;

    @Override
    protected int getContentLayoutId() {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        return R.layout.activity_launch;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //拿到根布局
        View root = findViewById(R.id.activity_launch);
        //获取颜色
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        //创建一个Drawable
        ColorDrawable drawable = new ColorDrawable(color);
        //设置给背景
        root.setBackground(drawable);
        mBgDrawable = drawable;

    }

    @Override
    protected void initData() {
        super.initData();
        skip();
    }
    /**
     * 在跳转之前需要把剩下的50%进行完成
     */
    private void skip() {
        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                //初始化数据库
                RobotDBHelper.getInstance(getApplicationContext());
                reallySkip();
            }
        });
    }

    /**
     * 真实的跳转
     */
    private void reallySkip() {
        MainActivity.show(this);
        finish();

    }

    /**
     * 给背景设置一个动画
     *
     * @param endProgress 动画的结束进度
     * @param endCallback 动画结束时触发
     */
    private void startAnim(float endProgress, final Runnable endCallback) {
        // 获取一个最终的颜色
        int finalColor = Resource.Color.WHITE; // UiCompat.getColor(getResources(), R.color.white);
        // 运算当前进度的颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);
        // 构建一个属性动画
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        valueAnimator.setDuration(3000); // 时间
        valueAnimator.setIntValues(mBgDrawable.getColor(), endColor); // 开始结束值
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 结束时触发
                endCallback.run();
            }
        });
        valueAnimator.start();
    }


    private final Property<LaunchActivity, Object> property = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }
        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }
    };
}
