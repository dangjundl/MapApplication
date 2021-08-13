package com.peng.plant.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.io.Serializable;
import java.util.ArrayList;

public class DisplayImage extends AppCompatActivity implements itemClickListener {

    private ArrayList<Piclist_data> arrayList;
    private recycler_adapter mAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        arrayList = new ArrayList<Piclist_data>();
        arrayList = (ArrayList<Piclist_data>) getIntent().getSerializableExtra("mapData");

        recyclerView = (RecyclerView) findViewById(R.id.pic_recyclerView);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new recycler_adapter(arrayList, DisplayImage.this, this);
        recyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onPicClicked(recycler_adapter.CustomViewHolder holder, int position, ArrayList<Piclist_data> pics) {
        Log.d("아이템 클릭", "onPicClicked: 넘어온 포지션값 : " + position);

        Intent intents = new Intent(DisplayImage.this, imageView.class);
        intents.putExtra("image_data", pics.get(position).getImg_path());
        startActivity(intents);

    }
}