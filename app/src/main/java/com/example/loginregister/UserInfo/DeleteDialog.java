package com.example.loginregister.UserInfo;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.loginregister.R;

public class DeleteDialog extends Dialog {
    private OnBtnClickListener onBtnClickListener;
    private TextView tv_confirm;
    private TextView tv_cancle;
    public DeleteDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete);
        tv_confirm = findViewById(R.id.tv_confirm);
        tv_cancle = findViewById(R.id.tv_cancle);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnClickListener.onPositiveClick();
                dismiss();
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnClickListener.onNegativeClick();
                dismiss();
            }
        });
    }

    public interface OnBtnClickListener{
        void onPositiveClick();
        void onNegativeClick();
    }

    public void setOnBtnClickListener(OnBtnClickListener onBtnClickListener){
        this.onBtnClickListener = onBtnClickListener;
    }
}