package com.ajaygaikwad.mydiary;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.ajaygaikwad.mydiary.WebHelper.Config;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class AttachImage extends AppCompatActivity {

    ImageView imageView;
    String image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_image);
        imageView = findViewById(R.id.imageView);
        Intent in = getIntent();
        image = in.getStringExtra("Photo");


        try{
            Glide.with(getApplicationContext()).load(Config.ip_image_address +image).apply(new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.internet_error)).into(imageView);
        }catch (Exception e){}


    }
}
