package com.ECE1778A1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.ECE1778A1.model.PhotoInfo;

import java.util.List;

class PicAdapter extends RecyclerView.Adapter<PicAdapter.MyViewHolder> {
    private List<PhotoInfo> mPhotos;

    // Provide a constructor
    public PicAdapter(List<PhotoInfo> mPhotos) {
        this.mPhotos = mPhotos;
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

    }

    // Provide a reference to the views for each entry
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView textView;

        public MyViewHolder(View v) {
            super(v);
            textView = itemView.findViewById(R.id.img_id);
        }
    }


}