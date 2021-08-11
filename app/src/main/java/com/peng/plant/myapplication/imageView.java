package com.peng.plant.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class imageView extends AppCompatActivity {

    String picData;
    private ImageView test_happy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        test_happy = findViewById(R.id.test_img);

        picData = getIntent().getStringExtra("image_data");

        Glide.with(getApplicationContext())
                .load(picData)
                .into(test_happy);
    }
}