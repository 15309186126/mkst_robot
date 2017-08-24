package com.example.tcptest.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tcptest.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpClientActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TcpClientActivity";
    public static Context context;
    private Button btnStartClient;
    private Button btnCloseClient;
    private Button btnCleanClientSend;
    private Button btnCleanClientRcv;
    private Button btnClientSend;
    private TextView txtRcv;
    private TextView txtSend;
    private EditText editClientSend;
    private EditText editClientPort;
    private EditText editClientIp;
    private static TcpClient tcpClient = null;
    private final MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    ExecutorService exec = Executors.newCachedThreadPool();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_client);
        context = this;
        bindID();
        bindListener();
        bindReceiver();
        init();
    }

    private void bindID() {
        btnStartClient = (Button) findViewById(R.id.btn_tcpClientConn);
        btnCloseClient = (Button) findViewById(R.id.btn_tcpClientClose);
        btnCleanClientRcv = (Button) findViewById(R.id.btn_tcpCleanClientRec);
        btnCleanClientSend = (Button) findViewById(R.id.btn_tcpCleanClientSend);
        btnClientSend = (Button) findViewById(R.id.btn_tcpClientSend);
        editClientPort = (EditText) findViewById(R.id.edit_tcpClientPort);
        editClientIp = (EditText) findViewById(R.id.edit_tcpClientIp);
        editClientSend = (EditText) findViewById(R.id.edit_tcpClientSend);
        txtRcv = (TextView) findViewById(R.id.txt_ClientRcv);
        txtSend = (TextView) findViewById(R.id.txt_ClientSend);
    }

    private void bindListener() {
        btnStartClient.setOnClickListener(this);
        btnCloseClient.setOnClickListener(this);
        btnCleanClientRcv.setOnClickListener(this);
        btnCleanClientSend.setOnClickListener(this);
        btnClientSend.setOnClickListener(this);
    }

    private void bindReceiver() {
        IntentFilter intentFilter = new IntentFilter("tcpClientReceiver");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void init() {
        btnCloseClient.setEnabled(false);
        btnClientSend.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_tcpClientConn:
                Log.i(TAG, "onClick: 开始");
                btnStartClient.setEnabled(false);
                btnCloseClient.setEnabled(true);
                btnClientSend.setEnabled(true);
                tcpClient = new TcpClient(editClientIp.getText().toString(), getPort(editClientPort.getText().toString()));
                exec.execute(tcpClient);
                break;
            case R.id.btn_tcpClientClose:
                tcpClient.closeSelf();
                btnStartClient.setEnabled(true);
                btnCloseClient.setEnabled(false);
                btnClientSend.setEnabled(false);
                break;
            case R.id.btn_tcpCleanClientRec:
                txtRcv.setText("");
                break;
            case R.id.btn_tcpCleanClientSend:
                txtSend.setText("");
                break;
            case R.id.btn_tcpClientSend:
                Message message = Message.obtain();
                message.what = 2;
                message.obj = editClientSend.getText().toString();
                myHandler.sendMessage(message);
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        tcpClient.send(editClientSend.getText().toString());
                    }
                });
                break;
        }
    }

    private int getPort(String msg) {
        if (msg.equals("")) {
            msg = "8899";
        }
        return Integer.parseInt(msg);
    }

    private class MyHandler extends Handler {
        private WeakReference<TcpClientActivity> mActivity;

        MyHandler(TcpClientActivity activity) {
            this.mActivity = new WeakReference<TcpClientActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity != null) {
                switch (msg.what) {
                    case 1:
                        txtRcv.append(msg.obj.toString() + "\n");
                        break;
                    case 2:
                        txtSend.append(msg.obj.toString() + "\n");
                        break;
                }
            }
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            switch (mAction) {
                case "tcpClientReceiver":
                    String msg = intent.getStringExtra("tcpClientReceiver");
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = msg;
                    myHandler.sendMessage(message);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }
}
