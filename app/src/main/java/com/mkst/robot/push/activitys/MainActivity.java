package com.mkst.robot.push.activitys;

import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;

import com.mkst.robot.push.R;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.service.ClientSocketUtil;
import com.mkst.robot.push.service.ServerSocketUtil;
import com.mkst.robot.push.service.SetStaticIPService;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/7/27
 * 描述: 主页
 */
public class MainActivity extends Activity {

    public static boolean DeskIsEdit = false, AreaIsEdit = false;
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
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //静态Wifi服务
        Intent SetStaticIPService = new Intent(this, SetStaticIPService.class);
        startService(SetStaticIPService);
        //启动后台通讯服务  服务端
        Intent serverSocket = new Intent(this, ServerSocketUtil.class);
        startService(serverSocket);
/*        //启动后台通讯服务  客务端
        Intent clientSocket = new Intent(this, ClientSocketUtil.class);
        startService(clientSocket);*/
    }

    @Override
    protected void initData() {

    }
}
