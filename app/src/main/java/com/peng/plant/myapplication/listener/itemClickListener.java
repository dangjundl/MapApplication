package com.peng.plant.myapplication.listener;

import com.peng.plant.myapplication.data.Piclist_data;
import com.peng.plant.myapplication.adapter.recyclerAdapter;

import java.util.ArrayList;


public interface itemClickListener {

    void onPicClicked(recyclerAdapter.CustomViewHolder holder, int position, ArrayList<Piclist_data> pics);

}
