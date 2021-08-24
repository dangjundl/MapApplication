package com.peng.plant.myapplication;

import java.util.ArrayList;


public interface itemClickListener {

    void onPicClicked(recyclerAdapter.CustomViewHolder holder, int position, ArrayList<Piclist_data> pics);

}
