package com.ECE1778A1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ECE1778A1.model.PhotoInfo;

import java.io.File;
import java.util.List;

public class PicAdapterPlazza extends RecyclerView.Adapter<PicAdapterPlazza.MyViewHolder> {
    private List<PhotoInfo> mPhotos;
    private String FilePath;
    private Activity curActivity;

    // Provide a constructor
    public PicAdapterPlazza(Activity Activity, String Path, List<PhotoInfo> Photos) {
        this.mPhotos = Photos;
        this.FilePath = Path;
        this.curActivity = Activity;
    }

    // Return the size
    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    // Create views
    @Override
    public PicAdapterPlazza.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plaza_card_view, parent, false);
        MyViewHolder mVH = new MyViewHolder(v);
        return mVH;
    }

    // Replace contents
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //holder.textView. todo: replace image with url
        final File img = new File(FilePath + mPhotos.get(position).getPhoto_id());
        holder.imgView.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
        holder.img_owner.setText("Posted by: "+mPhotos.get(position).getUser_name());
        holder.img_caption.setText(mPhotos.get(position).getCaption());


        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent largeImage_int = new Intent(curActivity, largeImage.class);
                largeImage_int.putExtra("uriPath",img.getAbsolutePath());
                curActivity.startActivity(largeImage_int);

            }
        });

    }

    // Provide a reference to the views for each entry
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public TextView img_owner;
        public TextView img_caption;

        public MyViewHolder(View v) {
            super(v);
            imgView = itemView.findViewById(R.id.plaza_img_id);
            img_owner = itemView.findViewById(R.id.plaza_img_owner);
            img_caption = itemView.findViewById(R.id.plaza_img_caption);
        }
    }


}