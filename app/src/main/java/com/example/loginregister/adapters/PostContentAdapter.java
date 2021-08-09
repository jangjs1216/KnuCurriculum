package com.example.loginregister.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Notice_B.Content;
import com.example.loginregister.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostContentAdapter extends RecyclerView.Adapter<PostContentAdapter.PostContentViewHolder> {

    private List<Content> mcontent_data;

    public PostContentAdapter(List<Content> mcontent_data){
        this.mcontent_data=mcontent_data;
    }

    @NonNull
    @Override
    public PostContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostContentViewHolder holder, int position) {
        Content data=mcontent_data.get(position);
        holder.c_nickname.setText(mcontent_data.get(position).getC_nickname());
        holder.comment.setText(mcontent_data.get(position).getComment());
        Log.d("씨포토2",mcontent_data.get(position).getC_photo());
        if ( !mcontent_data.get(position).getC_photo().isEmpty()) {
            Picasso.get()
                    .load(mcontent_data.get(position).getC_photo())
                    .into(holder.c_photo);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.ic_baseline_home_24)
                    .into(holder.c_photo);
        }
    }

    @Override
    public int getItemCount() {
        return mcontent_data.size();
    }

    class PostContentViewHolder extends RecyclerView.ViewHolder{

        private TextView c_nickname;
        private TextView comment;
        private ImageView c_photo;
        public PostContentViewHolder(@NonNull View itemView) {
            super(itemView);
            c_nickname=itemView.findViewById(R.id.comment_item_nickname);
            comment=itemView.findViewById(R.id.comment_contents);
            c_photo=itemView.findViewById(R.id.comment_item_photo);
        }
    }
}
