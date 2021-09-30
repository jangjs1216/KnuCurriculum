package com.UniPlan.loginregister.Notice_B;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.UniPlan.loginregister.MainActivity;
import com.UniPlan.loginregister.Table;
import com.UniPlan.loginregister.adapters.CurriculumAdapter;
import com.UniPlan.loginregister.adapters.MultiImageAdapter;
import com.UniPlan.loginregister.curiList.Recycler_Data;
import com.UniPlan.loginregister.login.FirebaseID;
import com.UniPlan.loginregister.login.UserAccount;
import com.UniPlan.loginregister.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.apache.log4j.chainsaw.Main;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Post_write extends AppCompatActivity {

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
   ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("###", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

        mTitle = findViewById(R.id.Post_write_title);//제목 , item_post.xml의 변수와 혼동주의
        mContents = findViewById(R.id.Post_write_contents);
        post_save=findViewById(R.id.post_save);
        btn_back=findViewById(R.id.btn_back);
        post_photo=findViewById(R.id.post_photo);
        post_tree=findViewById(R.id.post_tree);
        post_gallery=findViewById(R.id.post_gallery);
        photo_list  =findViewById(R.id.photo_list);

        Intent intent = getIntent();
        forum_sort = intent.getExtras().getString("게시판");
        storage=FirebaseStorage.getInstance();

        storageReference=storage.getReferenceFromUrl("gs://login-6ba8f.appspot.com/");

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                token = s;
            }
        });

        TedPermission.with(getApplicationContext())
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
                                Log.d("확인", "현재 사용자 uid입니다:" + writer_id);
                            }
                        }
                    });
        }
        // 사진올리기
//        post_photo.setOnClickListener(view -> {
//            Log.e("###","선택");
//            AlertDialog.Builder picBuilder = new AlertDialog.Builder(Post_write.this)
//                    .setTitle("사진 첨부")
//                    .setMessage("선택하세요")
//                    .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            takePhoto();
//                        }
//                    })
//                    .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            useGallery();
//                        }
//                    });
//            AlertDialog alertDialog = picBuilder.create();
//            alertDialog.show();
//        });

        post_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useGallery();
            }
        });

        post_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTreeDialog = new Dialog(Post_write.this);
                addTreeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                addTreeDialog.setContentView(R.layout.dialog_postaddtree);
                showDialog();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 게시글 올리기
        post_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavePost();
            }
        });
    }

    private void useGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, FROM_GALLERY);
    }

    private File createImageFile() throws IOException {
        String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName="TEST_"+timestamp+"_";
        File storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath=image.getAbsolutePath();
        return image;
    }

    public void SavePost()
    {
        Log.d("###", "SavePost진입");


            if (mAuth.getCurrentUser() != null) {
                String PostID = mStore.collection(forum_sort).document().getId();//제목이 같아도 게시글이 겹치지않게
                final Post[] post = new Post[1];
                DocumentReference docRef1 = mStore.collection("user").document(mAuth.getUid());
                docRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);

                        ArrayList<String> mmpost = new ArrayList<>();
                        if(userAccount.getMypost()!= null){mmpost=userAccount.getMypost();}
                        mmpost.add(PostID);
                        Map map1 = new HashMap<String, ArrayList<String>>();
                        map1.put("mypost",mmpost);
                        mStore.collection("user").document(mAuth.getUid()).set(map1, SetOptions.merge());

                        long datetime = System.currentTimeMillis();
                        Date date = new Date(datetime);
                        Timestamp timestamp = new Timestamp(date);
                        subscriber = new ArrayList<>();
                        subscriber.add(token);
                        post[0] = new Post(mAuth.getUid(), mTitle.getText().toString(), mContents.getText().toString(), userAccount.getNickname(), "0", timestamp, PostID, new ArrayList<>(), 0, image_urllist,forum_sort, choosedTable,subscriber, 0,token);
                        mStore.collection(forum_sort).document(PostID).set(post[0]);


                    }
                });
                finish();
            }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode!=RESULT_OK) {
//            return;
//        }
//        else if (requestCode == FROM_GALLERY) {
//            uri = data.getData();
//            Log.d("###", "첫번째 uri : "+String.valueOf(uri));
//            post_imageView.setImageURI(uri);
//            Cursor cursor = null;
//            try {
//                String[] proj = {MediaStore.Images.Media.DATA};
//                assert uri != null;
//                cursor = getContentResolver().query(uri, proj, null, null, null);
//                assert cursor != null;
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                tempFile = new File(cursor.getString(column_index));
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//            setImage();
//        } else if (requestCode == FROM_CAMERA) {
//            if(resultCode==RESULT_OK) {
//                Bitmap bitmap=BitmapFactory.decodeFile(imageFilePath);
//                ExifInterface exif=null;
//                try {
//                    exif=new ExifInterface(imageFilePath);
//                } catch(IOException e) {
//                    e.printStackTrace();
//                }
//                int exifOrientation;
//                int exifDegree;
//                if(exif!=null) {
//                    exifOrientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
//                    exifDegree=exifOrientationDegrees(exifOrientation);
//                } else {
//                    exifDegree=0;
//                }
//                post_imageView.setImageBitmap(rotate(bitmap,exifDegree));
//                post_imageView.setVisibility(View.VISIBLE);
//            } else {
//                Toast.makeText(this,"취소되었습니다",Toast.LENGTH_LONG).show();
//            }
//        }

        uriList.clear(); // 초기화한번해주고
        if(data == null){   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else{   // 이미지를 하나라도 선택한 경우
            if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

            }
            else{      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();

                if(clipData.getItemCount() > 10){   // 선택한 이미지가 11장 이상인 경우
                    Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else{   // 선택한 이미지가 1장 이상 10장 이하인 경우

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.

                        } catch (Exception e) {
                        }
                    }
                }
            }

            photoadapter = new MultiImageAdapter(uriList, getApplicationContext());
            photo_list.setAdapter(photoadapter);
            photo_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

            //사진 스토리지에 업로드
            ((MainActivity)MainActivity.maincontext).Onprogress(Post_write.this,"사진 업로드중");
            UploadPhoto(uriList,0);


            photoadapter.setOnItemClickListener(new MultiImageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {

                    Intent intent=new Intent(Post_write.this,Image_zoom.class);
                    intent.putExtra("uri",uriList.get(pos));
                    startActivity(intent);
                }
            });
        }

//        ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                final Task<Uri> imageUrl=task.getResult().getStorage().getDownloadUrl();
//                while(!imageUrl.isComplete());
//                image_url=imageUrl.getResult().toString();
//            }
//        });
    }

    private void UploadPhoto(ArrayList<Uri> uris,int n){

        int i=0;
        for(Uri uri:uris ) {
            Log.d("###", "Uri 는: " + uri);
            String filename = mAuth.getUid() + "_" + System.currentTimeMillis() + n;
            StorageReference ref = storageReference.child("post_image/" + filename + ".jpg");
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



    private int exifOrientationDegrees(int exifOrientation) {
        if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap,float degree) {
        Matrix matrix=new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    PermissionListener permissionListener=new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(),"권한이 거부됨",Toast.LENGTH_SHORT).show();
        }
    };

    public void showDialog() {
        addTreeDialog.show();

        docRef = mStore.collection("user").document(mAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);

                ArrayList<Table> tables = userAccount.getTables();

                postAddTreeRV = addTreeDialog.findViewById(R.id.treeListRV);
                linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                postAddTreeRV.setLayoutManager(linearLayoutManager);
                arrayList = new ArrayList<>();
                for(String tableName : userAccount.getTableNames()){
                    Recycler_Data recycler_data = new Recycler_Data(tableName);
                    arrayList.add(recycler_data);
                }
                curriculumAdapter = new CurriculumAdapter(arrayList);
                postAddTreeRV.setAdapter(curriculumAdapter);

                //리싸이클러뷰 클릭 리스너
                curriculumAdapter.setOnItemListener(new CurriculumAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos, String option) {
                        if(option.equals("choice")){
                            String tableName = arrayList.get(pos).getTv_title().toString();
                            Toast.makeText(getApplicationContext(), tableName + " 선택됨", Toast.LENGTH_LONG).show();

                            choosedTable = tables.get(pos);
                            addTreeDialog.dismiss();
                        }
                    }
                });
            }
        });
    }


//    void Onprogress(Activity activity, String message){
//
//        if (activity == null || activity.isFinishing()) {
//            return;
//        }
//
//
//        if (progressDialog != null && progressDialog.isShowing()) {
//
//        } else {
//            //이 밑부분 떼서 작업전에 AppcompatDialog 변수선언해주고 progressDialog 먼저 만들고
//            // 작업시작할때 Onprogress 넣어주고 작업끝나면 밑에 progressOFF 넣어주면됩니다.
//            progressDialog = new ProgressDialog(activity);
//            progressDialog.setCancelable(false);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//            progressDialog.setContentView(R.layout.progress_loading);
//            progressDialog.show();
//
//        }
//
//
//        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
//        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
//        img_loading_frame.post(new Runnable() {
//            @Override
//            public void run() {
//                frameAnimation.start();
//            }
//        });
//
//        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
//        if (!TextUtils.isEmpty(message)) {
//            tv_progress_message.setText(message);
//        }
//
//    }
//
//    public void progressOFF() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//    }

}