package com.UniPlan.loginregister.Notice_B;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.UniPlan.loginregister.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.otaliastudios.zoom.ZoomLayout;

public class Image_zoom extends AppCompatActivity {

    ZoomLayout zoomLayout;
    ImageView zoom_image;
    Uri image_url;
    Button btn_store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

//        btn_store=findViewById(R.id.btn_store);
        zoom_image=findViewById(R.id.zoom_image);
        image_url=getIntent().getParcelableExtra("uri");

        if (image_url != null) {

            Glide.with(Image_zoom.this).load(image_url).into(zoom_image);

        }
//        btn_store.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                zoom_image.setDrawingCacheEnabled(true);
//                Bitmap bitmap = zoom_image.getDrawingCache();
//                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()), "");
//                Toast.makeText(Image_zoom.this,"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}