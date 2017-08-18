package com.mkst.robot.push.activitys;

import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mkst.robot.push.R;
import com.mkst.robot.push.adapter.SpinnerAdapter;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.dialog.DeleteDialog;
import com.mkst.robot.push.dialog.MyDialog;
import com.mkst.robot.push.dialog.SpinnerDialog;
import com.mkst.robot.push.helper.RobotDBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/8
 * 描述:
 */

public class CommandActivity extends Activity implements View.OnClickListener {
    private RobotDBHelper robotDBHelper;
    private int command_id;
    private Map commandConfig;
    private List<Map> goalList;
    private List<Map> list;
    private TextView speed, mp3, outTime, showNum, showColor;
    private TextView goal, direction;
    public static int goalNum = -1, directionNum = -1;

    @Override
    protected int getContentLayoutId() {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_command_config;
    }

    @Override
    protected void initWidget() {
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        //传递参数
        Intent intent = getIntent();
        command_id = intent.getIntExtra("id", 0);
        Log.e("commandList", command_id + "");
        //初始化控件
        goal = (TextView) findViewById(R.id.goal);
        direction = (TextView) findViewById(R.id.direction);
        goal.setOnClickListener(this);
        direction.setOnClickListener(this);
        speed = (TextView) findViewById(R.id.speed);
        speed.setOnClickListener(this);
        mp3 = (TextView) findViewById(R.id.mp3);
        mp3.setOnClickListener(this);
        outTime = (TextView) findViewById(R.id.outime);
        outTime.setOnClickListener(this);
        showNum = (TextView) findViewById(R.id.shownum);
        showNum.setOnClickListener(this);
        showColor = (TextView) findViewById(R.id.showcolor);
        showColor.setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.setting_back).setOnClickListener(this);

        //查询Id卡
        goalList = robotDBHelper.queryListMap("select * from card ", null);

        list = new ArrayList<>();
        Map<String, Object> map;
        map = new HashMap<>();
        map.put("name", "直行");
        list.add(map);
        map = new HashMap<>();
        map.put("name", "左岔道");
        list.add(map);
        map = new HashMap<>();
        map.put("name", "右岔道");
        list.add(map);

        //查询执行命令
        List<Map> commandlist = robotDBHelper.queryListMap("select * from command where id = '" + command_id + "'", null);
        if (commandlist != null && commandlist.size() > 0) {
            commandConfig = commandlist.get(0);
            if (commandConfig.get("goal") != null) {
                if (goalList != null && goalList.size() > 0) {
                    boolean flag = false;
                    for (int i = 0, size = goalList.size(); i < size; i++) {
                        if (goalList.get(i).get("id").equals(commandConfig.get("goal"))) {
                            goal.setText(goalList.get(i).get("name").toString());
                            goalNum = i;
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        goal.setText("请选择站点");
                    }
                }
            } else {
                goal.setText("请选择站点");
            }
            if (commandConfig.get("direction") != null) {
                directionNum = (int) commandConfig.get("direction");
                switch (directionNum) {
                    case 0:
                        direction.setText("直行");
                        break;
                    case 1:
                        direction.setText("左岔道");
                        break;
                    case 2:
                        direction.setText("右岔道");
                        break;
                }
            } else {
                direction.setText("请选择方向");
            }

            if (commandConfig.get("speed") != null) {
                speed.setText(commandConfig.get("speed").toString().trim());
            }
            if (commandConfig.get("music") != null) {
                mp3.setText(commandConfig.get("music").toString());
            }
            if (commandConfig.get("outime") != null) {
                outTime.setText(commandConfig.get("outime").toString());
            }
            if (commandConfig.get("shownumber") != null) {
                showNum.setText(commandConfig.get("shownumber").toString());
            }
            if (commandConfig.get("showcolor") != null) {
                showColor.setText(commandConfig.get("showcolor").toString());
            }
            switch ((int) commandConfig.get("type")) {
                case 0:
                    break;
                case 1:
                    findViewById(R.id.linear_goal).setVisibility(View.GONE);
                    findViewById(R.id.linear_direction).setVisibility(View.GONE);
                    break;
                case 2:
                    findViewById(R.id.linear_goal).setVisibility(View.GONE);
                    findViewById(R.id.linear_direction).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.speed_text)).setText("旋转速度");
                    break;
                case 3:
                    findViewById(R.id.linear_goal).setVisibility(View.GONE);
                    findViewById(R.id.linear_direction).setVisibility(View.GONE);
                    findViewById(R.id.linear_speed).setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
        findViewById(R.id.btn_sure).setOnClickListener(this);
    }


    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_back:
                finish();
                break;
            case R.id.btn_delete:
                dialog();
                break;
            case R.id.goal:
                dialog_spinner(true);
                break;
            case R.id.direction:
                dialog_spinner(false);
                break;
            case R.id.speed:
                dialog_Text(0);
                break;
            case R.id.mp3:
                dialog_Text(1);
                break;
            case R.id.outime:
                dialog_Text(2);
                break;
            case R.id.shownum:
                dialog_Text(3);
                break;
            case R.id.showcolor:
                dialog_Text(4);
                break;
            case R.id.btn_sure:
                switch ((int) commandConfig.get("type")) {
                    case 0:
                        if (!speed.getText().toString().equals("") && !mp3.getText().toString().equals("") && !outTime.getText().toString().equals("")
                                && !showNum.getText().toString().equals("") && !showColor.getText().toString().equals("") && goalList != null && goalList.size() > 0) {
                            robotDBHelper.execSQL("update command set speed = '" + speed.getText().toString().trim() + "'," +
                                    "music = '" + mp3.getText().toString().trim() + "' ,outime = '" + outTime.getText().toString().trim() + "' ," +
                                    "shownumber = '" + showNum.getText().toString().trim() + "' ,showcolor = '" + showColor.getText().toString().trim() + "' where id= '" + command_id + "'");
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "请选择确认好参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        if (!speed.getText().toString().equals("") && !mp3.getText().toString().equals("") && !outTime.getText().toString().equals("")
                                && !showNum.getText().toString().equals("") && !showColor.getText().toString().equals("")) {
                            robotDBHelper.execSQL("update command set goal= '" + 0 + "' ," +
                                    "direction = '" + 0 + "' ,speed = '" + speed.getText().toString().trim() + "'," +
                                    "music = '" + mp3.getText().toString().trim() + "' ,outime = '" + outTime.getText().toString().trim() + "' ," +
                                    "shownumber = '" + showNum.getText().toString().trim() + "' ,showcolor = '" + showColor.getText().toString().trim() + "' where id= '" + command_id + "'");
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "请选择确认好参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        if (!speed.getText().toString().equals("") && !mp3.getText().toString().equals("") && !outTime.getText().toString().equals("")
                                && !showNum.getText().toString().equals("") && !showColor.getText().toString().equals("")) {
                            robotDBHelper.execSQL("update command set goal= '" + 0 + "' ," +
                                    "direction = '" + 0 + "' ,speed = '" + speed.getText().toString().trim() + "'," +
                                    "music = '" + mp3.getText().toString().trim() + "' ,outime = '" + outTime.getText().toString().trim() + "' ," +
                                    "shownumber = '" + showNum.getText().toString().trim() + "' ,showcolor = '" + showColor.getText().toString().trim() + "' where id= '" + command_id + "'");
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "请选择确认好参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 3:
                        if (!mp3.getText().toString().equals("") && !outTime.getText().toString().equals("")
                                && !showNum.getText().toString().equals("") && !showColor.getText().toString().equals("")) {
                            robotDBHelper.execSQL("update command set goal= '" + 0 + "' ," +
                                    "direction = '" + 0 + "' ,speed = '" + 0 + "'," +
                                    "music = '" + mp3.getText().toString().trim() + "' ,outime = '" + outTime.getText().toString().trim() + "' ," +
                                    "shownumber = '" + showNum.getText().toString().trim() + "' ,showcolor = '" + showColor.getText().toString().trim() + "' where id= '" + command_id + "'");
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "请选择确认好参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private DeleteDialog dialog;

    //删除
    private void dialog() {
        dialog = new DeleteDialog(this);
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除命令
                robotDBHelper.execSQL("delete from command where id = '" + command_id + "'");
                finish();
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private SpinnerDialog spinnerdialog;
    private SpinnerAdapter spinnerAdapter;

    private void dialog_spinner(final boolean gl) {
        spinnerdialog = new SpinnerDialog(this);
        if (gl) {
            spinnerAdapter = new SpinnerAdapter(this, goalList, gl);
        } else {
            spinnerAdapter = new SpinnerAdapter(this, list, gl);
        }
        //加载适配器
        spinnerdialog.getListView().setAdapter(spinnerAdapter);
        spinnerdialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (gl) {
                    goalNum = position;
                } else {
                    directionNum = position;
                }
                spinnerAdapter.notifyDataSetChanged();
            }
        });
        //确定
        spinnerdialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gl) {
                    if (goalList != null && goalList.size() > 0 && goalNum != -1) {
                        robotDBHelper.execSQL("update command set goal= '" + goalList.get(goalNum).get("id") + "' where id= '" + command_id + "'");
                        goal.setText(goalList.get(goalNum).get("name").toString());
                        spinnerdialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "请选择站点系统卡", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //修改系统
                    robotDBHelper.execSQL("update command set direction = '" + directionNum + "' where id= '" + command_id + "'");
                    switch (directionNum) {
                        case 0:
                            direction.setText("直行");
                            break;
                        case 1:
                            direction.setText("左岔道");
                            break;
                        case 2:
                            direction.setText("右岔道");
                            break;
                    }
                    spinnerdialog.dismiss();
                }
            }
        });
        //取消
        spinnerdialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerdialog.dismiss();
            }
        });
        spinnerdialog.show();
    }

    private MyDialog textDialog;
    private EditText editText;
    private void dialog_Text(final int type) {
        textDialog = new MyDialog(this);
        switch (type) {
            case 0:
                textDialog.getTitle().setText("速度修改");
                textDialog.getTitleTemp().setText("请输入最新速度值");
                break;
            case 1:
                textDialog.getTitle().setText("MP3通道修改");
                textDialog.getTitleTemp().setText("请输入MP3通道");
                break;
            case 2:
                textDialog.getTitle().setText("超时时间修改");
                textDialog.getTitleTemp().setText("请输入超时时间");
                break;
            case 3:
                textDialog.getTitle().setText("显示编号修改");
                textDialog.getTitleTemp().setText("请输入显示编号");
                break;
            case 4:
                textDialog.getTitle().setText("显示颜色修改");
                textDialog.getTitleTemp().setText("请输入显示颜色");
                break;
        }
        editText = (EditText) textDialog.getEditText();
        //输入类型
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        //确定
        textDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case 0:
                        if (!editText.getText().toString().trim().equals("")) {
                            robotDBHelper.execSQL("update command set speed = '" + editText.getText().toString().trim() + "' where id= '" + command_id + "'");
                            textDialog.dismiss();
                            speed.setText(editText.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        if (!editText.getText().toString().trim().equals("")) {
                            robotDBHelper.execSQL("update command set music = '" + editText.getText().toString().trim() + "' where id= '" + command_id + "'");
                            textDialog.dismiss();
                            mp3.setText(editText.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        if (!editText.getText().toString().trim().equals("")) {
                            robotDBHelper.execSQL("update command set outime = '" + editText.getText().toString().trim() + "' where id= '" + command_id + "'");
                            textDialog.dismiss();
                            outTime.setText(editText.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 3:
                        if (!editText.getText().toString().trim().equals("")) {
                            robotDBHelper.execSQL("update command set shownumber = '" + editText.getText().toString().trim() + "' where id= '" + command_id + "'");
                            textDialog.dismiss();
                            showNum.setText(editText.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 4:
                        if (!editText.getText().toString().trim().equals("")) {
                            robotDBHelper.execSQL("update command set showcolor = '" + editText.getText().toString().trim() + "' where id= '" + command_id + "'");
                            textDialog.dismiss();
                            showColor.setText(editText.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
        //取消
        textDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog.dismiss();
            }
        });
        textDialog.show();
    }

}
