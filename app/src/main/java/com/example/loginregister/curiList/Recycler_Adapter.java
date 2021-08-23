package com.example.loginregister.curiList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Fragment2;
import com.example.loginregister.R;
import com.example.loginregister.adapters.SubjectAdapter;

import java.util.ArrayList;

public class Recycler_Adapter extends RecyclerView.Adapter<Recycler_Adapter.CustomViewHolder> {

    private ArrayList<Recycler_Data> arrayList;
    private OnItemClickListener mListener;

    public Recycler_Adapter(ArrayList<Recycler_Data> arrayList){
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Recycler_Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.curi_list_recycler,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Recycler_Adapter.CustomViewHolder holder, int position) {
        holder.tv_title.setText(arrayList.get(position).getTv_title());
        holder.itemView.setTag(position);
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
        protected TextView tv_title;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = (TextView)itemView.findViewById(R.id.tv_curri_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if(mListener != null){
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }

    public void setOnItemListener(OnItemClickListener listener){
        this.mListener = listener;
    }
}
