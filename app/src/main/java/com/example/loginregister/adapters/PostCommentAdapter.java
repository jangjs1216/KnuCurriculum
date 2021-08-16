package com.example.loginregister.adapters;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.FirebaseID;
import com.example.loginregister.Notice_B.C_Comment;
import com.example.loginregister.Notice_B.Comment;
import com.example.loginregister.Notice_B.Post_Comment;
import com.example.loginregister.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.PostCommentViewHolder> {

    private List<Comment> mcontent_data;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition=-1;
    Activity activity;
    List<C_Comment> c_mcontent;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    public PostCommentAdapter(List<Comment> mcontent_data, Activity mactivity){
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
        holder.onBind(mcontent_data.get(position),position,selectedItems);
    }


    public void sibal ()
    {}

    @Override
    public int getItemCount() {
        return mcontent_data.size();
    }

    class PostCommentViewHolder extends RecyclerView.ViewHolder{

        private TextView c_nickname;
        private TextView comment;
        private ImageView c_photo;
        OnViewHolderItemClickListener onViewHolderItemClickListener;

        public PostCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            c_nickname=itemView.findViewById(R.id.comment_item_nickname);
            comment=itemView.findViewById(R.id.comment_contents);
            c_photo=itemView.findViewById(R.id.comment_item_photo);
            RecyclerView recyclerView = itemView.findViewById(R.id.comment_recycler);

            c_mcontent = new ArrayList<>();//리사이클러뷰에 표시할 댓글 목록
            int i=-1;
            for( Comment sexy : mcontent_data){
                int i1 = ++i;
                String comment_id = sexy.getComment_id();

                mStore.collection("Comment").document(comment_id).collection(comment_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    c_mcontent.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> shot = document.getData();

                                        String documentId = String.valueOf(shot.get(FirebaseID.documentId));
                                        String comment = String.valueOf(shot.get(FirebaseID.comment));
                                        String c_nickname = String.valueOf(shot.get(FirebaseID.nickname));
                                        String num_comment = String.valueOf(shot.get(FirebaseID.comment_post));

                                        C_Comment data = new C_Comment(documentId, c_nickname, comment, Integer.toString(i1), comment_id);
                                        c_mcontent.add(data);//여기까지가 게시글에 해당하는 데이터 적용
                                    }

                                    PostC_CommentAdapter c_contentAdapter = new PostC_CommentAdapter(c_mcontent);//mDatas라는 생성자를 넣어줌
                                    recyclerView.setAdapter(c_contentAdapter);
                                } else {

                                }

                            }
                        });

            }

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
        public void onBind(Comment data, int position, SparseBooleanArray selectedItems) {
            data=mcontent_data.get(position);
            c_nickname.setText(mcontent_data.get(position).getC_nickname());
            comment.setText(mcontent_data.get(position).getComment());
        }

        public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {
            this.onViewHolderItemClickListener = onViewHolderItemClickListener;
        }

    }
    public interface OnViewHolderItemClickListener {
        void onViewHolderItemClick();
    }
}
