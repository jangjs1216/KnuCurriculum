package com.example.loginregister.UserInfo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.loginregister.R;

public class Dialog_setgpa extends Dialog {
    private SetGpaDialogListener setGpaDialogListener ;
    private EditText et_gpa;
    private TextView tv_confirm,tv_cancle;
    private ImageView iv_cancle;

    public Dialog_setgpa(@NonNull Context context) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_usergpa);

        et_gpa = findViewById(R.id.et_gpa);
        iv_cancle = findViewById(R.id.iv_cancle);
        tv_confirm = findViewById(R.id.tv_confirm);
        tv_cancle = findViewById(R.id.tv_cancle);

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String univ = et_gpa.getText().toString();

                if(univ==null){
                    Toast.makeText(getContext(),"한 글자 이상 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
                else{
                    setGpaDialogListener.onPositiveClicked(univ);
                    dismiss();
                }
            }
        });

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        iv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setOnDialogClickListener(SetGpaDialogListener listener){
        this.setGpaDialogListener = listener;
    }

    interface SetGpaDialogListener{
        void onPositiveClicked(String userGpa);
    }

}
