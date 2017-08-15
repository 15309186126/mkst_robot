package com.mkst.robot.push.activitys;

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
    private Map commandconfig;
    private List<Map> goallist;
    private List<Map> list;
    private TextView speed, mp3, outime, shownum, showcolor;
    private TextView goal, direction;
    public static int goalnum = -1, directionnum = -1;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_command_config;
    }
}
