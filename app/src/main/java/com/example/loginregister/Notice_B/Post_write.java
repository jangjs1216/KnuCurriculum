package com.example.loginregister.Notice_B;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import com.example.loginregister.MainActivity;
import com.example.loginregister.Table;
import com.example.loginregister.login.FirebaseID;
import com.example.loginregister.R;
import com.example.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    private EditText mTitle, mContents;//제목, 내용
    private String p_nickname;//게시판에 표기할 닉네잉 //이게 가져온 값을 저장하는 임시 변수
    private Button post_photo;
    private ProgressBar post_progressBar;
    private String writer_id;
    private Uri uriProfileImage;
    private ImageView post_imageView;
    private File tempFile;
    private static final int CHOOSE_IMAGE = 101;
    private static final int FROM_CAMERA = 1;
    private static final int FROM_GALLERY = 2;
    private Table choosedTable=null;
    private String forum_sort;
    private Uri uri, imageUri;
    private String image_url;
    private ArrayList<String> subscriber;
    private FirebaseStorage storage;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("###", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

        mTitle = findViewById(R.id.Post_write_title);//제목 , item_post.xml의 변수와 혼동주의
        mContents = findViewById(R.id.Post_write_contents);
        post_photo = findViewById(R.id.Post_photo);
        post_imageView = findViewById(R.id.post_imageview);
        post_imageView.setVisibility(View.INVISIBLE);
        post_progressBar = findViewById(R.id.post_progressbar);

        Intent intent = getIntent();
        forum_sort = intent.getExtras().getString("게시판");
        storage=FirebaseStorage.getInstance();

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
        post_photo.setOnClickListener(view -> {
            Log.e("###","권한요청");
            TedPermission.with(getApplicationContext())
                    .setPermissionListener(permissionListener)
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .check();
            Log.e("###","선택");
            AlertDialog.Builder picBuilder = new AlertDialog.Builder(Post_write.this)
                    .setTitle("사진 첨부")
                    .setMessage("선택하세요")
                    .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            takePhoto();
                        }
                    })
                    .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            useGallery();
                        }
                    })
                    .setNeutralButton("Tree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 트리 사진 올리기
                        }
                    });
            AlertDialog alertDialog = picBuilder.create();
            alertDialog.show();
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
        String state = Environment.getExternalStorageState();
        Log.d("###", "들어옴 takephoto");
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(intent.resolveActivity(getPackageManager())!=null) {
                File photoFile=null;
                try {
                    photoFile=createImageFile();
                } catch (IOException e) {
                    // 실패
                }
                if (photoFile != null) {
                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageUri = providerURI;

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, FROM_CAMERA);
                }
            }
        } else {
            Toast.makeText(this, "접근이 불가합니다", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "picture");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        currentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void galleryAddPic(){
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void SavePost(View view)
    {
        Log.d("###", "SavePost진입");
        if(image_url==null && uri!=null)
        {
            Toast.makeText(Post_write.this,"사진 업로드 중입니다",Toast.LENGTH_SHORT).show();
        }
        else {
            if (mAuth.getCurrentUser() != null) {
                String PostID = mStore.collection(forum_sort).document().getId();//제목이 같아도 게시글이 겹치지않게
                final Post[] post = new Post[1];
                DocumentReference docRef1 = mStore.collection("user").document(mAuth.getUid());
                docRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);

                        long datetime = System.currentTimeMillis();
                        Date date = new Date(datetime);
                        Timestamp timestamp = new Timestamp(date);
                        subscriber = new ArrayList<>();
                        subscriber.add(mAuth.getUid());
                        post[0] = new Post(mAuth.getUid(), mTitle.getText().toString(), mContents.getText().toString(), userAccount.getNickname(), "0", timestamp, PostID, new ArrayList<>(), 0, 0, 0, image_url,forum_sort, choosedTable,subscriber);
                        mStore.collection(forum_sort).document(PostID).set(post[0]);
                        FirebaseMessaging.getInstance().subscribeToTopic(PostID)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.e("댓글 생성", " 구독성공");
                                    } else {
                                        Log.e("댓글 생성", " 구독실패");
                                    }
                                });
                    }
                });
                finish();
            }
        }
    }

    public String getPath(Uri uri){
        String [] proj={MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader=new CursorLoader(this,uri,proj,null,null,null);
        Cursor cursor=cursorLoader.loadInBackground();
        int index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
            try {
                Log.i("REQUEST_TAKE_PHOTO", "OK");
                galleryAddPic();
                post_imageView.setImageURI(imageUri);
            } catch (Exception e) {
                Log.e("REQUEST_TAKE_PHOTO", e.toString());
            }
        } else {
            Toast.makeText(Post_write.this, "취소하였습니다", Toast.LENGTH_SHORT).show();
        }
        StorageReference storageReference=storage.getReferenceFromUrl("gs://login-6ba8f.appspot.com/");
        Log.d("###", "Uri 는: "+uri);
        Uri file=Uri.fromFile(new File(getPath(uri)));
        Log.d("###", "Uri file은: "+file);
        String filename=mAuth.getUid()+"_"+System.currentTimeMillis();
        StorageReference ref=storageReference.child("post_image/"+filename+".jpg");
        image_url=filename;
        Log.d("###",filename);
        ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                final Task<Uri> imageUrl=task.getResult().getStorage().getDownloadUrl();
                while(!imageUrl.isComplete());
//                image_url=imageUrl.getResult().toString();
//                Log.d("###","image_url : " + image_url);
//                Log.d("###","imageUrl : " + imageUrl);
            }
        });
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

}