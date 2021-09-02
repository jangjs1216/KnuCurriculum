package com.example.loginregister.curiList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private OnItemClickListener mListener = null;
    private OnItemLongClickListener mLongListener = null;

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
        protected LinearLayout optionLL, choiceLL, deleteLL;
        protected ImageView imageView2;
        protected Button basicTableBtn;
        protected FrameLayout FL;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_curri_name);
            this.FL = (FrameLayout) itemView.findViewById(R.id.FL);
            this.optionLL = (LinearLayout) itemView.findViewById(R.id.optionLL);
            this.choiceLL = (LinearLayout) itemView.findViewById(R.id.choiceLL);
            this.deleteLL = (LinearLayout) itemView.findViewById(R.id.deleteLL);
            this.imageView2 = (ImageView) itemView.findViewById(R.id.imageView2);
            this.basicTableBtn = (Button) itemView.findViewById(R.id.basicTableBtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if(mListener != null){
                            if(imageView2.getVisibility() == View.VISIBLE){
                                imageView2.setVisibility(View.INVISIBLE);
                                optionLL.setVisibility(View.VISIBLE);
                            }
                            else{
                                imageView2.setVisibility(View.VISIBLE);
                                optionLL.setVisibility(View.INVISIBLE);
                            }
                            choiceLL.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    imageView2.setVisibility(View.VISIBLE);
                                    optionLL.setVisibility(View.INVISIBLE);
                                    mListener.onItemClick(v, pos, "choice");
                                }
                            });

                            deleteLL.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    imageView2.setVisibility(View.VISIBLE);
                                    optionLL.setVisibility(View.INVISIBLE);
                                    mListener.onItemClick(v, pos, "delete");
                                }
                            });
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        mLongListener.onItemLongClick(v, pos);
                    }
                    return true;
                }
            });

            basicTableBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if(mListener != null){
                            mListener.onItemClick(v, pos, "basic");
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos, String option);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View v, int pos);
    }

    public void setOnItemListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){ this.mLongListener = listener; }
}