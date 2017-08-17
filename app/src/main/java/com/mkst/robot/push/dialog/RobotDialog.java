package com.mkst.robot.push.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.mkst.robot.push.R;
import com.mkst.robot.push.activitys.MainActivity;
import com.mkst.robot.push.helper.RobotDBHelper;
import com.mkst.robot.push.service.ServerSocketUtil;
import com.mkst.robot.push.utils.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/8
 * 描述: 自定义机器人运行轨迹对话框
 */

public class RobotDialog extends Dialog {

    private Context context;
    private static String sendStr;
    private static String IP;
    public static boolean flag;
    public static Thread thread = new Thread();

    private static List<Map> list;
    private static List<Map> robotList;

    private GridView gridView;
    private SimpleAdapter robotAdapter;
    private static RobotDBHelper robotDBHelper;
    private List<Map<String, Object>> robotData_list = new ArrayList<>();
    private final String[] from = {"image", "text", "name", "imageback"};
    private final int[] to = {R.id.imageview, R.id.text, R.id.name, R.id.imageback};


    public RobotDialog(Context context, String str) {
        super(context, R.style.SoundRecorder);
        setCustomDialog();
        this.context = context;
        this.sendStr = str;
        flag = false;
    }

    public RobotDialog(Context context, List<Map> robotList) {
        super(context, R.style.SoundRecorder);
        setCustomDialog();
        this.context = context;
        this.robotList = robotList;
        flag = true;

    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_robot_dialog, null);
        gridView = (GridView) mView.findViewById(R.id.robot_girdview);
        list = new ArrayList<>();
        robotDBHelper = RobotDBHelper.getInstance(context);
        try {
            list = robotDBHelper.queryListMap("select * from robot where area = '" + MainActivity.CURRENT_AREA_id + "' and outline = '1'", null);
            Log.e("Robot", list.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            int i = 0;
            int j = list.size();
            Map<String, Object> map;
            while (i < j) {
                map = new HashMap<>();
                map.put("image", R.mipmap.zaixian);
                map.put("name", list.get(i).get("name"));
                map.put("ip", list.get(i).get("ip"));
                switch ((int) list.get(i).get("robotstate")) {
                    case 0:
                        map.put("text", "空闲");
                        map.put("imageback", R.mipmap.kongxian);
                        break;
                    case 1:
                        map.put("text", "送餐");
                        map.put("imageback", R.mipmap.fuwuzhong);
                        break;
                    case 2:
                        map.put("text", "故障");
                        map.put("imageback", R.mipmap.guzhang);
                        break;
                }
                robotData_list.add(map);
                i++;
            }
            Constant.debugLog(robotData_list.toString());
            robotAdapter = new SimpleAdapter(getContext(), robotData_list, R.layout.robot_grid_item, from, to);
            gridView.setAdapter(robotAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    IP = robotData_list.get(position).get("ip").toString();
                    if (flag) {
                        sendCommandList();
                        dismiss();
                    } else {
                        sendCommand();
                        dismiss();
                    }
                }
            });
        }
        super.setContentView(mView);
    }

    //发送命令
    public static void sendCommand() {
        for (Map map : ServerSocketUtil.socketList) {
            if (map.get("ip").equals(IP)) {
                final OutputStream out = (OutputStream) map.get("out");
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (out != null) {
                            try {
                                out.write(sendStr.getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                thread.start();
            }
        }
    }

    public static void sendCommandList() {
        Constant.debugLog(robotList.toString());
        for (Map map : ServerSocketUtil.socketList) {
            if (map.get("ip").equals(IP)) {
                final OutputStream out = (OutputStream) map.get("out");
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (out != null) {
                            try {
                                out.write("*s+6+#".getBytes());
                                synchronized (thread) {
                                    thread.wait();
                                }
                                for (int i = 0, size = robotList.size(); i < size; i++) {
                                    switch ((int) robotList.get(i).get("type")) {
                                        case 0:
                                            List<Map> card_list = robotDBHelper.queryListMap("select * from card where id = '" + robotList.get(i).get("goal") + "'", null);
                                            if (card_list != null && card_list.size() > 0) {
                                                sendStr = "*g+" + card_list.get(0).get("address") + "+" + robotList.get(i).get("direction") + "+" + robotList.get(i).get("speed")
                                                        + "+" + robotList.get(i).get("music") + "+" + robotList.get(i).get("outime") + "+"
                                                        + robotList.get(i).get("shownumber") + "+" + robotList.get(i).get("showcolor");
                                            }
                                            setSendStr(out, sendStr);
                                            synchronized (thread) {
                                                thread.wait();
                                            }
                                            break;
                                        case 1:
                                            sendStr = "*d+" + robotList.get(i).get("speed")
                                                    + "+" + robotList.get(i).get("music") + "+" + robotList.get(i).get("outime") + "+"
                                                    + robotList.get(i).get("shownumber") + "+" + robotList.get(i).get("showcolor");
                                            setSendStr(out, sendStr);
                                            synchronized (thread) {
                                                thread.wait();
                                            }
                                            break;
                                        case 2:
                                            sendStr = "*r+" + robotList.get(i).get("speed")
                                                    + "+" + robotList.get(i).get("music") + "+" + robotList.get(i).get("outime") + "+"
                                                    + robotList.get(i).get("shownumber") + "+" + robotList.get(i).get("showcolor");
                                            setSendStr(out, sendStr);
                                            synchronized (thread) {
                                                thread.wait();
                                            }
                                            break;
                                        case 3:
                                            sendStr = "*w+" + robotList.get(i).get("music") + "+" + robotList.get(i).get("outime") + "+"
                                                    + robotList.get(i).get("shownumber") + "+" + robotList.get(i).get("showcolor");
                                            setSendStr(out, sendStr);
                                            synchronized (thread) {
                                                thread.wait();
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                thread.start();
            }
        }
    }


    //发送命令格式
    private static void setSendStr(OutputStream out, String str) {
        if (str.length() >= 6) {
            str = str + "+" + (str.length() + 5) + "+#";
        } else {
            str = str + "+" + (str.length() + 4) + "+#";
        }
        try {
            out.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
