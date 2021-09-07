package com.example.loginregister.UserInfo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
   private RadioGroup rg;
   private ImageView iv_delete;
   private RadioButton rb1,rb2,rb3,rb4;
   private SpecDiaLogListener specDiaLogListener;

    public SpecDiaLog(@NonNull Context context) {
        super(context,R.style.SpecDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_spec);
        tv_confirm = findViewById(R.id.tv_confirm);
        iv_delete = findViewById(R.id.iv_delete);
        tv_cancle = findViewById(R.id.tv_cancle);
        rg= findViewById(R.id.rg);
        rb1=findViewById(R.id.rb1);
        rb2=findViewById(R.id.rb2);
        rb3=findViewById(R.id.rb3);
        rb4=findViewById(R.id.rb4);
        rg.check(R.id.rb1);
        et_spec_title = findViewById(R.id.et_spec_title);
        et_spec_content = findViewById(R.id.et_spec_content);
        if(user_info_data!=null) {
            et_spec_title.setText(user_info_data.getUser_info_title());
            et_spec_content.setText(user_info_data.getUser_info_content());
            switch(user_info_data.getType()){
                case "1":
                    rg.check(rb1.getId());
                    break;
                case"2":
                    rg.check(rb2.getId());
                    break;
                case"3":
                    rg.check(rb3.getId());
                    break;
                case"4":
                    rg.check(rb4.getId());
                    break;
            }
        }

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specDiaLogListener.onNegativeClicked(user_info_data);
                dismiss();
            }
        });

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type="";
                switch(rg.getCheckedRadioButtonId()){
                    case R.id.rb1:
                        type="1";
                        break;
                    case R.id.rb2:
                        type="2";
                        break;
                    case R.id.rb3:
                        type="3";
                        break;
                    case R.id.rb4:
                        type = "4";
                        break;
                }
                Log.e("spec",type);
                String title = et_spec_title.getText().toString();
                String content = et_spec_content.getText().toString();
                User_Info_Data user_info_data = new User_Info_Data(type,title,content);
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
        void onNegativeClicked(User_Info_Data user_info_data);
    }



}
