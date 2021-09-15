package com.UniPlan.loginregister.UserInfo;

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

import com.UniPlan.loginregister.R;

public class Dialog_delete_spec extends Dialog  {


    private TextView tv_confirm;
    private TextView tv_cancle;
    private DeleteDiaLogListener deleteDialogListener;

    public Dialog_delete_spec (@NonNull Context context) {
        super(context,R.style.SpecDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_spec);
        tv_confirm = findViewById(R.id.tv_confirm);
        tv_cancle = findViewById(R.id.tv_cancle);





        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialogListener.onPositiveClicked();
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




    public void setDeleteDialogListener(DeleteDiaLogListener deleteDialogListener){
        this.deleteDialogListener = deleteDialogListener;
    }



    interface DeleteDiaLogListener {
        void onPositiveClicked();
    }



}
