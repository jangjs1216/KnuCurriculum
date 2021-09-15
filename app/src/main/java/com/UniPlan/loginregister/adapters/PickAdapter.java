package com.UniPlan.loginregister.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.UniPlan.loginregister.Picksubject;
import com.UniPlan.loginregister.R;


import java.util.ArrayList;

public class PickAdapter extends RecyclerView.Adapter<PickAdapter.PickViewHolder> {
    ArrayList<Picksubject> list;
    private PickAdapter.OnItemClickListener mListener = null;


    public PickAdapter(ArrayList<Picksubject> list){
        this.list = list;
    }

    @NonNull
    @Override
    public PickAdapter.PickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickadapter_item, parent, false);
        PickAdapter.PickViewHolder pickViewHolder = new PickAdapter.PickViewHolder(view);

        return pickViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PickViewHolder holder, int position) {
        holder.onBind(list.get(position));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PickViewHolder extends RecyclerView.ViewHolder {
        private TextView cursub;
        private TextView next_sub;
        private TextView n_next_sub;
        private TextView first;
        private TextView second;

        public PickViewHolder(@NonNull View itemView) {
            super(itemView);

            cursub = itemView.findViewById(R.id.cur_subname);
            next_sub = itemView.findViewById(R.id.next_subname);
            n_next_sub = itemView.findViewById(R.id.n_next_subname);
            first = itemView.findViewById(R.id.first_pick);
            second = itemView.findViewById(R.id.second_pick);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition() ;
//                    if (pos != RecyclerView.NO_POSITION) {
//                        if(mListener != null){
//                            mListener.onItemClick(v, pos);
//                        }
//                    }
//                }
//            });
        }

        void onBind(Picksubject subject) {
           cursub.setText(subject.getCurSub());
            next_sub.setText(subject.getNextSub());
            n_next_sub.setText(subject.getN_nextSub());
            first.setText(Integer.toString((int) (subject.getFirst() * 100))+"%");
            second.setText(Integer.toString((int)(subject.getSecond()*100))+"%");
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }

    public void setOnItemListener(PickAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }
}
