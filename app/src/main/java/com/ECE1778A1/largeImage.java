package com.ECE1778A1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ECE1778A1.model.CommentInfo;
import com.ECE1778A1.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.Date;

public class largeImage extends AppCompatActivity {
    private TextView image_caption, img_owner;
    private ImageView delete_icon;
    private FirebaseUser user;
    private Button btn_comment;
    private FirebaseFirestore db;
    private String commenter_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // load data base
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                UserInfo userInfo = task.getResult().toObject(UserInfo.class);
                commenter_name = userInfo.getUserName();
            }
        });



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

        final String image_user_uid = (String)getIntent().getSerializableExtra("str_img_owner_uid");
        final String image_id = (String)getIntent().getSerializableExtra("str_img_id");



        ImageView delete_icon = findViewById(R.id.large_image_delete);
        if (!user.getUid().equals(image_user_uid)){
            delete_icon.setVisibility(View.INVISIBLE);
        }

        delete_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //User wants to delete this photo
            }
        });

        //comment
        btn_comment = findViewById(R.id.btn_Comment);
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user hit comment button
                final EditText comment_edit = findViewById(R.id.comment_message);
                final String comment_text = comment_edit.getText().toString().trim();
                if (!comment_text.equals(null) && !comment_text.equals("")){
                    //time stamp
                    //Add time stemp
                    Date date = new Date();
                    String currentTime = String.valueOf(new Timestamp(date).getSeconds());

                    // create a map object
                    CommentInfo commentObj = new CommentInfo(image_id, image_user_uid, user.getUid(), currentTime, commenter_name, comment_text);
                    //input into data base
                    db = FirebaseFirestore.getInstance();
                    db.collection("comments").add(commentObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            comment_edit.setText(null);
                            Toast.makeText(largeImage.this,"Comment Posted",Toast.LENGTH_SHORT).show();
                            refresh_comments();
                        }
                    });
                }
                else{
                    Toast.makeText(largeImage.this,"Please enter your comments!",Toast.LENGTH_SHORT).show();
                    refresh_comments();
                }
            }
        });
    }

    private void refresh_comments(){

    }
}
