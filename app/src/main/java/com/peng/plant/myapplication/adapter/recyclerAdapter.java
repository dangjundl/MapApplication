package com.peng.plant.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.peng.plant.myapplication.data.Piclist_data;
import com.peng.plant.myapplication.R;
import com.peng.plant.myapplication.listener.itemClickListener;

import java.util.ArrayList;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.CustomViewHolder> {
    private ArrayList<Piclist_data> arrayList;
    private final Context pictureContx;
    private final itemClickListener picListerner;
    private String position_num;


    public recyclerAdapter(ArrayList<Piclist_data> arrayList, Context pictureContext, itemClickListener picListerner) {
        this.arrayList = arrayList;
        this.pictureContx = pictureContext;
        this.picListerner = picListerner;
    }

    @NonNull
    @Override
    public recyclerAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.piclist_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerAdapter.CustomViewHolder holder, int position) {

        position_num =""+(position+1);


        Piclist_data mdata = arrayList.get(position);
        Glide.with(pictureContx)
                .load(mdata.getImg_path())
                .into(holder.imageView);

        holder.location_name.setText(mdata.name);
        holder.item_position.setText(position_num);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picListerner.onPicClicked(holder, position, arrayList);
            }
        });

        holder.item_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picListerner.onPicClicked(holder, position, arrayList);
            }
        });


    }


    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public void remove(int position) {
        try {
            arrayList.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView location_name,item_position;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.image);
            this.location_name = (TextView) itemView.findViewById(R.id.image_Name);
            this.item_position = (TextView) itemView.findViewById(R.id.image_position);
        }
    }
}