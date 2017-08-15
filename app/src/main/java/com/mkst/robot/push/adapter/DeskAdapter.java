package com.mkst.robot.push.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mkst.robot.push.R;
import com.mkst.robot.push.activitys.MainActivity;

import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/9
 * 描述:
 */

public class DeskAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> list;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.desk_item, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.name);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.bjzt = (ImageView) convertView.findViewById(R.id.bjzt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bjzt.setVisibility(View.GONE);
        if (list.get(position).get("image") != null) {
            viewHolder.image.setVisibility(View.VISIBLE);
            viewHolder.text.setVisibility(View.GONE);
            if (position == 0) {
                viewHolder.image.setImageResource(R.animator.btn_add_desk_selector);
            } else {
                if (MainActivity.DeskIsEdit) {
                    viewHolder.bjzt.setVisibility(View.VISIBLE);
                }
                viewHolder.image.setImageResource(R.animator.btn_add_selector);
            }
        } else {
            if (MainActivity.DeskIsEdit) {
                viewHolder.bjzt.setVisibility(View.VISIBLE);
            }
            viewHolder.text.setVisibility(View.VISIBLE);
            viewHolder.image.setVisibility(View.GONE);
            viewHolder.text.setText(list.get(position).get("name").toString());
        }
        return convertView;
    }

    //内部类
    static class ViewHolder {
        public TextView text;
        public ImageView image;
        public ImageView bjzt;
        public RelativeLayout back;
    }
}
