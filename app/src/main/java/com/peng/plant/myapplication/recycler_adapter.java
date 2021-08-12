package com.peng.plant.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

class recycler_adapter extends RecyclerView.Adapter<recycler_adapter.CustomViewHolder> {
    private ArrayList<Piclist_data> arrayList;
    private final Context pictureContx;

    public recycler_adapter(ArrayList<Piclist_data> arrayList,Context pictureContext) {
        this.arrayList = arrayList;
        this.pictureContx = pictureContext;

    }

    @NonNull
    @Override
    public recycler_adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.piclist_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull recycler_adapter.CustomViewHolder holder, int position) {
//        holder.TextView_title.setText(arrayList.get(position).getTextView_title());
//        holder.InputEditText_contents.setText(arrayList.get(position).getTv_content());
        Piclist_data mdata = arrayList.get(position);
                        Glide.with(pictureContx)
                        .load(mdata.getImg_path())
                        .into(holder.imageView);

        holder.location_name.setText(mdata.name);

//        holder.itemView.setTag(position);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                String curName = holder.TextView_title.getText().toString();
////                Toast.makeText(v.getContext(), curName, Toast.LENGTH_SHORT).show();
//

//
//
////                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
////                    @Override
////                    public boolean onLongClick(View v) {
////                        remove(holder.getAdapterPosition());
////                        return true;
////                    }
////                });
//
//            }
//        });

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
        private TextView location_name;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.image);
            this.location_name = (TextView) itemView.findViewById(R.id.image_Name);
        }
    }
}