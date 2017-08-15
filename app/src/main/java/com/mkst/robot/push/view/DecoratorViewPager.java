package com.mkst.robot.push.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/7/31
 * 描述: 自定义ViewPager
 */

public class DecoratorViewPager extends ViewPager {
    private ViewGroup parent;

    public DecoratorViewPager(Context context) {
        super(context);
    }

    public DecoratorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 防止ViewGroup和子类View冲突
     *
     * @param ev
     * @return ev
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 拦截手势
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 点击手势
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(ev);
    }

    public void setNestedpParent(ViewGroup viewGroup) {
        this.parent = viewGroup;
    }
}
