package com.mkst.robot.push.activitys;

import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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
 * 描述: 系统卡列表
 */

public class AreaConfig extends Activity implements View.OnClickListener {

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

    /**
     * 初始化控件
     */
    @Override
    protected void initWidget() {
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        //获取RobotConfigActivity传递过来的Id
        Intent intent = getIntent();
        robotId = intent.getIntExtra("id", 0);

        //查询机器人列表
        List<Map> robotList = robotDBHelper.queryListMap("select * from robot where id = '" + robotId + "'", null);
        robotConfig = robotList.get(0);

        findViewById(R.id.setting_back).setOnClickListener(this);
        findViewById(R.id.add_card).setOnClickListener(this);
        areaList = (ListView) findViewById(R.id.cardlist);
        //子列点击事件
        areaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                robotDBHelper.execSQL("update robot set area= '" + area_list.get(position).get("id") + "'  where id= '" + robotId + "'");
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //查询区域
        area_list = robotDBHelper.queryListMap("select * from area ", null);

        if (area_list != null && area_list.size() > 0 && 0 != (int) robotConfig.get("area")) {
            for (int i = 0, size = area_list.size(); i < size; i++) {
                if (area_list.get(i).get("id") == robotConfig.get("area")) {
                    Current_position = i;
                    areaList.setSelection(i);
                }
            }
        }
        //显示到List列表上
        myAdapter = new AreaConfigAdapter(this, area_list);
        areaList.setAdapter(myAdapter);
    }

    /**
     * 点击事件
     *
     * @param v 获取按钮的Id
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.setting_back:
                finish();
                break;
            //确定
            case R.id.add_card:
                //向CardConfigActivity传递数据
                Intent intent = new Intent(AreaConfig.this, CardConfigActivity.class);
                intent.putExtra("id", 0);
                startActivity(intent);
                break;
        }
    }


}
