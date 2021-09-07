package com.example.loginregister.UserInfo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.R;

import java.util.ArrayList;

public class Adapter_User_Info extends RecyclerView.Adapter<Adapter_User_Info.CustomViewHolder> implements ItemTouchHelperListener{

    private ArrayList<User_Info_Data> arrayList;
    private OnItemClickListner onItemClicklistner =null;
    private OnItemSwipeListener onItemSwipeListener = null;

    public Adapter_User_Info(ArrayList<User_Info_Data> arrayList){
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_info_detail,parent,false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_user_info_title.setText(arrayList.get(position).getUser_info_title());
        holder.tv_user_info_content.setText(arrayList.get(position).getUser_info_content());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClicklistner !=null) {
                    onItemClicklistner.onItemClick(v, position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClicklistner!=null){
                    onItemClicklistner.onItemLongClick(v,position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null!=arrayList ? arrayList.size():0);
    }

    public void remove(int position){
        try{
            arrayList.remove(position);
            notifyItemRemoved(position);
        }catch (IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView tv_user_info_title;
        protected TextView tv_user_info_content;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_user_info_title = itemView.findViewById(R.id.tv_user_info_title);
            this.tv_user_info_content=itemView.findViewById(R.id.tv_user_info_content);
        }
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        User_Info_Data user_info_data = arrayList.get(from_position);
        arrayList.remove(from_position);
        arrayList.add(to_position,user_info_data);
        notifyItemMoved(from_position,to_position);
        return true;
    }

    @Override
    public void onItemSwipe(int position) {
        onItemSwipeListener.onItemSwipe(position);
    }

    public interface OnItemClickListner{
        void onItemClick(View v, int pos);
        void onItemLongClick(View v,int pos);
    }
    public void setOnItemListener(OnItemClickListner onItemClicklistner){
        this.onItemClicklistner = onItemClicklistner;
    }

    public void setOnItemSwipeListener(OnItemSwipeListener onItemSwipeListener) {
        this.onItemSwipeListener = onItemSwipeListener;
    }

    public interface OnItemSwipeListener{
        void onItemSwipe(int position);
    }
}
