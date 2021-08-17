package com.example.loginregister.adapters;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Notice_B.C_Comment;
import com.example.loginregister.Notice_B.Comment;
import com.example.loginregister.Notice_B.Post_Comment;
import com.example.loginregister.R;
import com.example.loginregister.login.FirebaseID;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.PostCommentViewHolder> {

    ArrayList<Comment> mcontent_data = new ArrayList<Comment>();
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition=-1;
    Activity activity;

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    public PostCommentAdapter(ArrayList<Comment> mcontent_data, Activity mactivity){
        this.mcontent_data=mcontent_data;
        activity=mactivity;
    }

    @NonNull
    @Override
    public PostCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostCommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentViewHolder holder, int position) {
        holder.onBind(position,selectedItems);
    }



    @Override
    public int getItemCount() {
        if(mcontent_data == null) return 0;
        return mcontent_data.size();
    }

    class PostCommentViewHolder extends RecyclerView.ViewHolder{

        private TextView c_nickname;
        private TextView comment;
        private ImageView c_photo;
        OnViewHolderItemClickListener onViewHolderItemClickListener;
        private LinearLayout comment_layout;
        private ImageView iv_point;

        public PostCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            c_nickname=itemView.findViewById(R.id.comment_item_nickname);
            comment=itemView.findViewById(R.id.comment_contents);
            comment_layout=itemView.findViewById(R.id.comment_layout);
            iv_point=itemView.findViewById(R.id.iv_point);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder msgBuilder = new AlertDialog.Builder(activity)
                            .setTitle("대댓글 작성")
                            .setMessage("작성하시겠습니까?")
                            .setPositiveButton("작성", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    int ppap =getAdapterPosition();
                                    Comment precomment = mcontent_data.get(ppap);
                                    String comment_id = precomment.getComment_id();

                                    ((Post_Comment)Post_Comment.mcontext).compared(comment_id);

                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    AlertDialog msgDlg = msgBuilder.create();
                    msgDlg.show();
                }
            });
        }
        public void onBind( int position, SparseBooleanArray selectedItems) {

            c_nickname.setText(mcontent_data.get(position).getC_nickname());
            comment.setText(mcontent_data.get(position).getComment());

            int judge = Integer.parseInt(mcontent_data.get(position).getComment_id());
            Log.e("%%%",Integer.toString(judge));
            Log.e("%%%",Integer.toString(judge%100));

            LinearLayout.LayoutParams params
                    = (LinearLayout.LayoutParams)comment_layout.getLayoutParams();


            if(judge%100 != 0) {

                params.weight = 1;
                comment_layout.setLayoutParams(params);
                if(position>0) {
                    if (Integer.parseInt(mcontent_data.get(position - 1).getComment_id()) % 100 == 0) {
                        iv_point.setVisibility(View.VISIBLE);
                    }
                }
            }
            else{

                params.weight = 0;
                comment_layout.setLayoutParams(params);

            }
        }

        public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {
            this.onViewHolderItemClickListener = onViewHolderItemClickListener;
        }

    }
    public interface OnViewHolderItemClickListener {
        void onViewHolderItemClick();
    }
}
