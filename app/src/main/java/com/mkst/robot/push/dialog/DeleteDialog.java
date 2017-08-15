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
 * 描述:自定义删除指令对话框
 */

public class DeleteDialog extends Dialog {

    private EditText editText;
    private Button positiveButton, negativeButton;
    private TextView title;

    public DeleteDialog(Context context) {
        super(context, R.style.SoundRecorder);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_delete_dialog, null);
        title = (TextView) view.findViewById(R.id.title);
        editText = (EditText) view.findViewById(R.id.editText);
        positiveButton = (Button) view.findViewById(R.id.positiveButton);
        negativeButton = (Button) view.findViewById(R.id.negativeButton);
        super.setContentView(view);
    }

    public View getEditText() {
        return editText;
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
