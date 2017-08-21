package com.mkst.robot.push.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.mkst.robot.push.R;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.helper.RobotDBHelper;
import com.mkst.robot.push.utils.Constant;

import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/16
 * 描述: 机器人状态
 */

public class RobotActivity extends Activity implements View.OnClickListener {

    //数据库帮助类
    private RobotDBHelper robotDBHelper;
    private int robotId; // 编号
    private Map robotConfig;// 信息
    private MyReceiver receiver;


    @Override
    protected int getContentLayoutId() {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_robot;
    }

    @Override
    protected void initWidget() {
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        Intent intent = getIntent();
        robotId = intent.getIntExtra("id", 0);
        //设置
        findViewById(R.id.setting_redact).setOnClickListener(this);
        //返回
        findViewById(R.id.setting_back).setOnClickListener(this);
        //初始化广播
        receiver = new MyReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.jdrd.activity.Robot");
        //注册广播
        if (receiver != null) {
            this.registerReceiver(receiver, filter);
        }
    }

    //初始化数据
    public void init() {
        //查询机器人
        List<Map> robotList = robotDBHelper.queryListMap("select * from robot where id = '" + robotId + "'", null);
        robotConfig = robotList.get(0);
        //打印log日志
        Constant.debugLog("robot====------>" + robotList.toString());
        Constant.debugLog("robotConfig====------>" + robotConfig.toString());

        //查询机器人运行区域
        List<Map> areaList = robotDBHelper.queryListMap("select * from area where id = '" + robotConfig.get("area") + "'", null);
        //打印log日志
        Constant.debugLog("area====------>" + areaList.toString());

        //设置区域名称
        if (areaList != null && areaList.size() > 0) {
            ((TextView) findViewById(R.id.area)).setText(areaList.get(0).get("name").toString());
        }

        //机器人在线状态   1->在线     0->离线
        if ((int) robotConfig.get("outline") == 1) {
            ((TextView) findViewById(R.id.outline)).setText("在线");
        } else {
            ((TextView) findViewById(R.id.outline)).setText("离线");
        }

        //机器人状态  0->空闲   1->送餐   2->故障
        if ((int) robotConfig.get("robotstate") == 0) {
            ((TextView) findViewById(R.id.robot_state)).setText("空闲");
        } else if ((int) robotConfig.get("robotstate") == 1) {
            ((TextView) findViewById(R.id.robot_state)).setText("送餐");
        } else if ((int) robotConfig.get("robotstate") == 2) {
            ((TextView) findViewById(R.id.robot_state)).setText("故障");
        }

        //机器人运行状态  0->直行前进    0->左转    0->右转    0->旋转
        if ((int) robotConfig.get("state") == 0) {
            ((TextView) findViewById(R.id.state)).setText("直行前进");
        } else if ((int) robotConfig.get("state") == 1) {
            ((TextView) findViewById(R.id.state)).setText("左转");
        } else if ((int) robotConfig.get("state") == 2) {
            ((TextView) findViewById(R.id.state)).setText("右转");
        } else if ((int) robotConfig.get("state") == 3) {
            ((TextView) findViewById(R.id.state)).setText("旋转");
        }

        //获取信息
        ((TextView) findViewById(R.id.name)).setText(robotConfig.get("name").toString());
        ((TextView) findViewById(R.id.ip)).setText(robotConfig.get("ip").toString());
        ((TextView) findViewById(R.id.electric)).setText(robotConfig.get("electric").toString());
        ((TextView) findViewById(R.id.command_num)).setText(robotConfig.get("commandnum").toString());
        ((TextView) findViewById(R.id.last_location)).setText(robotConfig.get("lastlocation").toString());
        if ((int) robotConfig.get("obstacle") == 0) {
            ((TextView) findViewById(R.id.obstacle)).setText("无");
        } else {
            ((TextView) findViewById(R.id.obstacle)).setText("有");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击设置
            case R.id.setting_redact:
                Intent intent = new Intent(RobotActivity.this, RobotConfigActivity.class);
                intent.putExtra("id", robotId);
                startActivity(intent);
                break;
            //点击返回
            case R.id.setting_back:
                finish();
                break;
        }
    }

    //注册广播
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取传递过来的信息
            String StringE = intent.getStringExtra("msg");
            //打印日志
            Constant.debugLog("msg====------>" + StringE);
            if (StringE != null && StringE.equals("")) {
                parseJson(StringE);
            }
        }
    }

    //解析数据
    public void parseJson(String str) {
        if (str.equals("robot")) {
            init();
        } else {
            Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消广播监听
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }
}
