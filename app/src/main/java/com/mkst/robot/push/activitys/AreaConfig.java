package com.mkst.robot.push.activitys;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.mkst.robot.push.R;
import com.mkst.robot.push.adapter.AreaConfigAdapter;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.helper.RobotDBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/9
 * 描述:
 */

public class AreaConfig extends Activity {

    private AreaConfigAdapter myAdapter;
    private ListView areaList;
    public static int Current_position = -1;
    private RobotDBHelper robotDBHelper;
    private Map robotConfig;
    private static int robotId;
    private List<Map> area_list = new ArrayList<>();

    @Override
    protected int getContentLayoutId() {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_areaconfig;
    }
}
