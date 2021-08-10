package com.example.loginregister;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class Holder_RecyclerView extends AppCompatActivity {
    TextView tv_content;
    TextView tv_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag1_holder_recycler_view);
        tv_title=(TextView)findViewById(R.id.tv_curri_title);
        tv_content=(TextView)findViewById(R.id.tv_curri_content);
    }
}

class Holder extends RecyclerView.ViewHolder{

    public Holder(@NonNull View itemView) { super(itemView);   }
}
