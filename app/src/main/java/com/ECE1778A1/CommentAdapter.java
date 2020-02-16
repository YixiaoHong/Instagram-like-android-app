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

import com.ECE1778A1.model.CommentInfo;
import com.ECE1778A1.model.PhotoInfo;

import java.io.File;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private List<CommentInfo> mComments;
    private String FilePath;
    private Activity curActivity;

    // Provide a constructor
    public CommentAdapter(Activity Activity, String Path, List<CommentInfo> CommentObj) {
        this.mComments = CommentObj;
        this.FilePath = Path;
        this.curActivity = Activity;
    }

    // Return the size
    @Override
    public int getItemCount() {
        return mComments.size();
    }

    // Create views
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card_view, parent, false);
        MyViewHolder mVH = new MyViewHolder(v);
        return mVH;
    }

    // Replace contents
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final File img_ic = new File(FilePath + mComments.get(position).getCommenter_id() + "/"+ "displayPic.jpg");
        holder.commenter_ic.setImageBitmap(BitmapFactory.decodeFile(img_ic.getAbsolutePath()));
        holder.commenter_name.setText(mComments.get(position).getCommenter_name());
        holder.commenter_content.setText(mComments.get(position).getComment_text());

    }

    // Provide a reference to the views for each entry
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView commenter_ic;
        public TextView commenter_name;
        public TextView commenter_content;

        public MyViewHolder(View v) {
            super(v);
            commenter_ic = itemView.findViewById(R.id.commenter_icon);
            commenter_name = itemView.findViewById(R.id.comment_owner);
            commenter_content = itemView.findViewById(R.id.comment_content);
        }
    }


}