package com.example.loginregister.UserInfo;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.R;

import java.util.ArrayList;

public class Adapter_User_Info extends RecyclerView.Adapter<Adapter_User_Info.CustomViewHolder> {

    private ArrayList<User_Info_Data> arrayList;

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
        holder.et_user_info_title.setText(arrayList.get(position).getUser_info_title());
        holder.et_user_info_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(position<arrayList.size()){
                    arrayList.get(holder.getAdapterPosition()).setUser_info_title(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.et_user_info_content.setText(arrayList.get(position).getUser_info_content());
        holder.et_user_info_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(position<arrayList.size()){
                    arrayList.get(holder.getAdapterPosition()).setUser_info_content(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  눌렀을때 할 동작 하면 된다
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //  길게 눌렀을 때 할 동작
                //remove(holder.getAdapterPosition());
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
        protected EditText et_user_info_title;
        protected EditText et_user_info_content;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.et_user_info_title = itemView.findViewById(R.id.et_user_info_title);
            this.et_user_info_content=itemView.findViewById(R.id.et_user_info_content);
        }
    }
}
