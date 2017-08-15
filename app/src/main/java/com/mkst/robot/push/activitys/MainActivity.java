package com.mkst.robot.push.activitys;

import android.content.Context;
import android.content.Intent;

import com.mkst.robot.push.R;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.service.SetStaticIPService;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/7/27
 * 描述: 主页
 */
public class MainActivity extends Activity {

    public static boolean DeskIsEdit = false,AreaIsEdit = false;
    public static int Current_INDEX = 1;

    /**
     * MainActivity 显示的入口
     *
     * @param context 上下文
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        startService(new Intent(this, SetStaticIPService.class));
    }

    @Override
    protected void initData() {

    }
}
