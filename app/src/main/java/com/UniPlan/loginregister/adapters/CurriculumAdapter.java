package com.UniPlan.loginregister.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.UniPlan.loginregister.curiList.Recycler_Data;
import com.UniPlan.loginregister.R;

import java.util.ArrayList;

public class CurriculumAdapter extends RecyclerView.Adapter<CurriculumAdapter.CustomViewHolder> {

    private ArrayList<Recycler_Data> arrayList;
    private OnItemClickListener mListener = null;

    public CurriculumAdapter(ArrayList<Recycler_Data> arrayList){
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public CurriculumAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curriculum,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CurriculumAdapter.CustomViewHolder holder, int position) {
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
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_curri_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if(mListener != null){
                            mListener.onItemClick(v, pos, "choice");
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos, String option);
    }

    public void setOnItemListener(OnItemClickListener listener){
        this.mListener = listener;
    }
}