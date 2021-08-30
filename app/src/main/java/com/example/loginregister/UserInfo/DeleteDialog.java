package com.example.loginregister.UserInfo;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.loginregister.R;

public class DeleteDialog extends Dialog {


    private OnDeleteDialoglickListener onDeleteDialoglickListener;
    private TextView tv_confirm;
    private TextView tv_cancle;

    public DeleteDialog(@NonNull Context context,OnDeleteDialoglickListener onDeleteDialoglickListener) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        this.onDeleteDialoglickListener = onDeleteDialoglickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete);

        tv_confirm = findViewById(R.id.tv_confirm);
        tv_cancle = findViewById(R.id.tv_cancle);
        Log.e("dkstmdwo","zz");
        if(onDeleteDialoglickListener==null) Log.e("dkstmdwo","fail");

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteDialoglickListener.onPositiveClick();
                dismiss();
            }
        });

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface OnDeleteDialoglickListener {
        void onPositiveClick();
    }

    public void setOnDeleteDialogClickListener(OnDeleteDialoglickListener onDeleteDialoglickListener){
        this.onDeleteDialoglickListener = onDeleteDialoglickListener;
    }
}