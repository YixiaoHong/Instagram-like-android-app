package com.ECE1778A1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ECE1778A1.model.PhotoInfo;

import java.io.File;
import java.util.List;

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.MyViewHolder> {
    private List<PhotoInfo> mPhotos;
    private String FilePath;
    private Activity curActivity;

    // Provide a constructor
    public PicAdapter(Activity Activity,String Path, List<PhotoInfo> Photos) {
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
    public PicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        MyViewHolder mVH = new MyViewHolder(v);
        return mVH;
    }

    // Replace contents
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //holder.textView. todo: replace image with url
        final File img = new File(FilePath + mPhotos.get(position).getUser_uid() + "/" + mPhotos.get(position).getPhoto_id());
        final File img_ic = new File(FilePath + mPhotos.get(position).getUser_uid() + "/"+ "displayPic.jpg");


        holder.imgView.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));

        final String str_img_owner = mPhotos.get(position).getUser_name();
        final String str_img_caption = mPhotos.get(position).getCaption();
        final String str_img_owner_uid = mPhotos.get(position).getUser_uid();
        final String str_img_id = mPhotos.get(position).getPhoto_id();


        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent largeImage_int = new Intent(curActivity, largeImage.class);
                largeImage_int.putExtra("uriPath",img.getAbsolutePath());
                largeImage_int.putExtra("ic_uriPath",img_ic.getAbsolutePath());
                largeImage_int.putExtra("str_img_owner","Posted by: "+str_img_owner);
                largeImage_int.putExtra("str_img_caption",str_img_caption);
                largeImage_int.putExtra("str_img_owner_uid",str_img_owner_uid);
                largeImage_int.putExtra("str_img_id",str_img_id);
                curActivity.startActivity(largeImage_int);

            }
        });

    }

    // Provide a reference to the views for each entry
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;

        public MyViewHolder(View v) {
            super(v);
            imgView = itemView.findViewById(R.id.img_id);

        }
    }


}