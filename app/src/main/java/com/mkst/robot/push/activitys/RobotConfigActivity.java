package com.mkst.robot.push.activitys;

import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mkst.robot.push.R;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.helper.RobotDBHelper;
import com.mkst.robot.push.utils.Constant;

import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/17
 * 描述: 机器人编辑页面
 */

public class RobotConfigActivity extends Activity implements View.OnClickListener {

    private RobotDBHelper robotDBHelper;//数据库帮助类
    private static int robotId;//机器人id
    private Map robotConfig;
    private EditText name;
    private TextView area_text;
    public int areaId;//区域id
    private List<Map> areaList;


    @Override
    protected int getContentLayoutId() {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_robot_config;
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initWidget() {
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        name = ((EditText) findViewById(R.id.name));
        findViewById(R.id.sure).setOnClickListener(this);
        //返回
        findViewById(R.id.setting_back).setOnClickListener(this);
        area_text = (TextView) findViewById(R.id.area_text);
        findViewById(R.id.area).setOnClickListener(this);
        //获取RobotActivity传递的Id
        Intent intent = getIntent();
        robotId = intent.getIntExtra("id", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //查询机器人
        List<Map> robotList = robotDBHelper.queryListMap("select * from robot where id = '" + robotId + "'", null);
        robotConfig = robotList.get(0);
        name.setHint(robotConfig.get("name").toString());
        areaList = robotDBHelper.queryListMap("select * from area ", null);
        areaId = (int) robotConfig.get("area");
        area_text.setText("未选择区域");
        if (areaList != null && areaList.size() > 0) {
            if (areaId != 0) {
                for (int i = 0, size = areaList.size(); i < size; i++) {
                    if (areaList.get(i).get("id").equals(areaId)) {
                        area_text.setText(areaList.get(i).get("name").toString());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //确定
            case R.id.sure:
                Constant.debugLog("areaId====------>" + areaId);
                if (!name.getText().toString().trim().equals("")) {
                    robotDBHelper.execSQL("update robot set name= '" + name.getText().toString().trim() + "'  where id= '" + robotId + "'");
                } else {
                    robotDBHelper.execSQL("update robot set name= '" + robotConfig.get("name").toString() + "'  where id= '" + robotId + "'");
                }
                finish();
                break;
            //返回
            case R.id.setting_back:
                finish();
                break;
            //跳转到区域
            case R.id.area:
                //向AreaConfig传递数据
                Intent intent = new Intent(RobotConfigActivity.this, AreaConfig.class);
                intent.putExtra("id", robotId);
                startActivity(intent);
                break;
        }
    }
}
