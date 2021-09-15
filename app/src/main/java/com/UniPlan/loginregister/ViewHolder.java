package com.UniPlan.loginregister;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.UniPlan.loginregister.R;

public class ViewHolder {
    /*
    [20210806] 장준승
    ViewHolder Class : Tree 시각화 하기 위한 Class
     */

    String semester;
    Boolean isTaken;
    public CardView tn_layout;
    public TextView mTextView;
    public TextView semesterTv;
    public ViewHolder(View view) {
        tn_layout = view.findViewById(R.id.card_view);
        mTextView = view.findViewById(R.id.textView);
        semesterTv = view.findViewById(R.id.tv_semester_view);
    }

    public void setViewHoldernotSelected()
    {
        mTextView.setTextColor(Color.parseColor("#ffffff"));
//        tn_layout.setBackgroundResource(R.drawable.not_selected_treenode);
        isTaken = false;
    }

    public void setViewHolderSelected()
    {
        mTextView.setTextColor(Color.parseColor("#008000"));
//        tn_layout.setBackgroundResource(R.drawable.selected_treenode);
        isTaken = true;
    }

    public void setSemesterColored()
    {
        //Log.e("###","현재 semester값 : "+semesterTv.getText());
        if(semesterTv.getText().equals("1학년 1학기"))
        {
            tn_layout.setCardBackgroundColor(Color.parseColor("#EEAFAF"));
        }else if(semesterTv.getText().equals("1학년 2학기")){
            tn_layout.setCardBackgroundColor(Color.parseColor("#AFC4E7"));
        }else if(semesterTv.getText().equals("1학년 2학기")){
            tn_layout.setCardBackgroundColor(Color.parseColor("#BAE7AF"));
        }else if(semesterTv.getText().equals("2학년 1학기")){
            tn_layout.setCardBackgroundColor(Color.parseColor("#FFF77F"));
        }else if(semesterTv.getText().equals("2학년 2학기")){
            tn_layout.setCardBackgroundColor(Color.parseColor("#FF7F7F"));
        }else if(semesterTv.getText().equals("3학년 1학기")){
            tn_layout.setCardBackgroundColor(Color.parseColor("#FDC4F6"));
        }else if(semesterTv.getText().equals("3학년 2학기")){
            tn_layout.setCardBackgroundColor(Color.parseColor("#CB9FFD"));
        }else if(semesterTv.getText().equals("4학년 1학기")){
            tn_layout.setCardBackgroundColor(Color.parseColor("#A9E1ED"));
        }else if(semesterTv.getText().equals("4학년 2학기")){
            tn_layout.setCardBackgroundColor(Color.parseColor("#F3CDAC"));
        }else if(semesterTv.getText().equals("5학년 1학기")){
            tn_layout.setCardBackgroundColor(Color.parseColor("#000000"));
        }
        semester = semesterTv.getText().toString();
    }
}
