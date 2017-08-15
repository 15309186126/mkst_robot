package com.mkst.robot.push.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkst.robot.push.R;
import com.mkst.robot.push.activitys.CommandAcitivty;

import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/8
 * 描述: 区域名称适配器
 */

public class SpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<Map> list;
    private boolean flag;

    public SpinnerAdapter(Context context, List<Map> list, boolean flag) {
        this.context = context;
        this.list = list;
        this.flag = flag;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            //解析布局
            final LayoutInflater inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_card, null);
            //创建ViewHolder持有类
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.imageview = (ImageView) convertView.findViewById(R.id.imageview);
            //将每个convertView对象中设置这个持有类对象
            convertView.setTag(viewHolder);
        } else {
            //每次需要使用的时候都会拿到这个持有类
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //然后可以直接使用这个类中的控件，对控件进行操作，而不用重复去findViewById了
        viewHolder.imageview.setVisibility(View.GONE);
        if (flag) {
            if (position == CommandAcitivty.goalnum) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
            }
        } else {
            if (position == CommandAcitivty.directionnum) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
            }
        }
        viewHolder.text.setText(list.get(position).get("name").toString());
        return convertView;
    }

    //静态内部类
    static class ViewHolder {
        TextView text;
        ImageView imageview;
    }
}
