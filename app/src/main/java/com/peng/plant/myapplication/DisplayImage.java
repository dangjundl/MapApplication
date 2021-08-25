package com.peng.plant.myapplication;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.peng.plant.myapplication.adapter.recyclerAdapter;
import com.peng.plant.myapplication.data.Piclist_data;
import com.peng.plant.myapplication.listener.itemClickListener;
import com.peng.plant.myapplication.listener.recyclerViewScrollController;
import com.peng.plant.myapplication.view.imageView;

import java.util.ArrayList;

public class DisplayImage extends AppCompatActivity implements itemClickListener, recyclerViewScrollController.ScrollListener{

    private ArrayList<Piclist_data> arrayList;
    private recyclerAdapter mAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private recyclerViewScrollController mScrollController;
//    private SnapHelper snapHelper;
    private int sensor_statistic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        arrayList = new ArrayList<Piclist_data>();
        arrayList = (ArrayList<Piclist_data>) getIntent().getSerializableExtra("mapData");

        mScrollController = new recyclerViewScrollController(getApplicationContext(), this);


        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.pic_recyclerView);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.hasFixedSize();
//        snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);

        mAdapter = new recyclerAdapter(arrayList, DisplayImage.this, this);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onTilt(float x, float y) {

            recyclerView.smoothScrollBy((int) x * 623, 0);
            sensor_statistic = (int) (x * 623);

        }

    @Override
    public void onResume() {
        super.onResume();
        mScrollController.requestAllSensors();
        recyclerView.smoothScrollBy(sensor_statistic, 0);
    }
    @Override
    public void onPause() {
        super.onPause();
        mScrollController.releaseAllSensors();
    }

    public int Dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void onPicClicked(recyclerAdapter.CustomViewHolder holder, int position, ArrayList<Piclist_data> pics) {
        Log.d("아이템 클릭", "onPicClicked: 넘어온 포지션값 : " + position);

        Intent intents = new Intent(DisplayImage.this, imageView.class);
        intents.putExtra("image_data", pics.get(position).getImg_path());
        startActivity(intents);

    }
}