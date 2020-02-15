package com.ECE1778A1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class largeImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);
        String fileWholePath = (String)getIntent().getSerializableExtra("uriPath");
        ImageView largeImageView = findViewById(R.id.large_image);
        final File img = new File(fileWholePath);
        largeImageView.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
    }
}
