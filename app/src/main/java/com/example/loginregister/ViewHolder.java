package com.example.loginregister;

import android.view.View;
import android.widget.TextView;

public class ViewHolder {
    /*
    [20210806] 장준승
    ViewHolder Class : Tree 시각화 하기 위한 Class
     */

    TextView mTextView;
    ViewHolder(View view) {
        mTextView = view.findViewById(R.id.textView);
    }
}
