package com.mkst.robot.push.activitys;

import android.view.Window;
import android.view.WindowManager;

import com.mkst.robot.push.R;
import com.mkst.robot.push.app.Activity;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/17
 * 描述:
 */

public class CardConfigActivity extends Activity {
    @Override
    protected int getContentLayoutId() {
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_cardactivity;
    }
}
