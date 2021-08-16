package com.example.loginregister.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Notice_B.Post;
import com.example.loginregister.Notice_B.Post_Comment;
import com.example.loginregister.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class
PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> datas;//뒷부분 추가
    private Context mcontext;


    public interface  EventListener<QuerySnapshot>{
        boolean onOptionItemSelected(MenuItem item);

        void onItemClicked(int position);
    }


    public PostAdapter(Context mcontext, List<Post> datas) {//어댑터에 대한 생성자
        this.datas = datas;
        this.mcontext=mcontext;
    }

    @NonNull
    @Override//밑에 메소드들은 그냥 implement method한거입니다. 해야한대요
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {//아이템을 하나하나 보여주는 함수
        Post data=datas.get(position);//Post라는 모델객체를 하나 만든 이유
        holder.p_nickname.setText(datas.get(position).getP_nickname());
        holder.title.setText(datas.get(position).getTitle());//각각 데이터에 들어있는 제목 내용들이 각각 하나고 여러개가 아니기때문에
        holder.contents.setText(datas.get(position).getContents());//리스트로 만들어 주기 위해서
        holder.post_like_text.setText(datas.get(position).getLike());

        if ( !datas.get(position).getPost_photo().isEmpty()) {
            Picasso.get()
                    .load(datas.get(position).getPost_photo())
                    .into(holder.post_photo);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.ic_baseline_home_24)
                    .into(holder.post_photo);
        }

        final int posi=holder.getAdapterPosition();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context=v.getContext();
                if(posi!= RecyclerView.NO_POSITION){
                    Intent intent=new Intent(v.getContext(), Post_Comment.class);
                    intent.putExtra("title",datas.get(posi).getTitle());
                    intent.putExtra("content",datas.get(posi).getContents());
                    intent.putExtra("nickname",datas.get(posi).getP_nickname());
                    intent.putExtra("p_photo",datas.get(posi).getP_photo());
                    intent.putExtra("post_photo",datas.get(posi).getPost_photo());
                    intent.putExtra("uid",datas.get(posi).getDocumentId());//게시글 작성자의 uid를 넘겨줌
                    intent.putExtra("post_id",datas.get(posi).getPost_id());
                    intent.putExtra("number",datas.get(posi).getPost_num());//게시글의 넘버를 넘겨줌
                    intent.putExtra("position",posi);//게시글의 위치를 넘겨줌
                    //intent.putExtra("like",String.valueOf(datas.get(position).getLike()));
                    intent.putExtra("like",datas.get(posi).getLike());
                    intent.putExtra("writer_id",datas.get(posi).getWriter_id());//사용자의 uid
                    intent.putExtra("time",datas.get(posi).getDate());
                    //intent.putExtra("title",datas.get(pos).title);
                    mcontext.startActivity(intent);
                }
            }
        });
        //예를들면 첫째줄에 데이터에 위치를 각각 0번째 1번째...으로 받아서 그 위치마다 0번째 데이터위치에
        //0번째 제목, 0번째 내용 이런식으로 묶어서 리스트로 만들기 위해서 모델객체를 선언, holder가 그런 것을 지정해줌
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView contents;
        private TextView p_nickname;
        private ImageView post_photo;
        private TextView post_like_text;


        public PostViewHolder(@NonNull final View itemView) {//포스트 뷰홀더의 생성자
            super(itemView);
            title=itemView.findViewById(R.id.post_title);
            contents=itemView.findViewById(R.id.post_contents);
            p_nickname=itemView.findViewById(R.id.post_writer);
            post_photo=itemView.findViewById(R.id.post_imageView);
            post_like_text = itemView.findViewById(R.id.post_liketext);
        }
    }
}