package com.example.loginregister.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Notice_B.C_Comment;
import com.example.loginregister.Notice_B.Comment;
import com.example.loginregister.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostC_CommentAdapter extends RecyclerView.Adapter<PostC_CommentAdapter.PostC_ContentViewHolder> {

    private List<C_Comment> mcontent_data;

    public PostC_CommentAdapter(List<C_Comment> mcontent_data){
        this.mcontent_data=mcontent_data;
    }

    @NonNull
    @Override
    public PostC_ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostC_ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.c_comment_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostC_ContentViewHolder holder, int position) {
        C_Comment data=mcontent_data.get(position);
        holder.c_nickname.setText(mcontent_data.get(position).getC_nickname());
        holder.comment.setText(mcontent_data.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return mcontent_data.size();
    }

    class PostC_ContentViewHolder extends RecyclerView.ViewHolder{

        private TextView c_nickname;
        private TextView comment;
        public PostC_ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            c_nickname=itemView.findViewById(R.id.c_comment_item_nickname);
            comment=itemView.findViewById(R.id.c_comment_contents);
        }
    }
}
