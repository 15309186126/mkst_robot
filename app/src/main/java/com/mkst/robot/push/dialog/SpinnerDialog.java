package com.mkst.robot.push.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mkst.robot.push.R;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/8
 * 描述:自定义运动到站点对话框
 */

public class SpinnerDialog extends Dialog {

    private ListView listView;
    private Button positiveButton, negativeButton;
    private TextView title;

    public SpinnerDialog(Context context) {
        super(context, R.style.SoundRecorder);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_spinner_dialog, null);
        title = (TextView) mView.findViewById(R.id.title);
        listView = (ListView) mView.findViewById(R.id.listview);
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        super.setContentView(mView);
    }

    public ListView getListView() {
        return listView;
    }

    public View getTextView() {
        return title;
    }

    public View getNegative() {
        return negativeButton;
    }

    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 确定键监听器
     */
    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }

    /**
     * 取消键监听器
     */
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }
}
