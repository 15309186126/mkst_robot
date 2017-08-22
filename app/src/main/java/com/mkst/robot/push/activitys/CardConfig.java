package com.mkst.robot.push.activitys;

import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mkst.robot.push.R;
import com.mkst.robot.push.adapter.CardAdapter;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.helper.RobotDBHelper;
import com.mkst.robot.push.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/17
 * 描述:系统卡列表
 */

public class CardConfig extends Activity implements View.OnClickListener {

    private CardAdapter myAdapter;//系统卡适配器
    private ListView cardList;//卡列表
    private RobotDBHelper robotDBHelper;//数据库帮助类
    private List<Map> card_list = new ArrayList<>();//存储系统卡

    @Override
    protected int getContentLayoutId() {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_card;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initWidget() {
        //初始化数据库
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());

        findViewById(R.id.setting_back).setOnClickListener(this);
        findViewById(R.id.add_card).setOnClickListener(this);
        cardList = (ListView) findViewById(R.id.cardlist);
        //子列表点击事件
        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //向CardConfigActivity传递数据
                Intent intent = new Intent(CardConfig.this, CardConfigActivity.class);
                intent.putExtra("id", (Integer) card_list.get(position).get("id"));
                //打印Log日志
                Constant.debugLog("id" + card_list.get(position).get("id").toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //查询系统卡
        card_list = robotDBHelper.queryListMap("select * from card ", null);
        //加载适配器
        myAdapter = new CardAdapter(this, card_list);
        cardList.setAdapter(myAdapter);
    }

    /**
     * 按钮点击事件
     *
     * @param v 获取按钮id
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_back:
                finish();
                break;
            case R.id.add_card:
                Intent intent = new Intent(CardConfig.this, CardConfigActivity.class);
                intent.putExtra("id", 0);
                startActivity(intent);
                break;
        }
    }
}
