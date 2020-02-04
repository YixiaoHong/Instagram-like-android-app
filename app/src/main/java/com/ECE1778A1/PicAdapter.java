package com.ECE1778A1;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.ECE1778A1.model.PhotoInfo;

import java.io.File;
import java.util.List;

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.MyViewHolder> {
    private List<PhotoInfo> mPhotos;
    private String FilePath;

    // Provide a constructor
    public PicAdapter(String Path, List<PhotoInfo> Photos) {
        this.mPhotos = Photos;
        this.FilePath = Path;
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
        final File img = new File(FilePath + mPhotos.get(position).getPhoto_id());
        holder.imgView.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));

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