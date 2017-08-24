package com.example.tcptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.tcptest.client.TcpClientActivity;
import com.example.tcptest.server.TcpServerActivity;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private RadioButton radio_Server;
    private RadioButton radio_Client;
    private Button btn_FunctionEnsure;
    private TextView txt_ShowFunction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindID();
        bindListener();
    }

    //初始化控件
    private void bindID() {
        radio_Server = (RadioButton) findViewById(R.id.radio_Server);
        radio_Client = (RadioButton) findViewById(R.id.radio_Client);
        btn_FunctionEnsure = (Button) findViewById(R.id.btn_FunctionEnsure);
        txt_ShowFunction = (TextView) findViewById(R.id.txt_ShowFunction);
    }

    //监听
    private void bindListener() {
        radio_Server.setOnCheckedChangeListener(this);
        radio_Client.setOnCheckedChangeListener(this);
        btn_FunctionEnsure.setOnClickListener(this);
    }

    //单选按钮选在事件
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.radio_Server:
                if(b){
                    txt_ShowFunction.setText("你选则的功能是：服务器");
                }
                break;
            case R.id.radio_Client:
                if(b){
                    txt_ShowFunction.setText("你选则的功能是：客户端");
                }
                break;
        }
    }

    //点击按钮
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_FunctionEnsure:
                Intent intent = new Intent();
                //跳转到服务端页面
                if (radio_Server.isChecked()) {
                    intent.setClass(MainActivity.this, TcpServerActivity.class);
                    startActivity(intent);
                }
                //跳转到客户端页面
                if (radio_Client.isChecked()) {
                    intent.setClass(MainActivity.this, TcpClientActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }
}
