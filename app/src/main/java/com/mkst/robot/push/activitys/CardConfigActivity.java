package com.mkst.robot.push.activitys;

import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.mkst.robot.push.R;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.dialog.DeleteDialog;
import com.mkst.robot.push.helper.RobotDBHelper;
import com.mkst.robot.push.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/17
 * 描述: 系统卡编辑页面
 */

public class CardConfigActivity extends Activity implements View.OnClickListener {

    private RobotDBHelper robotDBHelper;//数据库帮助类
    private int card_id;//编号
    private EditText cardName, cardAddress;//卡名称和地址
    private Button btn_sure, btn_delete;//确定和删除
    private List<Map> card_list = new ArrayList<>();//存储系统卡列表


    @Override
    protected int getContentLayoutId() {
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_cardactivity;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initWidget() {
        //初始化数据库
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        //获取CardConfig传递过来的数据
        Intent intent = getIntent();
        card_id = intent.getIntExtra("id", 0);

        //卡名称
        cardName = (EditText) findViewById(R.id.cardname);
        //卡编码
        cardAddress = (EditText) findViewById(R.id.cardaddress);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(this);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);
        findViewById(R.id.setting_back).setOnClickListener(this);

        //打印Log
        Constant.debugLog("card_id=====>" + card_id);

        if (card_id == 0) {
            //判断删除按钮是否显示
            btn_delete.setVisibility(View.GONE);
        } else {
            //查询卡列表
            card_list = robotDBHelper.queryListMap("select * from card where id = '" + card_id + "'", null);
            //打印Log
            Constant.debugLog("card_list" + card_list.toString());

            cardName.setText(card_list.get(0).get("name").toString());
            cardAddress.setText(card_list.get(0).get("address").toString());
        }
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
            case R.id.btn_delete:
                dialog();
                break;
            case R.id.btn_sure:
                if (card_id == 0) {
                    robotDBHelper.insert("card", new String[]{"name", "address"}, new Object[]{cardName.getText().toString(), cardAddress.getText().toString().trim()});
                } else {
                    robotDBHelper.execSQL("update card set name= '" + cardName.getText().toString().trim() + "',address = '" + cardAddress.getText().toString().trim() + "'  where id= '" + card_id + "'");
                }
                finish();
                break;
        }
    }

    //删除的Dialog
    private DeleteDialog dialog;

    private void dialog() {
        dialog = new DeleteDialog(this);
        //确定
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                robotDBHelper.execSQL("delete from card where id= '" + card_id + "'");
                finish();
            }
        });
        //取消
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
