package com.example.loginregister.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kotlin.collections.UCollectionsKt;

public class
PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private String forum_sort;
    private List<Post> datas;//뒷부분 추가
    private Context mcontext;


    public interface  EventListener<QuerySnapshot>{
        boolean onOptionItemSelected(MenuItem item);

        void onItemClicked(int position);
    }


    public PostAdapter(Context mcontext, List<Post> datas) {//어댑터에 대한 생성자
        this.forum_sort=forum_sort;
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
        holder.post_writer.setText(datas.get(position).getP_nickname());
        holder.post_title.setText(datas.get(position).getTitle());//각각 데이터에 들어있는 제목 내용들이 각각 하나고 여러개가 아니기때문에
        holder.post_contents.setText(datas.get(position).getContents());//리스트로 만들어 주기 위해서
        holder.post_liketext.setText(datas.get(position).getLike());
        Timestamp timestamp = datas.get(position).getTimestamp();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.post_time.setText(simpleDateFormat.format(timestamp.toDate()));
        holder.post_click.setText(Integer.toString(datas.get(position).getClick()));

        final int posi=holder.getAdapterPosition();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(posi!= RecyclerView.NO_POSITION){
                    Intent intent=new Intent(v.getContext(), Post_Comment.class);
                    putData(intent,datas,posi);

                    if(datas.get(posi).getTable() == null){
                        intent.putExtra("isTreeExist", "no");
                    }
                    else intent.putExtra("isTreeExist", "yes");
                    Log.d("###","넘기는 이미지 유알엘 : "+datas.get(posi).getImage_url());
                    mcontext.startActivity(intent);
                }
            }
        });
        //예를들면 첫째줄에 데이터에 위치를 각각 0번째 1번째...으로 받아서 그 위치마다 0번째 데이터위치에
        //0번째 제목, 0번째 내용 이런식으로 묶어서 리스트로 만들기 위해서 모델객체를 선언, holder가 그런 것을 지정해줌
    }


    @Override
    public int getItemCount() {
        return null!=   datas ? datas.size():0;
    }

    class PostViewHolder extends RecyclerView.ViewHolder{
        private TextView post_title;
        private TextView post_contents;
        private TextView post_writer;
        private TextView post_liketext;
        private TextView post_time;
        private TextView post_click;


        public PostViewHolder(@NonNull final View itemView) {//포스트 뷰홀더의 생성자
            super(itemView);
            post_title=itemView.findViewById(R.id.post_title);
            post_contents=itemView.findViewById(R.id.post_contents);
            post_writer=itemView.findViewById(R.id.post_writer);
            post_liketext = itemView.findViewById(R.id.post_liketext);
            post_time = itemView.findViewById(R.id.post_time);
            post_click = itemView.findViewById(R.id.post_click);
        }
    }
    public Intent putData(Intent intent,List<Post> datas,int posi){
        intent.putExtra("forum_sort", datas.get(posi).getForum());
        intent.putExtra("post_id",datas.get(posi).getPost_id());

        return intent;

    }
}