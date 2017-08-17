package com.mkst.robot.push.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mkst.robot.push.R;
import com.mkst.robot.push.activitys.CommandAcitivty;
import com.mkst.robot.push.utils.Constant;

import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/8
 * 描述: 运行状态适配器
 */

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Map> list;

    public MyAdapter(Context context, List<Map> list) {
        this.context = context;
        this.list = list;
    }

    //返回有多少个条目
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //返回每个位置条目显示的内容
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            //解析布局
            final LayoutInflater inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, null);
            //创建ViewHolder持有类
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.btn = (Button) convertView.findViewById(R.id.btn);
            //将每个convertView对象中设置这个持有类对象
            convertView.setTag(viewHolder);
        } else {
            //每次需要使用的时候都会拿到这个持有类
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //然后可以直接使用这个类中的控件，对控件进行操作，而不用重复去findViewById了
        ViewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转
                Intent intent = new Intent(context, CommandAcitivty.class);
                //打印log测试
                Constant.debugLog("commandid" + list.get(position).get("id").toString());
                intent.putExtra("id", (Integer) list.get(position).get("id"));
                context.startActivity(intent);
            }
        });

        switch ((int)list.get(position).get("type")){
            case 0:
                viewHolder.text.setText(R.string.straight);
                break;
            case 1:
                viewHolder.text.setText(R.string.derail);
                break;
            case 2:
                viewHolder.text.setText(R.string.rotato);
                break;
            case 3:
                viewHolder.text.setText(R.string.wait);
                break;
            case 4:
                viewHolder.text.setText(R.string.puthook);
                break;
            case 5:
                viewHolder.text.setText(R.string.lockhook);
                break;
        }

        return convertView;
    }

    //内部类
    static class ViewHolder {
        public TextView text;
        public static Button btn;
    }
}
