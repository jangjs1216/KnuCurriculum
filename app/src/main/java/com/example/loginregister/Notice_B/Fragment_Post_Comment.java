package com.example.loginregister.Notice_B;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loginregister.MainActivity;
import com.example.loginregister.R;
import com.example.loginregister.UserInfo.Fragment_Edit_User_Info;
import com.example.loginregister.adapters.PostCommentAdapter;
import com.example.loginregister.login.FirebaseID;
import com.example.loginregister.push_alram.Msg;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment_Post_Comment extends Fragment {
    private View view;
    SharedPreferences.Editor prefEditor;
    SharedPreferences prefs;
    FirebaseUser user;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FragmentManager fm;
    private FragmentTransaction ft;
    private TextView com_title;
    private TextView com_text;
    private TextView com_nick;
    private ImageView com_photo;
    private ImageView url_image; // 게시글 이미지
    private String forum_sort;
    private ImageView btn_comment;
    private PostCommentAdapter contentAdapter;
    private RecyclerView mCommentRecyclerView;
    private List<Comment> mcontent;
    private EditText com_edit;
    private String comment_p, post_t, post_num, comment_post;//

    private Button treeButton; //[ 장준승 ] 트리 보여주기 버튼
    private ImageView likeButton; //좋아요 버튼
    private TextView likeText; //좋아요 갯수보여주는 텍스트 이번엔 다르다
    String P_comment_id;
    private ArrayList<Comment> Cdata;
    private Integer ll;
    SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    public boolean Compared_c = true;
    private ArrayList<String> subs,Liked = new ArrayList<>();
    private MenuItem subscribe;
    private String photoUrl, post_id, writer_id_post, current_user, image_url, isTreeExist;
    private Boolean isChecked,isLiked;
    private Post post;

    public Fragment_Post_Comment(Post post) {
        this.post = post;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment__post__comment, container, false);
        fm=getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();

        //게시글 기본설정
        forum_sort=((NoticeBoard)getActivity()).getForum_sort();
        ll= new Integer(0);
        btn_comment = (ImageView)view.findViewById(R.id.btn_comment);
        com_nick = (TextView) view.findViewById(R.id.Comment_nickname);          //본문 작성자
        com_title = (TextView) view.findViewById(R.id.Comment_title);            //제목
        com_text = (TextView) view.findViewById(R.id.Comment_text);              //본문
        com_edit = (EditText) view.findViewById(R.id.Edit_comment);              //댓글 작성 내용 입력창
        url_image = (ImageView) view.findViewById(R.id.url_image);               //작성자가 올린 이미지
        treeButton = (Button) view.findViewById(R.id.btn_post_treeview);         //트리 보여주는 버튼
        likeButton = (ImageView) view.findViewById(R.id.like_button);            //좋아요 버튼
        likeText = (TextView) view.findViewById(R.id.like_text);                 //좋아요 개수 보여주는 텍스트
        mCommentRecyclerView = view.findViewById(R.id.comment_recycler);         //코멘트 리사이클러뷰

        //      기본내용 세팅
        com_nick.setText(post.getP_nickname());
        com_title.setText(post.getTitle());
        com_text.setText(post.getContents());
        likeText.setText(post.getLike());
        subs = post.getSubscriber();
        writer_id_post = post.getWriter_id();
        image_url=post.getImage_url();

        //      알람여부 확인
        if(subs.contains(mAuth.getUid())){
            subscribe.setIcon(R.drawable.ic_baseline_notifications_active_24);
            isChecked = true;
        }
        else {
            subscribe.setIcon(R.drawable.ic_baseline_notifications_off_24);
            isChecked = false;
        }

        //좋아요 여부 확인
        mStore.collection("user").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Liked  =(ArrayList<String>) documentSnapshot.get(FirebaseID.Liked);
            }
        });

        if(Liked!=null) {
            isLiked = Liked.contains(post_id);
            if (isLiked)
                likeButton.setImageResource(R.drawable.ic_baseline_favorite_24);
            else
                likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
        else {
            likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            isLiked = false;
        }

        //      트리여부확인
        if(post.getTable() == null){
            isTreeExist ="no";
        }
        else isTreeExist = "yes";

        if(isTreeExist.equals("yes")){
            treeButton.setVisibility(View.VISIBLE);
        }

        //      이미지여부확인
        if(image_url!=null)
        {
            Log.d("###","image_url : "+image_url);
            FirebaseStorage storage=FirebaseStorage.getInstance();
            StorageReference storageReference=storage.getReference();
            StorageReference pathReference=storageReference.child("post_image");
            if(pathReference==null) {
                Toast.makeText(getActivity(),"해당 사진이 없습니다",Toast.LENGTH_SHORT).show();
            } else {
                Log.d("###","최종 사진 주소 : "+"post_image/"+image_url+".jpg");
                StorageReference submitImage=storageReference.child("post_image/"+image_url+".jpg");
                submitImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("###", String.valueOf(uri));
                        Glide.with(getActivity()).load(uri).into(url_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 실패
                    }
                });
            }
        }


        //      툴바
        toolbar = (Toolbar)view.findViewById(R.id.tb_post_comment);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로가기 기능생성
        //툴바 끝

        //      댓글 입력 버튼
        view.findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Compared_c) { // 댓글
                    if (mAuth.getCurrentUser() != null) {
                        DocumentReference docRef = mStore.collection(forum_sort).document(post_id);
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                //      댓글달기전에  변경사항 있는지 확인
                                post = documentSnapshot.toObject(Post.class);
                                ArrayList<Comment> data = new ArrayList<>();

                                if(post.getComments() !=null) {
                                    data = post.getComments();
                                }

                                Comment cur_comment = new Comment();

                                int Csize = post.getcoment_Num();

                                cur_comment.setComment(com_edit.getText().toString());
                                cur_comment.setC_nickname(comment_p);
                                cur_comment.setDocumentId(mAuth.getCurrentUser().getUid());
                                cur_comment.setComment_id(Integer.toString( (1+Csize)*100 ));

                                if(Csize+1 >=100)
                                {
                                    Toast.makeText(getActivity(), "댓글수 제한 100개을 넘었습니다",Toast.LENGTH_LONG).show();
                                    return;
                                }

                                data.add(cur_comment);
                                Collections.sort(data);
                                post.setComments(data);
                                post.setCur_comment(post.getCur_comment()+1);
                                subs = post.getSubscriber();

                                if(!subs.contains(mAuth.getUid())){
                                    subs.add(mAuth.getUid());
                                    post.setSubscriber(subs);
                                }
                                Log.e("TLqkf",post.getSubscriber().toString());
                                mStore.collection(forum_sort).document(post_id).set(post);
                                String mId = mStore.collection("message").document().getId();

                                long datetime = System.currentTimeMillis();
                                Date date = new Date(datetime);
                                Timestamp timestamp = new Timestamp(date);
                                Msg msg = new Msg(forum_sort,post_id,mAuth.getUid(),timestamp);
                                mStore.collection("message").document(mId).set(msg);



                                getActivity().getCurrentFocus();//작성버튼을 누르면 에딧텍스트 키보드 내리게 하기

                                if (view != null) {//댓글작성시 키보드 내리고 댓글에 작성한 내용 초기화

                                    InputMethodManager hide = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    hide.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    com_edit.setText("");
                                }

                            }
                        });
                    }
                }
                else if(P_comment_id != null) { // 대댓글
                    if (mAuth.getCurrentUser() != null) {//새로 Comment란 컬렉션에 넣어줌
                        DocumentReference docRef = mStore.collection(forum_sort).document(post_id);
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Post post = documentSnapshot.toObject(Post.class);
                                ArrayList<Comment> data = new ArrayList<>();
                                int Csize = 1;

                                if(post.getComments() !=null) {
                                    data = post.getComments();

                                    for(int i=0;i<data.size();++i){
                                        Log.e("&&&",data.get(i).getComment_id());
                                        if((data.get(i).getComment_id()).equals(P_comment_id)){
                                            Csize=+1+data.get(i).getCcoment_Num();
                                            Log.e("&&&",P_comment_id+' '+Integer.toString(Csize));
                                        }
                                        data.get(i).setCcoment_Num(Csize);
                                    }
                                }

                                if(Csize >=100)
                                {
                                    Toast.makeText(getActivity(), "대댓글수 제한 100개을 넘었습니다",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Comment cur_comment = new Comment();


                                cur_comment.setComment(com_edit.getText().toString());
                                cur_comment.setC_nickname(comment_p);
                                cur_comment.setDocumentId(mAuth.getCurrentUser().getUid());
                                cur_comment.setComment_id(Integer.toString( (Integer.parseInt(P_comment_id)) + Csize  ));



                                data.add(cur_comment);
                                Collections.sort(data);

                                post.setComments(data);
                                post.setCur_comment(post.getCur_comment()+1);
                                if(!subs.contains(mAuth.getUid())){
                                    subs.add(mAuth.getUid());
                                    post.setSubscriber(subs);
                                }
                                mStore.collection(forum_sort).document(post_id).set(post);

                                String mId = mStore.collection("message").document().getId();

                                long datetime = System.currentTimeMillis();
                                Date date = new Date(datetime);
                                Timestamp timestamp = new Timestamp(date);
                                Msg msg = new Msg( forum_sort,post_id,mAuth.getUid(),timestamp);
                                mStore.collection("message").document(mId).set(msg);


                                View view = getActivity().getCurrentFocus();//작성버튼을 누르면 에딧텍스트 키보드 내리게 하기

                                if (view != null) {//댓글작성시 키보드 내리고 댓글에 작성한 내용 초기화

                                    InputMethodManager hide = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    hide.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    com_edit.setText("");
                                }
                            }
                        });
                    }
                    Compared_c=true;
                }
                fragrefresh();
            }
        });//댓글 입력 버튼

        //새로고침 리싸이클러뷰
        swipeRefreshLayout=view.findViewById(R.id.refresh_commnet);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onStart();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        treeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Post_Treeview.class);
                intent.putExtra("writerID", writer_id_post);
                intent.putExtra("forumID", forum_sort);
                intent.putExtra("postID", post_id);
                intent.putExtra("writerNickname", com_nick.getText().toString());
                startActivity(intent); //게시글 수정`
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStore.collection(forum_sort).document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ll = Integer.parseInt(task.getResult().get("like").toString());

                        if(isLiked){
                            Liked.remove(post_id);
                            likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                            ll--;
                        }
                        else{
                            Liked.add(post_id);
                            likeButton.setImageResource(R.drawable.ic_baseline_favorite_24);
                            ll++;
                        }
                        isLiked= !isLiked;
                        likeText.setText(Integer.toString(ll));
                        Map map1 = new HashMap<String, ArrayList<String>>();
                        map1.put(FirebaseID.Liked,Liked);
                        mStore.collection("user").document(mAuth.getUid()).set(map1, SetOptions.merge());
                        Map map2 = new HashMap<String,String>();
                        map2.put(FirebaseID.like,Integer.toString(ll));
                        Log.e("Post_Comment",Integer.toString(ll));
                        mStore.collection(forum_sort).document(post_id).set(map2, SetOptions.merge());
                    }
                });

            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_post_comment,menu);
        subscribe= menu.findItem(R.id.action_btn_notification);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//게시글 작성자와 현재 사용자와의 uid가 같으면 기능 수행가능하게

        switch (item.getItemId()) {
            case R.id.action_btn_delete:
                if (mAuth.getCurrentUser().getUid().equals(writer_id_post)) {

                    mStore.collection(forum_sort).document(post_id)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Log.d("확인", "삭제되었습니다.");
                                    ft.remove(Fragment_Post_Comment.this).commit();
                                    fm.popBackStack();
                                }
                            });
                } else {

                }
                break;
            case R.id.action_btn_modify:
                if (writer_id_post.equals(mAuth.getCurrentUser().getUid())) {
                    modify();
                    fragrefresh();

                } else {
                    Toast.makeText(getActivity(), "작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_btn_notification:
                if(isChecked){
                    Log.e("Post_Comment","알람해제");
                    isChecked=!isChecked;
                    item.setIcon(R.drawable.ic_baseline_notifications_off_24);

                    mStore.collection(forum_sort).document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                post = task.getResult().toObject(Post.class);
                                subs = post.getSubscriber();
                                subs.remove(mAuth.getUid());
                                post.setSubscriber(subs);
                                mStore.collection(forum_sort).document(post.getPost_id()).set(post);
                            }
                        }
                    });
                }
                else{
                    Log.e("Post_Comment","알람설정");
                    isChecked=!isChecked;
                    item.setIcon(R.drawable.ic_baseline_notifications_active_24);
                    mStore.collection(forum_sort).document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                post = task.getResult().toObject(Post.class);
                                subs = post.getSubscriber();
                                subs.add(mAuth.getUid());
                                post.setSubscriber(subs);
                                mStore.collection(forum_sort).document(post.getPost_id()).set(post);
                            }
                        }
                    });
                }
            case android.R.id.home:
                //뒤로가기버튼
                ft.remove(Fragment_Post_Comment.this).commit();
                fm.popBackStack();
                break;

        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        Cdata=new ArrayList<Comment>();
        DocumentReference docRef = mStore.collection(forum_sort).document(post_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
                Cdata.clear();
                Cdata = post.getComments();
                contentAdapter = new PostCommentAdapter(Cdata, getActivity());//mDatas라는 생성자를 넣어줌
                mCommentRecyclerView.setAdapter(contentAdapter);
            }
        });
    }

    public void compared(String comment_id) {
        Compared_c = false;
        com_edit.setHint("대댓글 작성하기");
        P_comment_id = comment_id;
    }
    public void fragrefresh(){
        ft.detach(this).attach(this).commit();
    }

    public void modify(){
        Fragment_Post_Update frag = new Fragment_Post_Update(post.getTitle(),post.getContents());
        frag.setModifyListener(new Fragment_Post_Update.ModifyListener() {
            @Override
            public void onPositiveClicked(String title, String content) {
                post.setTitle(title);
                post.setContents(content);
                mStore.collection(forum_sort).document(post_id).set(post);
                fragrefresh();
            }
        });

        ft.replace(R.id.main_board,frag);
        ft.addToBackStack(null);
        ft.commit();
    }


}