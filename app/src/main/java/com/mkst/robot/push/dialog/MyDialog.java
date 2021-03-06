package com.mkst.robot.push.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mkst.robot.push.R;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/8
 * 描述: 自定义区域对话框
 */

public class MyDialog extends Dialog {

    private EditText editText;
    private Button positiveButton, negativeButton;
    private TextView title, title_template;

    public MyDialog(Context context) {
        super(context, R.style.SoundRecorder);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog, null);
        title = (TextView) mView.findViewById(R.id.title);
        editText = (EditText) mView.findViewById(R.id.editText);
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        title_template = (TextView) mView.findViewById(R.id.title_template);
        super.setContentView(mView);
    }

    public View getEditText() {
        return editText;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getTitleTemp() {
        return title_template;
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
