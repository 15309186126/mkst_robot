package com.mkst.robot.push.dialog;

import android.app.Dialog;
import android.content.Context;

import com.mkst.robot.push.R;

import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/8
 * 描述: 自定义机器人运行轨迹对话框
 */

public class RobotDialog extends Dialog {

    private Context context;
    private static String sendStr;
    private static boolean flag;
    private static String IP;
    public static Thread thread = new Thread();

    private static List<Map> list;
    private static List<Map> robotList;



    public RobotDialog(Context context, String str) {
        super(context, R.style.SoundRecorder);
        setCustomDialog();
        this.context = context;
        this.sendStr = str;
        flag = false;
    }

    public RobotDialog(Context context, List<Map> robotList) {
        super(context, R.style.SoundRecorder);
        setCustomDialog();
        this.context = context;
        this.robotList = robotList;
        flag = true;

    }


    private void setCustomDialog() {

    }

}
