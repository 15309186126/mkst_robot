package com.example.tcptest.server;

import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/24
 * 描述: 服务端
 */

public class TcpServer implements Runnable {

    private static final String TAG = "TcpServer";
    private int port = 8899;
    private boolean isListen = true;// 线程监听标志位
    public ArrayList<ServerSocketThread> SST = new ArrayList<ServerSocketThread>();

    //构造方法
    public TcpServer(int port) {
        this.port = port;
    }

    //更改监听标志位
    public void setIsListen(boolean b) {
        isListen = b;
    }

    private Socket getSocket(ServerSocket serverSocket) {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "run:监听超时");
            return null;
        }
    }

    //关闭
    public void closeSelf() {
        isListen = false;
        for (ServerSocketThread s : SST) {
            s.isRun = false;
        }
        SST.clear();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(5000);
            while (isListen) {
                Log.i(TAG, "run: 开始监听...");

                Socket socket = getSocket(serverSocket);
                if (socket != null) {
                    new ServerSocketThread(socket);
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //线程
    public class ServerSocketThread extends Thread {
        Socket socket = null;
        private PrintWriter pw;
        private InputStream is = null;
        private OutputStream os = null;
        private String ip = null;
        private boolean isRun = true;


        ServerSocketThread(Socket socket) {
            this.socket = socket;
            //获取本机Ip
            ip = socket.getInetAddress().toString();
            //打印Log
            Log.i(TAG, "ServerSocketThread: 检测到新的客户端连入,IP:" + ip);

            try {
                socket.setSoTimeout(5000);
                os = socket.getOutputStream();
                is = socket.getInputStream();
                pw = new PrintWriter(os, true);
                //启动
                start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(String msg) {
            pw.println(msg);
            pw.flush();//强制送出数据
        }

        @Override
        public void run() {
            byte buff[] = new byte[4096];
            String rcvMsg;
            int rcvLen;
            SST.add(this);
            while (isRun && !socket.isClosed() && !socket.isInputShutdown()) {
                try {
                    if ((rcvLen = is.read(buff)) != -1) {
                        rcvMsg = new String(buff, 0, rcvLen, "utf-8");
                        //打印log
                        Log.i(TAG, "run: 收到消息:" + rcvMsg);
                        Intent intent = new Intent();
                        intent.setAction("tcpServerReceiver");
                        intent.putExtra("tcpServerReceiver", rcvMsg);
                        TcpServerActivity.context.sendBroadcast(intent);
                        if (rcvMsg.equals("QuitServer")) {
                            isRun = false;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                socket.close();
                SST.clear();
                Log.i(TAG, "run: 断开连接");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
