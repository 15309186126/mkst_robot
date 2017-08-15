package com.mkst.robot.push.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/2
 * 描述: 常量类
 */

public class Constant {
    //打印Log
    private static final String TAG = "Robot";
    //Wifi名称
    public static String wifiname = "MAIKESITE";
    //Wifi密码
    public static String password = "MKST8888";

    //wifi静态ip参数
    static final String dns1 = "192.168.1.1";
    static final String dns2 = "192.168.0.1";
    static String gateway = "";
    static int prefix = 24;
    static String IP = "";

    static final String IPLast = "178";
    static String isConnectSocket = "";

    //Socket服务器端口
    public static int ServerPort = 20123;
    public static int ClientPort = 20123;

    //是否打印日志
    private static final boolean isDebug = true;
    //地图默认配置地图
    public static String filePath = "data/data/com.android.jdrd.headcontrol/cache/map.xml";
    public static int linearWidth;
    //消息解析字段
    public static String Type = "type";
    public static String Function = "function";
    public static String Data = "data";
    public static String Command = "command";
    public static String State = "state";

    public static String Navigation = "navigation";
    public static String Peoplesearch = "peoplesearch";
    public static String Roamsearch = "roamsearch";

    public static String StopSearch = "stop";
    public static String Result = "result";

    public static String Camera = "3dcamera";
    public static String Obstacle = "obstacle";

    //唯一对象
    private static Constant constant;

    public static Constant getConstant() {
        if (constant != null) {
            return constant;
        } else {
            constant = new Constant();
            return constant;
        }
    }

    //打印日志
    public static void debugLog(String debug) {
        Log.e(TAG, debug);
    }

    public void sendCamera(float paramFloat, Context paramContext) {
        Intent intent = new Intent();
        if (paramFloat == 0) {
            intent.putExtra("msg", "远");
        } else if (paramFloat == 1) {
            intent.putExtra("msg", "中");
        } else if (paramFloat == 2) {
            intent.putExtra("msg", "近");
        } else {
            intent.putExtra("msg", "关闭");
        }
        intent.setAction("com.mkst.CursorSDKExample.TD_CAMERA");
        paramContext.sendBroadcast(intent);
    }
}
