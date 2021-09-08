package com.example.loginregister.Notice_B;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginregister.Table;
import com.example.loginregister.R;
import com.example.loginregister.adapters.CurriculumAdapter;
import com.example.loginregister.curiList.Recycler_Adapter;
import com.example.loginregister.curiList.Recycler_Data;
import com.example.loginregister.login.FirebaseID;
import com.example.loginregister.login.UserAccount;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post_Update extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();//사용자 정보 가져오기
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private DocumentReference docRef;
    private EditText mTitle, mContents;//제목, 내용
    private String p_nickname;//게시판에 표기할 닉네잉 //이게 가져온 값을 저장하는 임시 변수
    private TextView post_photo, post_tree, post_gallery;
    private ProgressBar post_progressBar;
    private String writer_id, post_id, post_num, comment_post, like, title, content;
    private Timestamp timestamp;
    private ImageView post_imageView;
    private File tempFile;
    private TextView post_save, btn_back;
    private static final int FROM_CAMERA = 1;
    private static final int FROM_GALLERY = 2;
    private Table choosedTable=null;
    private String forum_sort;
    private Uri uri;
    private int commnet_num;
    private Table table;
    private String image_url;
    private ArrayList<String> subscriber;
    private ArrayList<Comment> comments=new ArrayList<>();
    private FirebaseStorage storage;
    private String imageFilePath;
    private Dialog addTreeDialog;
    private RecyclerView postAddTreeRV;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Recycler_Data> arrayList;
    private CurriculumAdapter curriculumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("###", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

        mTitle = findViewById(R.id.Post_write_title);//제목 , item_post.xml의 변수와 혼동주의
        mContents = findViewById(R.id.Post_write_contents);
        post_imageView = findViewById(R.id.post_imageview);
        post_imageView.setVisibility(View.INVISIBLE);
        post_progressBar = findViewById(R.id.post_progressbar);
        post_save=findViewById(R.id.post_save);
        btn_back=findViewById(R.id.btn_back);
        post_photo=findViewById(R.id.post_photo);
        post_tree=findViewById(R.id.post_tree);
        post_gallery=findViewById(R.id.post_gallery);

        Intent intent=getIntent();
        post_id=intent.getStringExtra("post_id");
        forum_sort=intent.getStringExtra("forum_sort");

        storage=FirebaseStorage.getInstance();

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다")
                .setDeniedMessage("거부하셨습니다")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        if(mAuth.getCurrentUser()!=null){//UserInfo에 등록되어있는 닉네임을 가져오기 위해서

            DocumentReference docRef2 = mStore.collection(forum_sort).document(post_id);
            docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Post post = documentSnapshot.toObject(Post.class);
                    writer_id = post.getWriter_id();
                    p_nickname = post.getP_nickname();
                    like = post.getLike();
                    timestamp = post.getTimestamp();
                    comments = post.getComments();
                    commnet_num=post.getcoment_Num();
                    image_url=post.getImage_url();
                    Log.d("###","image_url in update : "+image_url);
                    table = post.getTable();
                    subscriber = post.getSubscriber();
                    title=post.getTitle();
                    content=post.getContents();

                    mTitle.setText(title);
                    mContents.setText(content);

                    if (image_url != null) {
                        Log.d("###", "image_url : " + image_url);
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReference();
                        StorageReference pathReference = storageReference.child("post_image");
                        if (pathReference == null) {
                            Toast.makeText(Post_Update.this, "해당 사진이 없습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("###", "최종 사진 주소 : " + "post_image/" + image_url + ".jpg");
                            StorageReference submitImage = storageReference.child("post_image/" + image_url + ".jpg");
                            submitImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("###", String.valueOf(uri));
                                    Glide.with(Post_Update.this).load(uri).into(post_imageView);
                                    post_imageView.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 실패
                                }
                            });
                        }
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

        post_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        post_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useGallery();
            }
        });

        post_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTreeDialog = new Dialog(Post_Update.this);
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
        startActivityForResult(intent, FROM_GALLERY);
    }

    private void setImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        post_imageView.setVisibility(View.VISIBLE);
        post_imageView.setImageBitmap(originalBm);
    }

    private void takePhoto() {
        Log.e("###","takePhoto");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null) {
            File photoFile=null;
            try{
                photoFile=createImageFile();
            } catch (IOException e) {

            }
            if(photoFile!=null) {
                uri=FileProvider.getUriForFile(getApplicationContext(),getPackageName(),photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(intent,FROM_CAMERA);
            }
        }
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
        if(image_url==null && uri!=null)
        {
            Toast.makeText(Post_Update.this,"사진 업로드 중입니다",Toast.LENGTH_SHORT).show();
        }
        else {
            if (mAuth.getCurrentUser() != null) {
                Post post=new Post(writer_id, mTitle.getText().toString(), mContents.getText().toString(), p_nickname, like, timestamp, post_id, comments, commnet_num, image_url, forum_sort, table, subscriber);
                mStore.collection(forum_sort).document(post_id).set(post);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK) {
            return;
        }
        else if (requestCode == FROM_GALLERY) {
            uri = data.getData();
            Log.d("###", "첫번째 uri : "+String.valueOf(uri));
            post_imageView.setImageURI(uri);
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                assert uri != null;
                cursor = getContentResolver().query(uri, proj, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                tempFile = new File(cursor.getString(column_index));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();
        } else if (requestCode == FROM_CAMERA) {
            if(resultCode==RESULT_OK) {
                Bitmap bitmap=BitmapFactory.decodeFile(imageFilePath);
                ExifInterface exif=null;
                try {
                    exif=new ExifInterface(imageFilePath);
                } catch(IOException e) {
                    e.printStackTrace();
                }
                int exifOrientation;
                int exifDegree;
                if(exif!=null) {
                    exifOrientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
                    exifDegree=exifOrientationDegrees(exifOrientation);
                } else {
                    exifDegree=0;
                }
                post_imageView.setImageBitmap(rotate(bitmap,exifDegree));
                post_imageView.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this,"취소되었습니다",Toast.LENGTH_LONG).show();
            }
        }
        StorageReference storageReference=storage.getReferenceFromUrl("gs://login-6ba8f.appspot.com/");
        Log.d("###", "Uri 는: "+uri);
        String filename=mAuth.getUid()+"_"+System.currentTimeMillis();
        StorageReference ref=storageReference.child("post_image/"+filename+".jpg");
        image_url=filename;
        Log.d("###",filename);
        ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                final Task<Uri> imageUrl=task.getResult().getStorage().getDownloadUrl();
                while(!imageUrl.isComplete());
                image_url=imageUrl.getResult().toString();
            }
        });
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
}