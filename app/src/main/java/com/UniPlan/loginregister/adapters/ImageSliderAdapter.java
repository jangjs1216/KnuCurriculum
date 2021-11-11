package com.UniPlan.loginregister.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.UniPlan.loginregister.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Uri> sliderImage = new ArrayList<>();

    public ImageSliderAdapter(Context context, ArrayList<Uri> sliderImage) {
        this.context = context;
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_image_zoom, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindSliderImage(sliderImage.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderImage.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.zoom_image);
        }

        public void bindSliderImage(Uri imageURL) {
            Glide.with(context)
                    .load(imageURL)
                    .into(mImageView);
        }
    }
}