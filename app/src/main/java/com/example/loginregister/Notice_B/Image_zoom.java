package com.example.loginregister.Notice_B;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loginregister.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.otaliastudios.zoom.ZoomLayout;

public class Image_zoom extends AppCompatActivity {

    ZoomLayout zoomLayout;
    ImageView zoom_image;
    String image_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

        zoom_image=findViewById(R.id.zoom_image);
        image_url=getIntent().getStringExtra("url");
        if (image_url != null) {
            Log.d("###", "image_url in Image_zoom : " + image_url);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            StorageReference pathReference = storageReference.child("post_image");
            if (pathReference == null) {
                Toast.makeText(Image_zoom.this, "해당 사진이 없습니다", Toast.LENGTH_SHORT).show();
            } else {
                StorageReference submitImage = storageReference.child("post_image/" + image_url + ".jpg");
                submitImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("###", String.valueOf(uri));
                        Glide.with(Image_zoom.this).load(uri).into(zoom_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("###","실패");
                    }
                });
            }
        }
    }
}