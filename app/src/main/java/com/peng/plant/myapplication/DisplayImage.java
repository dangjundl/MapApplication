package com.peng.plant.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.io.Serializable;
import java.util.ArrayList;

public class DisplayImage extends AppCompatActivity {

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

        Log.d("하하하", "onCreate: 넘어온거 제대로왔나"+arrayList.size());
        Log.d("하하하", "onCreate: 넘어온거 제대로왔나"+arrayList.get(0).getName());


        recyclerView = (RecyclerView)findViewById(R.id.pic_recyclerView);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new recycler_adapter(arrayList,DisplayImage.this);
        recyclerView.setAdapter(mAdapter);



    }
}