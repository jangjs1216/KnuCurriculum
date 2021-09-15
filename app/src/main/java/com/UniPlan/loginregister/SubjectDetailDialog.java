package com.UniPlan.loginregister;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.UniPlan.loginregister.R;

public class SubjectDetailDialog extends Dialog {
    SubjectDetailDialog detailDialog;
    String subjectName;
    Spinner detailSpinner;
    ToggleButton detailSwitch;

    interface CustomDialogListener{
        void onReturnClicked(Boolean isTakenClass, String TakenSemester);
    }

    private CustomDialogListener customDialogListener;

    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }

    public SubjectDetailDialog(Context context, String subjectName)
    {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.subjectName = subjectName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.detail_dialog);

        detailDialog = this;

        // TextView 연결
        TextView oView = (TextView) this.findViewById(R.id.detail_textview);
        oView.setText(subjectName);

        // Spinner 연결
        detailSpinner = (Spinner) this.findViewById(R.id.detail_spinner);
        ArrayAdapter semesterAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.datail_semester, android.R.layout.simple_spinner_item);
        detailSpinner.setAdapter(semesterAdapter);

        // Switch 연결
        detailSwitch = (ToggleButton) this.findViewById(R.id.detail_switch);
        detailSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detailSwitch.isChecked()){
                    Log.e("###", "스위치가 선택되었습니다.");
                }else{

                }
            }
        });

        Button oBtn = (Button)this.findViewById(R.id.detail_ok);
        oBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean isTakenClass = detailSwitch.isChecked();
                String takenSemester = detailSpinner.getSelectedItem().toString();

                customDialogListener.onReturnClicked(isTakenClass, takenSemester);
                dismiss();
            }
        });
    }
}
