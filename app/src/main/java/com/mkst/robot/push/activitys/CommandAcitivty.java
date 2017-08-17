package com.mkst.robot.push.activitys;

import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mkst.robot.push.R;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.helper.RobotDBHelper;

import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/8
 * 描述:
 */

public class CommandAcitivty extends Activity {
    private RobotDBHelper robotDBHelper;
    private int command_id;
    private Map commandConfig;
    private List<Map> goalList;
    private List<Map> list;
    private TextView speed, mp3, outime, shownum, showcolor;
    private TextView goal, direction;
    public static int goalnum = -1, directionnum = -1;

    @Override
    protected int getContentLayoutId() {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_command_config;
    }
}
