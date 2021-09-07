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

public class Dialog_setNick extends Dialog {
    private SetNickDialogListener setNickDialogListener ;
    private EditText et_nick;
    private TextView tv_confirm,tv_cancle;
    private ImageView iv_cancle;

    public Dialog_setNick(@NonNull Context context) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setnick);

        et_nick = findViewById(R.id.et_nick);

        tv_confirm = findViewById(R.id.tv_confirm);
        tv_cancle = findViewById(R.id.tv_cancle);
        iv_cancle=findViewById(R.id.iv_cancle);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nick = et_nick.getText().toString();

                if(nick==null){
                    Toast.makeText(getContext(),"한 글자 이상 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
                else{
                    setNickDialogListener.onPositiveClicked(nick);
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

    public void setOnDialogClickListener(SetNickDialogListener listener){
        this.setNickDialogListener = listener;
    }

    interface SetNickDialogListener{
        void onPositiveClicked(String userNick);
    }

}
