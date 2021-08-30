package com.example.loginregister.UserInfo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.loginregister.R;

public class SpecDiaLog extends Dialog  {

   private SpecDiaLog specDiaLog;
   private User_Info_Data user_info_data=null;
   private EditText et_spec_title;
   private EditText et_spec_content;
   private TextView tv_confirm;
   private TextView tv_cancle;
   private SpecDiaLogListener specDiaLogListener;

    public SpecDiaLog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_spec);

        et_spec_title = findViewById(R.id.et_spec_title);
        et_spec_content = findViewById(R.id.et_spec_content);
        if(user_info_data!=null) {
            et_spec_title.setText(user_info_data.getUser_info_title());
            et_spec_content.setText(user_info_data.getUser_info_content());
        }
        tv_confirm = findViewById(R.id.tv_confirm);
        tv_cancle = findViewById(R.id.tv_cancle);

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_spec_title.getText().toString();
                String content = et_spec_content.getText().toString();
                User_Info_Data user_info_data = new User_Info_Data(title,content);
                specDiaLogListener.onPositiveClicked(user_info_data);
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

    public SpecDiaLog(@NonNull Context context, User_Info_Data user_info_data) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.user_info_data = user_info_data;
    }



    public void setSpecDialogListener(SpecDiaLogListener specDiaLogListener){
        this.specDiaLogListener = specDiaLogListener;
    }



    interface SpecDiaLogListener {
        void onPositiveClicked(User_Info_Data user_info_data);
        void onNegativeClicked();
    }



}
