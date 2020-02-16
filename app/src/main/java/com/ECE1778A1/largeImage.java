package com.ECE1778A1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ECE1778A1.model.CommentInfo;
import com.ECE1778A1.model.PhotoInfo;
import com.ECE1778A1.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class largeImage extends AppCompatActivity {
    private TextView image_caption, img_owner;
    private ImageView delete_icon;
    private FirebaseUser user;
    private Button btn_comment;
    private FirebaseFirestore db;
    private String commenter_name;

    //comment related
    private List<CommentInfo> commentInfoList;
    private List<CommentInfo> commentDownloadedList;
    private CommentAdapter mAdapter;
    private String Path;
    private RecyclerView mRcyView;
    private String current_image_id;
    private StorageReference mStorageRef;

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

        //comment needed
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Path = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/";
        mRcyView = findViewById(R.id.comment_recycler_view);


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
        current_image_id = (String)getIntent().getSerializableExtra("str_img_id");

        ImageView delete_icon = findViewById(R.id.large_image_delete);
        if (!user.getUid().equals(image_user_uid)){
            delete_icon.setVisibility(View.INVISIBLE);
        }

        refresh_comments();

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
        //clean the dict
        //Init comment List views
        commentInfoList = new ArrayList<>();
        commentDownloadedList = new ArrayList<>();
        //init adapter
        mAdapter = new CommentAdapter(this,Path, commentDownloadedList);
        //init recycler view
        mRcyView.setAdapter(mAdapter);
        GridLayoutManager mGLM = new GridLayoutManager(this,1);
        mRcyView.setLayoutManager(mGLM);

        //Get all the comment data from database
        // Create a reference to the comment collection
        CollectionReference commentRef = db.collection("comments");
        // Create a query against the collection.
        Query query = commentRef.whereEqualTo("photo_id", user.getUid());
        db.collection("comments").whereEqualTo("photo_id", current_image_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Get all comment list
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CommentInfo fi = document.toObject(CommentInfo.class);
                                commentInfoList.add(fi);
                                Log.d("OUTPUT!!", document.getId() + " => " + fi.getCommenter_id());
                            }
                            //Collected all info need to download all images

                            //Determine if there is any comments
                            if (commentInfoList.size()>0){
                                findViewById(R.id.no_comments).setVisibility(View.INVISIBLE);
                            }

                            for ( final CommentInfo eachComment: commentInfoList) {
                                //loop each comments

                                //Now need to check if the image user icon downloaded:
                                File folder =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + eachComment.getCommenter_id());
                                if (!folder.exists()){
                                    folder.mkdirs();
                                }
                                final File icon_img = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + eachComment.getCommenter_id() + "/", "displayPic.jpg");
                                if (!icon_img.exists()) {
                                    String path = "user_icon/" + eachComment.getCommenter_id() + "/" + "displayPic.jpg";
                                    StorageReference displayPicRef = mStorageRef.child(path);
                                    displayPicRef.getFile(icon_img)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    //Successfully downloaded user icon

                                                    commentDownloadedList.add(eachComment);
                                                    //sort list
                                                    Collections.sort(commentDownloadedList);
                                                    mAdapter.notifyDataSetChanged();


                                                }
                                            });
                                }
                                else {
                                    //user icon exist
                                    commentDownloadedList.add(eachComment);
                                    //sort list
                                    Collections.sort(commentDownloadedList);
                                    mAdapter.notifyDataSetChanged();
                                }


                            }//for loop
                        } //Get all comment list
                        else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
