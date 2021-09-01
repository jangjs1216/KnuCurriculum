package com.example.loginregister.push_alram;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.R;
import com.example.loginregister.curiList.Recycler_Adapter;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Adater_Alarm extends RecyclerView.Adapter<Adater_Alarm.CustomViewHolder> {

    private ArrayList<Alarm> alarms;
    private IOnAlarmClickListener listener;
    public Adater_Alarm(ArrayList<Alarm> alarms){this.alarms = alarms;}


    @NonNull
    @Override
    public Adater_Alarm.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adater_Alarm.CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_title.setText(alarms.get(position).getTitle());
        holder.tv_content.setText("새 댓글이 달렸습니다.");
        SimpleDateFormat dateFormat = new SimpleDateFormat( "MM월 dd일 HH시mm분" , Locale.KOREA );
        Timestamp timestamp = alarms.get(position).getTimestamp();
        String str = dateFormat.format( timestamp.toDate());
        holder.tv_timestamp.setText(str);

        if(!(alarms.get(position).isChecked())){
            holder.itemView.setBackgroundResource(R.color.alarm_checked);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAlarmCLick(v,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return alarms!=null?alarms.size():0;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_timestamp;
        private String forum_sort;
        private String post_id;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_content=itemView.findViewById(R.id.tv_content);
            this.tv_timestamp =itemView.findViewById(R.id.tv_timestamp);
        }
    }

    public interface IOnAlarmClickListener{
        void onAlarmCLick(View v,int pos);
    }
    public void setOnAlarmClickListener(IOnAlarmClickListener listener){
        this.listener= listener;
    }


}
