package com.mkst.robot.push.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mkst.robot.push.R;
import com.mkst.robot.push.adapter.AreaAdapter;
import com.mkst.robot.push.adapter.DeskAdapter;
import com.mkst.robot.push.adapter.GridViewAdapter;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.dialog.MyDialog;
import com.mkst.robot.push.dialog.RobotDialog;
import com.mkst.robot.push.helper.RobotDBHelper;
import com.mkst.robot.push.service.ClientSocketUtil;
import com.mkst.robot.push.service.ServerSocketUtil;
import com.mkst.robot.push.service.SetStaticIPService;
import com.mkst.robot.push.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/7/27
 * 描述: 主页
 */
public class MainActivity extends Activity implements View.OnClickListener, Animation.AnimationListener {
    //数据库帮助类
    private RobotDBHelper robotDBHelper;
    private MyReceiver receiver;


    //Map集合，存储数据
    private static List<Map> areaList = new ArrayList<>();
    private static List<Map> deskList = new ArrayList<>();
    private static List<Map> robotList = new ArrayList<>();
    private static List<Map> commandList = new ArrayList<>();
    private static List<Map> robotData_list = new ArrayList<>();

    //Map集合，桌面数据存储
    private List<Map<String, Object>> areaData_list = new ArrayList<>();
    private List<Map<String, Object>> deskData_list = new ArrayList<>();

    //适配器
    private DeskAdapter desk_adapter;
    private AreaAdapter area_adapter;
    private GridViewAdapter gridViewAdapter;

    //线性布局
    private LinearLayout linearLayout_all;
    private LinearLayout linear_robot;
    private LinearLayout linear_desk;

    //相对布局
    private RelativeLayout map_right_relative;

    //定义控件
    private Button up, down, left, right, stop, shrink;//控制机器运动
    private ImageView imgViewMapRight;//导航栏左侧菜单
    private GridView deskView;// 桌面列表
    private GridView robotGirdView;//机器人列表
    private TextView area_text;
    private ListView area;

    //平移动画
    private TranslateAnimation translateAnimation;
    private float density;

    //是否编辑桌面
    public static boolean DeskIsEdit = false;
    public static boolean AreaIsEdit = false;
    private boolean IsRight = true;
    private boolean IsFinish = true;
    private boolean isShrink = false;

    //当前坐标
    public static int Current_INDEX = 1;
    public static int CURRENT_AREA_id = 0;


    /**
     * MainActivity 显示的入口
     *
     * @param context 上下文
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    /**
     * 初始化布局
     *
     * @return 返回一个布局文件
     */
    @Override
    protected int getContentLayoutId() {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_main;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initWidget() {
        super.initWidget();
        //静态Wifi服务
        Intent SetStaticIPService = new Intent(this, SetStaticIPService.class);
        startService(SetStaticIPService);
        //启动后台通讯服务  服务端
        Intent serverSocket = new Intent(this, ServerSocketUtil.class);
        startService(serverSocket);
/*        //启动后台通讯服务  客务端
        Intent clientSocket = new Intent(this, ClientSocketUtil.class);
        startService(clientSocket);*/

        //初始化数据库实例
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());

        //线性布局
        linearLayout_all = (LinearLayout) findViewById(R.id.linearlayout_all);

        //机器人
        linear_robot = (LinearLayout) findViewById(R.id.linear_robot);
        linear_robot.setOnClickListener(this);

        //桌面
        linear_desk = (LinearLayout) findViewById(R.id.linear_desk);
        linear_desk.setOnClickListener(this);

        //主页
        findViewById(R.id.main).setOnClickListener(this);
        findViewById(R.id.main).setOnClickListener(this);
        findViewById(R.id.activity_main).setOnClickListener(this);
        findViewById(R.id.shrink).setOnClickListener(this);

        //导航栏左侧菜单
        imgViewMapRight = (ImageView) findViewById(R.id.imgViewmapnRight);
        imgViewMapRight.setOnClickListener(this);

        //相对布局
        map_right_relative = (RelativeLayout) findViewById(R.id.map_right_Ralative);

        //ListView
        area = (ListView) findViewById(R.id.area);

        //区域编辑
        findViewById(R.id.config_redact).setOnClickListener(this);

        //桌面列表
        deskView = (GridView) findViewById(R.id.gview);
        area_text = (TextView) findViewById(R.id.area_text);


        //机器人控制按钮
        up = (Button) findViewById(R.id.up);
        up.setOnClickListener(this);
        down = (Button) findViewById(R.id.down);
        down.setOnClickListener(this);
        left = (Button) findViewById(R.id.left);
        left.setOnClickListener(this);
        right = (Button) findViewById(R.id.right);
        right.setOnClickListener(this);
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(this);
        shrink = (Button) findViewById(R.id.shrink);
        shrink.setOnClickListener(this);

        //机器人列表
        robotGirdView = (GridView) findViewById(R.id.robotgirdview);
        //机器人桌面列表点击事件
        robotGirdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!IsRight) {
                    startAnimationLeft();
                } else {
                    //向RobotActivity传递area
                    Intent intent = new Intent(MainActivity.this, RobotActivity.class);
                    intent.putExtra("id", (Integer) robotData_list.get(position).get("id"));
                    startActivity(intent);
                }
            }
        });

        //桌面适配器
        desk_adapter = new DeskAdapter(this, deskData_list);
        deskView.setAdapter(desk_adapter);
        //桌面列表点击事件
        deskView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!IsRight) {
                    startAnimationLeft();
                } else {
                    getAreaData();
                    if (areaList != null && areaList.size() > 0 && CURRENT_AREA_id != 0) {
                        if (DeskIsEdit) {
                            if (position == 0) {
                                //向DeskConfigPathActivity传递area
                                Intent intent = new Intent(MainActivity.this, DeskConfigPathActivity.class);
                                intent.putExtra("area", CURRENT_AREA_id);
                                startActivity(intent);
                            } else {
                                //向DeskConfigPathActivity传递Id     area
                                Intent intent = new Intent(MainActivity.this, DeskConfigPathActivity.class);
                                intent.putExtra("area", CURRENT_AREA_id);
                                intent.putExtra("id", (Integer) deskData_list.get(position).get("id"));
                                startActivity(intent);
                            }
                            getDeskData();
                        } else {
                            Constant.debugLog("position" + CURRENT_AREA_id);
                            commandList = robotDBHelper.queryListMap("select * from command where desk = '" + deskData_list.get(position).get("id") + "'", null);
                            if (commandList != null && commandList.size() > 0) {
                                robotDialog(commandList);
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请添加并选择区域", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //桌面适配器
        area_adapter = new AreaAdapter(this, areaData_list);
        area.setAdapter(area_adapter);
        area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getAreaData();
                if (AreaIsEdit) {
                    if (position == 0) {
                        AreaIsEdit = false;
                    } else if (position == 1) {
                        dialog();
                    } else {
                        dialog(areaData_list.get(position).get("name").toString(), (int) areaData_list.get(position).get("id"));
                    }
                    getAreaData();
                } else {
                    if (position == 0) {
                        AreaIsEdit = true;
                    } else {
                        if (!IsRight) {
                            startAnimationLeft();
                        }
                        DeskIsEdit = false;
                        CURRENT_AREA_id = (int) areaData_list.get(position).get("id");
                        Current_INDEX = position;
                        area_text.setText(areaData_list.get(position).get("name").toString());
                        getDeskData();
                    }
                    getAreaData();
                }
            }
        });

        robotDBHelper.execSQL("update robot set outline= '0' ");
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.jdrd.activity.Main");
        if (receiver != null) {
            this.registerReceiver(receiver, filter);
        }
    }

    //动态设置GridView属性
    private void setGridView() {
        int size = robotData_list.size();
        int length = 76;
        int height = 106;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;
        int gridViewWidth = (int) (size * (length + 30) * density);
        if (gridViewWidth <= 340 * density) {
            gridViewWidth = (int) (340 * density);
        }
        int itemWidth = (int) (length * density);
        int itemHeight = (int) (height * density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridViewWidth, itemHeight);
        Constant.linearWidth = (int) (76 * density);
        robotGirdView.setLayoutParams(params); // 重点
        robotGirdView.setColumnWidth(itemWidth); // 重点
        robotGirdView.setHorizontalSpacing((int) (8 * density)); // 间距
        robotGirdView.setStretchMode(GridView.NO_STRETCH);
        robotGirdView.setNumColumns(size); // 重点
        gridViewAdapter = new GridViewAdapter(getApplicationContext(),
                robotData_list);
        robotGirdView.setAdapter(gridViewAdapter);
    }

    /**
     * 按钮点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //主页不显示左侧菜单
            case R.id.linear_robot:
                if (!IsRight) {
                    startAnimationLeft();
                }
                break;
            //主页不显示左侧菜单
            case R.id.main:
                if (!IsRight) {
                    startAnimationLeft();
                }
                break;
            //点击左侧菜单栏显示
            case R.id.imgViewmapnRight:
                startAnimationLeft();
                break;
            //编辑桌面
            case R.id.config_redact:
                if (DeskIsEdit) {
                    DeskIsEdit = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        findViewById(R.id.config_redact).setBackground(getResources().getDrawable(R.animator.btn_direct_selector, null));
                    }
                } else {
                    DeskIsEdit = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        findViewById(R.id.config_redact).setBackground(getResources().getDrawable(R.animator.btn_exit_selector, null));
                    }
                }
                getDeskData();
                break;
            case R.id.robotgirdview:
                if (!IsRight) {
                    startAnimationLeft();
                }
                break;
            case R.id.linear_desk:
                if (!IsRight) {
                    startAnimationLeft();
                }
                break;
            case R.id.up:
                break;
            case R.id.down:
                break;
            case R.id.left:
                break;
            case R.id.right:
                break;
            case R.id.stop:
                break;
            case R.id.shrink:
                if (!IsRight) {
                    startAnimationLeft();
                }
                startAnimationShrink();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAreaData();
        if (CURRENT_AREA_id == 0) {
            if (areaList != null && areaList.size() > 0) {
                CURRENT_AREA_id = (int) areaList.get(0).get("id");
                Current_INDEX = 1;
                area_text.setText(areaList.get(0).get("name").toString());
            } else {
                area_text.setText("请选择左侧区域");
            }
        } else {
            for (int i = 0, size = areaList.size(); i < size; i++) {
                if (((int) areaList.get(i).get("id")) == CURRENT_AREA_id) {
                    area_text.setText(areaList.get(i).get("name").toString());
                    CURRENT_AREA_id = (int) areaList.get(i).get("id");
                    Current_INDEX = i + 1;
                }
            }
        }
        getDeskData();
        getRobotData();
        gridViewAdapter.notifyDataSetInvalidated();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    //获取桌面数据
    public List<Map<String, Object>> getDeskData() {
        deskData_list.clear();
        try {
            deskList = robotDBHelper.queryListMap("select * from desk where area = '" + CURRENT_AREA_id + "'", null);
            Log.e("Robot", deskList.toString());
            Log.e("Robot", "CURRENT_AREA_id" + CURRENT_AREA_id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> map;
        if (DeskIsEdit) {
            map = new HashMap<>();
            map.put("image", R.animator.btn_add_desk_selector);
            map.put("id", 0);
//            map.put("name",getString(R.string.config_add));
            deskData_list.add(map);
        }

        if (deskList != null && deskList.size() > 0) {
            for (int i = 0, size = deskList.size(); i < size; i++) {
                map = new HashMap<>();
                if (DeskIsEdit) {
//                    map.put("image", R.mipmap.ic_launcher);
                } else {
//                    map.put("image", R.mipmap.bg);
                }
                map.put("id", deskList.get(i).get("id"));
                map.put("name", deskList.get(i).get("name"));
                map.put("area", deskList.get(i).get("area"));
                deskData_list.add(map);
            }
        }
        desk_adapter.notifyDataSetChanged();
        return deskData_list;
    }

    //获取机器人数据
    public List<Map> getRobotData() {
        robotData_list.clear();
        try {
            robotList = robotDBHelper.queryListMap("select * from robot", null);
            Constant.debugLog("robotList" + robotList.toString());
            List<Map> robotData_listCache = new ArrayList<>();
            int j;
            boolean flag = false;
            for (int i = 0, size = robotList.size(); i < size; i++) {
                Constant.debugLog("size" + size + " ip" + robotList.get(i).get("ip").toString());
                String ip = robotList.get(i).get("ip").toString();
                j = 0;
                int h = ServerSocketUtil.socketList.size();
                while (j < h) {
                    if (ip.equals(ServerSocketUtil.socketList.get(j).get("ip"))) {
                        robotDBHelper.execSQL("update robot set outline= '1' where ip = '" + robotList.get(i).get("ip") + "'");
                        robotList.get(i).put("outline", 1);
                        robotData_listCache.add(robotList.get(i));
                        robotList.remove(i);
                        flag = true;
                        break;
                    }
                    j++;
                    h = ServerSocketUtil.socketList.size();
                }
                size = robotList.size();
                if (flag) {
                    i--;
                }
            }
            robotData_list.addAll(robotData_listCache);
            robotData_list.addAll(robotList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setGridView();
        return robotData_list;
    }

    //获取命令数据
    public List<Map<String, Object>> getAreaData() {
        areaData_list.clear();
        try {
            areaList = robotDBHelper.queryListMap("select * from area", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> map;
        map = new HashMap<>();
        map.put("image", R.mipmap.qybianji_no);
//        map.put("name",getString(R.string.config_redact));
        areaData_list.add(map);
        if (AreaIsEdit) {
            map = new HashMap<>();
            map.put("image", R.mipmap.add_area);
//            map.put("name",getString(R.string.config_add));
            areaData_list.add(map);
        }
        if (areaList != null && areaList.size() > 0) {
            for (int i = 0, size = areaList.size(); i < size; i++) {
                map = new HashMap<>();
                if (AreaIsEdit) {
//                    map.put("image", R.mipmap.ic_launcher);
                } else {
//                    map.put("image", R.mipmap.bg);
                }
                map.put("id", areaList.get(i).get("id"));
                map.put("name", areaList.get(i).get("name"));
                areaData_list.add(map);
            }
        }
        area_adapter.notifyDataSetChanged();
        return areaData_list;
    }

    //左侧菜单导航栏
    private void startAnimationLeft() {
        if (IsFinish) {
            IsFinish = false;
            if (IsRight) {
                linearLayout_all.setVisibility(View.VISIBLE);
                //第一个参数fromXDelta为动画起始时 X坐标上的移动位置
                //第二个参数toXDelta为动画结束时 X坐标上的移动位置
                //第三个参数fromYDelta为动画起始时Y坐标上的移动位置
                //第四个参数toYDelta为动画结束时Y坐标上的移动位置
                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, -Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0F
                );
                //设置一个动画的持续时间
                translateAnimation.setDuration(500);
                //设置动画是否停留在最后一帧，为true则是停留在最后一帧
                translateAnimation.setFillAfter(true);
                //给一个动画设置监听，设置类似侦听动画的开始或动画重复的通知
                translateAnimation.setAnimationListener(MainActivity.this);
                //启动动画
                map_right_relative.startAnimation(translateAnimation);

                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0F
                );
                translateAnimation.setDuration(500);
                translateAnimation.setFillAfter(true);
                linear_robot.startAnimation(translateAnimation);
                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0F
                );
                translateAnimation.setDuration(500);
                translateAnimation.setFillAfter(true);
                linear_desk.startAnimation(translateAnimation);
            } else {
                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, -Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f
                );
                translateAnimation.setDuration(500);
                translateAnimation.setFillAfter(true);
                translateAnimation.setAnimationListener(MainActivity.this);
                map_right_relative.startAnimation(translateAnimation);

                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f
                );
                translateAnimation.setDuration(500);
                translateAnimation.setFillAfter(true);
                linear_robot.startAnimation(translateAnimation);
                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f
                );
                translateAnimation.setDuration(500);
                translateAnimation.setFillAfter(true);
                linear_desk.startAnimation(translateAnimation);
            }
        }
    }

    //开启动画
    @Override
    public void onAnimationStart(Animation animation) {

    }

    //关闭动画
    @Override
    public void onAnimationEnd(Animation animation) {
        map_right_relative.clearAnimation();
        if (IsRight) {
            IsRight = false;
        } else {
            IsRight = true;
            linearLayout_all.setVisibility(View.GONE);
        }
        IsFinish = true;
    }

    //重置
    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    //执行命令按钮动画
    @SuppressWarnings("deprecation")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startAnimationShrink() {
        Animation translate;
        if (isShrink) {
            findViewById(R.id.shrink).setBackground(getResources().getDrawable(R.animator.btn_shrink_selector));
            //左
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_in_left);
            translate.setAnimationListener(animationListener);
            left.startAnimation(translate);
            //右
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_in_right);
            translate.setAnimationListener(animationListener);
            right.startAnimation(translate);
            //上
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_in_up);
            translate.setAnimationListener(animationListener);
            up.startAnimation(translate);
            //下
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_in_down);
            translate.setAnimationListener(animationListener);
            down.startAnimation(translate);
            //停止
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_in_stop);
            translate.setAnimationListener(animationListener);
            stop.startAnimation(translate);
        } else {
            findViewById(R.id.shrink).setBackground(getResources().getDrawable(R.animator.btn_shrink_out_selector));
            left.setVisibility(View.VISIBLE);
            down.setVisibility(View.VISIBLE);
            up.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_out_left);
            translate.setAnimationListener(animationListener);
            left.startAnimation(translate);
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_out_right);
            translate.setAnimationListener(animationListener);
            right.startAnimation(translate);
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_out_up);
            translate.setAnimationListener(animationListener);
            up.startAnimation(translate);
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_out_down);
            translate.setAnimationListener(animationListener);
            down.startAnimation(translate);
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_out_stop);
            translate.setAnimationListener(animationListener);
            stop.startAnimation(translate);
        }
    }

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (isShrink) {
                isShrink = false;
                up.setVisibility(View.GONE);
                down.setVisibility(View.GONE);
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
            } else {
                isShrink = true;
                left.setVisibility(View.VISIBLE);
                down.setVisibility(View.VISIBLE);
                up.setVisibility(View.VISIBLE);
                right.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };


    private MyDialog dialog;
    private EditText editText;
    private TextView title;

    private void dialog() {
        dialog = new MyDialog(this);
        editText = (EditText) dialog.getEditText();
        //确定
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "区域名称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    robotDBHelper.insert("area", new String[]{"name"}, new Object[]{editText.getText().toString()});
                    getAreaData();
                    dialog.dismiss();
                }
            }
        });
        //取消
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //显示窗口
        dialog.show();
    }

    private void dialog(String name, final int id) {
        dialog = new MyDialog(this);
        //获取文本框内容
        editText = (EditText) dialog.getEditText();
        editText.setText(name);
        //给Title赋值
        title = (TextView) dialog.getTitle();
        title.setText(R.string.change_area);
        //确定
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "区域名称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    robotDBHelper.execSQL("update area set name= '" + editText.getText().toString().trim() + "' where id= '" + id + "'");
                    dialog.dismiss();
                }
            }
        });
        //取消
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                robotDBHelper.execSQL("delete from area where id= '" + id + "'");
                List<Map> deskList;
                deskList = robotDBHelper.queryListMap("select * from desk where area = '" + id + "'", null);
                for (int i = 0, size = deskList.size(); i < size; i++) {
                    robotDBHelper.execSQL("delete from command where desk= '" + deskList.get(i).get("id") + "'");
                }
                robotDBHelper.execSQL("delete from desk where area= '" + id + "'");
                getAreaData();
                if (areaList != null && areaList.size() > 0) {
                    CURRENT_AREA_id = (int) areaList.get(0).get("id");
                    Current_INDEX = 1;
                    area_text.setText(areaList.get(0).get("name").toString());
                } else {
                    area_text.setText("请选择左侧区域");
                }
                getDeskData();
                //关闭窗口
                dialog.dismiss();
            }
        });
        ((Button) dialog.getNegative()).setText(R.string.btn_delete);
        dialog.show();
    }

    //自定义机器人运行轨迹对话框
    private RobotDialog robotDialog;

    //机器人单个窗口
    private void robotDialog(String str) {
        robotDialog = new RobotDialog(this, str);
        robotDialog.show();
    }

    //机器人多个窗口
    private void robotDialog(List<Map> list) {
        robotDialog = new RobotDialog(this, list);
        robotDialog.show();
    }


    //广播监听消息
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String StringE = intent.getStringExtra("msg");
            Constant.debugLog("msg" + StringE);
            if (StringE != null && !StringE.equals("")) {
                paresJson(StringE);
            }
        }
    }

    //解析数据
    public void paresJson(String string) {
        if (string.equals("robot_connect") || string.equals("robot_unconnect")) {
            getRobotData();
            gridViewAdapter.notifyDataSetInvalidated();
            Toast.makeText(getApplicationContext(), "连接 断开", Toast.LENGTH_SHORT).show();
        } else if (string.equals("robot_receive_succus")) {
            Toast.makeText(getApplicationContext(), "收到成功指令", Toast.LENGTH_SHORT).show();
            synchronized (RobotDialog.thread) {
                RobotDialog.thread.notify();
            }
        } else if (string.equals("robot_receive_fail")) {
            Toast.makeText(getApplicationContext(), "收到失败指令", Toast.LENGTH_SHORT).show();
            if (RobotDialog.flag) {
                RobotDialog.sendCommandList();
            } else {
                RobotDialog.sendCommand();
            }
        } else if (string.equals("robot_destory")) {
            robotDBHelper.execSQL("update robot set outline= '0' ");
        } else {
        }
    }


    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }


}
