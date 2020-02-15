package com.ECE1778A1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class largeImage extends AppCompatActivity {
    private TextView image_caption, img_owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);
        String fileWholePath = (String)getIntent().getSerializableExtra("uriPath");
        String fileWholePath_ic = (String)getIntent().getSerializableExtra("ic_uriPath");

        ImageView largeImageView = findViewById(R.id.large_image);
        final File img = new File(fileWholePath);
        largeImageView.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));

        ImageView largeImageView_ic = findViewById(R.id.large_image_owner_icon);
        final File img_ic = new File(fileWholePath_ic);
        largeImageView_ic.setImageBitmap(BitmapFactory.decodeFile(img_ic.getAbsolutePath()));

        TextView image_caption = findViewById(R.id.large_image_caption);
        image_caption.setText((String)getIntent().getSerializableExtra("str_img_caption"));
        TextView img_owner = findViewById(R.id.large_image_owner);
        img_owner.setText((String)getIntent().getSerializableExtra("str_img_owner"));
    }
}
