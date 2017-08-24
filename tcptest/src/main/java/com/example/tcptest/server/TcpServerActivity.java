package com.example.tcptest.server;

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
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TcpServerActivity";

    private Button btn_tcpServerConn;//连接
    private Button btn_tcpServerClose;//关闭
    private Button btn_tcpCleanServerRec;//清空接收区
    private Button btn_tcpCleanServerSend;//清空发送区
    private Button btn_tcpServerSend;//发送

    private TextView txt_Server_Ip;//本机IP地址
    private TextView txt_ServerRcv;//接收区
    private TextView txt_ServerSend;//发送区

    private EditText edit_Server_Port;//端口
    private EditText edit_tcpServerSend;//输入框

    private static TcpServer tcpServer = null;
    public static Context context;
    //更新ui
    private final MyHandler myHandler = new MyHandler(this);
    //广播
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    //线程池
    ExecutorService exec = Executors.newCachedThreadPool();

    //开启线程更新ui
    private class MyHandler extends Handler {
        private final WeakReference<TcpServerActivity> mActivity;

        public MyHandler(TcpServerActivity activity) {
            mActivity = new WeakReference<TcpServerActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            TcpServerActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        txt_ServerRcv.append(msg.obj.toString()+"\n");
                        break;
                    case 2:
                        txt_ServerSend.append(msg.obj.toString()+"\n");
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_server);
        context = this;
        bindID();
        bindListener();
        bindReceiver();
        ini();
    }


    //初始化控件
    private void bindID() {
        btn_tcpServerConn = (Button) findViewById(R.id.btn_tcpServerConn);
        btn_tcpServerClose = (Button) findViewById(R.id.btn_tcpServerClose);
        btn_tcpCleanServerRec = (Button) findViewById(R.id.btn_tcpCleanServerRec);
        btn_tcpCleanServerSend = (Button) findViewById(R.id.btn_tcpCleanServerSend);
        btn_tcpServerSend = (Button) findViewById(R.id.btn_tcpServerSend);
        txt_Server_Ip = (TextView) findViewById(R.id.txt_Server_Ip);
        txt_ServerRcv = (TextView) findViewById(R.id.txt_ServerRcv);
        txt_ServerSend = (TextView) findViewById(R.id.txt_ServerSend);
        edit_Server_Port = (EditText) findViewById(R.id.edit_Server_Port);
        edit_tcpServerSend = (EditText) findViewById(R.id.edit_tcpServerSend);
    }

    //初始化监听
    private void bindListener() {
        btn_tcpServerConn.setOnClickListener(this);
        btn_tcpServerClose.setOnClickListener(this);
        btn_tcpCleanServerRec.setOnClickListener(this);
        btn_tcpCleanServerSend.setOnClickListener(this);
        btn_tcpServerSend.setOnClickListener(this);
    }

    //绑定广播
    private void bindReceiver() {
        IntentFilter intent = new IntentFilter("tcpServerReceiver");
        registerReceiver(myBroadcastReceiver, intent);
    }

    //初始化数据
    private void ini() {
        btn_tcpServerClose.setEnabled(false);
        btn_tcpServerSend.setEnabled(false);
        txt_Server_Ip.setText(getHostIP());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_tcpServerConn:
                Log.i(TAG, "onClick: 开始");
                btn_tcpServerConn.setEnabled(false);
                btn_tcpServerClose.setEnabled(true);
                btn_tcpServerSend.setEnabled(true);
                //获取端口号
                tcpServer = new TcpServer(getHost(edit_Server_Port.getText().toString()));
                exec.execute(tcpServer);
                break;
            case R.id.btn_tcpServerClose:
                tcpServer.closeSelf();
                btn_tcpServerConn.setEnabled(true);
                btn_tcpServerClose.setEnabled(false);
                btn_tcpServerSend.setEnabled(false);
                break;
            case R.id.btn_tcpCleanServerRec:
                txt_ServerRcv.setText("");
                break;
            case R.id.btn_tcpCleanServerSend:
                txt_ServerSend.setText("");
                break;
            case R.id.btn_tcpServerSend:
                Message message = Message.obtain();
                message.what = 2;
                message.obj = edit_tcpServerSend.getText().toString();
                myHandler.sendMessage(message);
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        tcpServer.SST.get(0).send(edit_tcpServerSend.getText().toString());
                    }
                });
                break;
        }
    }


    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            switch (mAction) {
                case "tcpServerReceiver":
                    String msg = intent.getStringExtra("tcpServerReceiver");
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = msg;
                    myHandler.sendMessage(message);
                    break;
            }
        }
    }

    private int getHost(String msg) {
        if (msg.equals("")) {
            msg = "8899";
        }
        return Integer.parseInt(msg);
    }


    /**
     * 获取ip地址
     *
     * @return
     */
    public String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            Log.i("TcpServerActivity", "SocketException ");
        }
        return hostIp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }
}
