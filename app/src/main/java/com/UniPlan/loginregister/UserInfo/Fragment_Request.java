package com.UniPlan.loginregister.UserInfo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.UniPlan.loginregister.MainActivity;
import com.UniPlan.loginregister.Notice_B.Image_zoom;
import com.UniPlan.loginregister.Notice_B.Post_write;
import com.UniPlan.loginregister.R;
import com.UniPlan.loginregister.Request;
import com.UniPlan.loginregister.Table;
import com.UniPlan.loginregister.adapters.CurriculumAdapter;
import com.UniPlan.loginregister.adapters.MultiImageAdapter;
import com.UniPlan.loginregister.curiList.Recycler_Data;
import com.UniPlan.loginregister.login.FirebaseID;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;


public class Fragment_Request extends Fragment implements MainActivity.IOnBackPressed {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();//사용자 정보 가져오기
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private DocumentReference docRef;
    private EditText mTitle, mContents;//제목, 내용
    private String p_nickname;//게시판에 표기할 닉네잉 //이게 가져온 값을 저장하는 임시 변수
    private TextView post_photo, post_tree, post_gallery;
    private ProgressBar post_progressBar;
    private String writer_id;
    private ImageView post_imageView;
    private File tempFile;
    private TextView post_save, btn_back;
    private static final int FROM_CAMERA = 1;
    private static final int FROM_GALLERY = 2;
    private Table choosedTable=null;
    private String forum_sort;
    private String image_url,token;
    private ArrayList<String> subscriber;
    private FirebaseStorage storage;
    private String imageFilePath;
    private Dialog addTreeDialog;
    private RecyclerView postAddTreeRV;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Recycler_Data> arrayList;
    private CurriculumAdapter curriculumAdapter;
    private ArrayList<Uri> uriList = new ArrayList<>();
    private RecyclerView photo_list;
    private MultiImageAdapter photoadapter;
    StorageReference storageReference;
    private ArrayList<String>image_urllist = new ArrayList<>();
    private View view;
    private TextView tv_back,tv_save;
    private EditText et_title,et_content;
    private FragmentTransaction ft;
    private FragmentManager fm;
    private AppCompatDialog progressDialog;

    public Fragment_Request() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_framgent__request, container, false);
        fm = getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        tv_back = view.findViewById(R.id.btn_back);
        et_title = view.findViewById(R.id.et_title);//제목 , item_post.xml의 변수와 혼동주의
        et_content=view.findViewById(R.id.et_content);
        tv_save= view.findViewById(R.id.tv_save);
        post_gallery=view.findViewById(R.id.post_gallery);
        photo_list=view.findViewById(R.id.photo_list);
        ((MainActivity)getActivity()).setBackPressedlistener(this);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.remove(Fragment_Request.this).commit();
                fm.popBackStack();
            }
        });


        photoadapter = new MultiImageAdapter(uriList, getContext());
        photo_list.setAdapter(photoadapter);
        photo_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new AppCompatDialog(getContext());
                Onprogress(getActivity(),"로딩중...");
                String title = et_title.getText().toString();
                String content = et_content.getText().toString();

                Request request = new Request(title,content,mAuth.getUid(),image_urllist);
                String mId = mStore.collection("request").document().getId();
                mStore.collection("request").document(mId).set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        ft.remove(Fragment_Request.this).commit();
                        fm.popBackStack();
                        progressOFF();
                    }
                });
            }
        });

        storage=FirebaseStorage.getInstance();

        storageReference=storage.getReferenceFromUrl("gs://login-6ba8f.appspot.com/");

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                token = s;
            }
        });

        TedPermission.with(getContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다")
                .setDeniedMessage("거부하셨습니다")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        if (mAuth.getCurrentUser() != null) {
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null) {
                                writer_id = (String) task.getResult().getData().get(FirebaseID.documentId);
                            }
                        }
                    });
        }

        post_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useGallery();
            }
        });
        return view;
    }

    private void useGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, FROM_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uriList.clear(); // 초기화한번해주고
        if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        } else {   // 이미지를 하나라도 선택한 경우
            if (data.getClipData() == null) {     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

            } else {      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();

                if (clipData.getItemCount() > 10) {   // 선택한 이미지가 11장 이상인 경우
                    Toast.makeText(getContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                } else {   // 선택한 이미지가 1장 이상 10장 이하인 경우

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.

                        } catch (Exception e) {
                        }
                    }
                }
            }


            //사진 스토리지에 업로드
            ((MainActivity) MainActivity.maincontext).Onprogress(getActivity(), "사진 업로드중");
            UploadPhoto(uriList, 0);

        }
    }

    PermissionListener permissionListener=new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getContext(),"권한이 거부됨",Toast.LENGTH_SHORT).show();
        }
    };

    private void UploadPhoto(ArrayList<Uri> uris,int n){

        int i=0;
        for(Uri uri:uris ) {
            Log.d("###", "Uri 는: " + uri);
            String filename = mAuth.getUid() + "_" + System.currentTimeMillis() + n;
            StorageReference ref = storageReference.child("request_image/" + filename + ".jpg");
            image_urllist.add(filename);
            image_url = filename;
            Log.d("###", filename);

            UploadTask uploadTask;
            uploadTask = ref.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    //Toast.makeText(getApplicationContext(),"업로드 실패",Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(getApplicationContext(),"업로드 성공",Toast.LENGTH_LONG).show();
                }
            });
            ++i;
            if(uris.size() ==i)((MainActivity)MainActivity.maincontext).progressOFF();
        }
    }

    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).setBackPressedlistener(null);
    }

    @Override
    public void onBackPressed() {
        ft.remove(Fragment_Request.this).commit();
        fm.popBackStack();
    }

    void Onprogress(Activity activity, String message){

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {

        } else {
            //이 밑부분 떼서 작업전에 AppcompatDialog 변수선언해주고 progressDialog 먼저 만들고
            // 작업시작할때 Onprogress 넣어주고 작업끝나면 밑에 progressOFF 넣어주면됩니다.
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();

        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}