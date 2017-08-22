package com.mkst.robot.push.activitys;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mkst.robot.push.R;
import com.mkst.robot.push.adapter.MyAdapter;
import com.mkst.robot.push.app.Activity;
import com.mkst.robot.push.dialog.DeleteDialog;
import com.mkst.robot.push.dialog.MyDialog;
import com.mkst.robot.push.helper.RobotDBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/16
 * 描述: 设置指令
 */

public class DeskConfigPathActivity extends Activity implements View.OnClickListener {

    private RobotDBHelper robotDBHelper;
    private int deskId, areaId;
    private TextView name;
    private Map deskConfigList;
    private ListView commandListView;
    private List<Map> command_list = new ArrayList<>();
    private boolean IsADD = false;
    private MyAdapter myAdapter;
    private List<View> listViews;
    private ImageView cursorIv;
    private TextView tab01, tab02;
    private TextView[] titles;
    private ViewPager viewPager;
    private int offset = 0;


    /**
     * 下划线图片宽度
     */
    private int lineWidth;

    /**
     * 当前选项卡的位置
     */
    private int current_index = 0;

    /**
     * 选项卡总数
     */
    private static final int TAB_COUNT = 2;
    private static final int TAB_0 = 0;
    private static final int TAB_1 = 1;


    @Override
    protected int getContentLayoutId() {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_config;
    }

    //初始化控件
    @Override
    protected void initWidget() {
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        // 获取MainActivity传递的id  area
        Intent intent = getIntent();
        deskId = intent.getIntExtra("id", 0);
        areaId = intent.getIntExtra("area", 0);

        name = (TextView) findViewById(R.id.deskname);
        name.setOnClickListener(this);
        findViewById(R.id.change_name).setOnClickListener(this);
        findViewById(R.id.setting_back).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.card).setOnClickListener(this);

        if (deskId == 0) {
            findViewById(R.id.btn_delete).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.title)).setText(R.string.desk_add);
            IsADD = true;
        } else {
            ((TextView) findViewById(R.id.title)).setText(R.string.desk_settings);
            IsADD = false;
            List<Map> deskList = robotDBHelper.queryListMap("select * from desk where id = '" + deskId + "'", null);
            deskConfigList = deskList.get(0);
            name.setText(deskConfigList.get("name").toString());
            ((TextView) findViewById(R.id.title)).setText(R.string.desk_deract);
        }

        viewPager = (ViewPager) findViewById(R.id.vPager);
        cursorIv = (ImageView) findViewById(R.id.iv_tab_bottom_img);
        tab01 = (TextView) findViewById(R.id.tv01);
        tab02 = (TextView) findViewById(R.id.tv02);

        tab01.setOnClickListener(this);
        tab02.setOnClickListener(this);

        // 获取图片宽度
        lineWidth = BitmapFactory.decodeResource(getResources(), R.mipmap.up_pre).getWidth();
        // Android提供的DisplayMetrics可以很方便的获取屏幕分辨率
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels; // 获取分辨率宽度
        offset = (screenW / TAB_COUNT - lineWidth) / 2;  // 计算偏移值
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        // 设置下划线初始位置
        cursorIv.setImageMatrix(matrix);

        listViews = new ArrayList<>();
        listViews.add(this.getLayoutInflater().inflate(R.layout.tab_01, null));
        View view = this.getLayoutInflater().inflate(R.layout.tab_02, null);
        commandListView = (ListView) view.findViewById(R.id.added_command);
        myAdapter = new MyAdapter(this, command_list);
        commandListView.setAdapter(myAdapter);
        listViews.add(view);

        viewPager.setAdapter(new MyPagerAdapter(listViews));
        viewPager.setCurrentItem(0);
        titles = new TextView[]{tab01, tab02};

        viewPager.setOffscreenPageLimit(titles.length);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int one = offset * 2 + lineWidth;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                refreshCommand();
            }

            @Override
            public void onPageSelected(int position) {
                // 下划线开始移动前的位置
                float fromX = one * current_index;
                // 下划线移动完毕后的位置
                float toX = one * position;
                Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
                animation.setFillAfter(true);
                animation.setDuration(500);
                // 给图片添加动画
                cursorIv.startAnimation(animation);
                // 当前Tab的字体变成红色
                titles[position].setTextColor(Color.parseColor("#FFB837"));
                titles[current_index].setTextColor(Color.BLACK);
                current_index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                refreshCommand();
            }
        });
    }

    public void testClick(View v) {
        if (IsADD) {
            Toast.makeText(getApplicationContext(), "请先添加餐桌名称", Toast.LENGTH_SHORT).show();
        }
        switch (v.getId()) {
            case R.id.straight:
                robotDBHelper.execSQL("insert into command (type,desk) values ('0','" + deskId + "')");
                Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.derail:
                robotDBHelper.execSQL("insert into command (type,desk) values ('1','" + deskId + "')");
                Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rotato:
                robotDBHelper.execSQL("insert into command (type,desk) values ('2','" + deskId + "')");
                Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.wait:
                robotDBHelper.execSQL("insert into command (type,desk) values ('3','" + deskId + "')");
                Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.puthook:
                robotDBHelper.execSQL("insert into command (type,desk) values ('4','" + deskId + "')");
                Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.lockhook:
                robotDBHelper.execSQL("insert into command (type,desk) values ('5','" + deskId + "')");
                Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                break;
        }
        refreshCommand();
    }

    //刷新命令
    public void refreshCommand() {
        command_list.clear();
        List<Map> list = robotDBHelper.queryListMap("select * from command where desk = '" + deskId + "'", null);
        command_list.addAll(list);
        myAdapter.notifyDataSetChanged();
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deskname:
                dialog_Text();
                break;
            case R.id.btn_delete:
                dialog();
                break;
            case R.id.card:
                startActivity(new Intent(DeskConfigPathActivity.this, CardConfig.class));
                break;
            case R.id.setting_back:
                finish();
                break;
            case R.id.change_name:
                if (name.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "名称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (IsADD) {
                        robotDBHelper.execSQL("insert into desk (name,area) values ('" + name.getText().toString() + "','" + areaId + "')");
                        List<Map> deskList = robotDBHelper.queryListMap("select * from desk where area = '" + areaId + "'", null);
                        deskId = (int) deskList.get(deskList.size() - 1).get("id");
                        deskList = robotDBHelper.queryListMap("select * from desk where id = '" + deskId + "'", null);
                        deskConfigList = deskList.get(0);
                        ((TextView) findViewById(R.id.title)).setText(R.string.desk_deract);
                        IsADD = false;
                        findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                    } else {
                        robotDBHelper.execSQL("update desk set name= '" + name.getText().toString().trim() + "' where id= '" + deskId + "'");
                        Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tv01:
                // 避免重复加载
                if (viewPager.getCurrentItem() != TAB_0) {
                    viewPager.setCurrentItem(TAB_0);
                }
                break;
            case R.id.tv02:
                if (viewPager.getCurrentItem() != TAB_1) {
                    viewPager.setCurrentItem(TAB_1);
                    refreshCommand();
                }
                break;
        }
    }

    private DeleteDialog dialog;

    //删除
    private void dialog() {
        dialog = new DeleteDialog(this);
        //确定
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                robotDBHelper.execSQL("delete from desk where id= '" + deskId + "'");
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

    private MyDialog textDialog;
    private EditText editText;
    private TextView title;

    private void dialog_Text() {
        textDialog = new MyDialog(this);
        editText = (EditText) textDialog.getEditText();
        textDialog.getTitle().setText("桌名修改");
        textDialog.getTitleTemp().setText("请输入新桌名");
        //确定
        textDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().trim().equals("")) {
                    if (IsADD) {
                        robotDBHelper.execSQL("insert into desk (name,area) values ('" + editText.getText().toString().trim() + "','" + areaId + "')");
                        List<Map> deskList = robotDBHelper.queryListMap("select * from desk where area = '" + areaId + "'", null);
                        deskId = (int) deskList.get(deskList.size() - 1).get("id");
                        deskList = robotDBHelper.queryListMap("select * from desk where id = '" + deskId + "'", null);
                        deskConfigList = deskList.get(0);
                        ((TextView) findViewById(R.id.title)).setText(R.string.desk_deract);
                        IsADD = false;
                        findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                    } else {
                        robotDBHelper.execSQL("update desk set name= '" + editText.getText().toString().trim() + "' where id= '" + deskId + "'");
                        Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
                    }
                    name.setText(editText.getText().toString().trim());
                } else {
                    Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                }
                textDialog.dismiss();
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


    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {

        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCommand();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
