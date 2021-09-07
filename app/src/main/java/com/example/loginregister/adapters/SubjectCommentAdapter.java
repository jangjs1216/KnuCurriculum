package com.example.loginregister.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.R;
import com.example.loginregister.SubjectComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SubjectCommentAdapter extends RecyclerView.Adapter<SubjectCommentAdapter.SubjectCommentViewHolder> {
    ArrayList<SubjectComment> list;
    private OnItemClickListener mListener = null;


    public SubjectCommentAdapter(ArrayList<SubjectComment> list){
        this.list = list;
    }

    @NonNull
    @Override
    public SubjectCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subjectcomment, parent, false);
        SubjectCommentViewHolder subjectCommentViewHolder = new SubjectCommentViewHolder(view);

        return subjectCommentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectCommentViewHolder holder, int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SubjectCommentViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView writerTV;
        TextView contentTV;
        Button reviseBtn, deleteBtn;
        View line;

        public SubjectCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ratingBar = itemView.findViewById(R.id.ratingBar);
            this.writerTV = itemView.findViewById(R.id.writerTV);
            this.contentTV = itemView.findViewById(R.id.contentTV);
            this.reviseBtn = (Button) itemView.findViewById(R.id.reviseBtn);
            this.deleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
            this.line = (View)itemView.findViewById(R.id.line_list);



            reviseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if(mListener != null){
                            mListener.onItemClick(v, pos, "revise");
                        }
                    }
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if(mListener != null){
                            mListener.onItemClick(v, pos, "delete");
                        }
                    }
                }
            });
        }

        void onBind(SubjectComment subjectComment) {
            ratingBar.setRating(Float.parseFloat(subjectComment.getRating()));
            contentTV.setText(subjectComment.getContent());

            int n = getAdapterPosition();
            if(n==list.size()-1)line.setVisibility(View.INVISIBLE);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user").document(subjectComment.getUser_id())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                String user_nick = task.getResult().getString("nickname");
                                if(user_nick!=null&&user_nick.length()!=0) {
                                    writerTV.setText(user_nick);
                                }
                                else{
                                    writerTV.setText("닉네임 없음");
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
