/*
 * MIT License
 *
 * Copyright (c) 2020 YixiaoHong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ECE1778A1.ui.plaza;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ECE1778A1.PicAdapterPlazza;
import com.ECE1778A1.R;
import com.ECE1778A1.model.PhotoInfo;
import com.ECE1778A1.model.UserInfo;
import com.ECE1778A1.ui.home.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SlideshowFragment extends Fragment {

    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private Uri takenImageUri;
    private String currentTakenImagename;
    private PicAdapterPlazza mAdapter;
    private RecyclerView mRcyView;
    private List<PhotoInfo> photoInfoList;
    private List<PhotoInfo> photoDownloadedList;
    private String Path;
    private FirebaseUser user;
    private EditText photo_caption;
    private String currentUserName;

    //camera icon
    FloatingActionButton camera_btn;

    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_plaza, container, false);
        mRcyView = root.findViewById(R.id.plaza_view);
        //current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        //camera btn
        camera_btn = root.findViewById(R.id.plaza_add_img);

        //storage
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Path = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/";

        //database
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                UserInfo userInfo = task.getResult().toObject(UserInfo.class);
                currentUserName = userInfo.getUserName();
            }
        });

        //Set camera icon listener
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                } else {
                    Intent takephoto_int = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takephoto_int.resolveActivity(getActivity().getPackageManager()) != null) {
                        //create temp path
                        File folder = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/temp");
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                        //Add time stemp
                        Date date = new Date();
                        currentTakenImagename = String.valueOf(new Timestamp(date).getSeconds());
                        //save file
                        File photoFile = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + user.getUid() + "/" +currentTakenImagename+".jpg");
                        takenImageUri = FileProvider.getUriForFile(getActivity(), "com.ECE1778A1.path",
                                photoFile);
                        takephoto_int.putExtra(MediaStore.EXTRA_OUTPUT, takenImageUri);
                        startActivityForResult(takephoto_int, 1);
                    }
                }
            }
        });

        refreshPhotoList();
        return root;
    }


    void refreshPhotoList(){
        //clean the dict
        //Init Photo List views
        photoInfoList = new ArrayList<>();
        photoDownloadedList = new ArrayList<>();
        //init adapter
        mAdapter = new PicAdapterPlazza(getActivity(),Path,photoDownloadedList);
        //init recycler view
        mRcyView.setAdapter(mAdapter);
        GridLayoutManager mGLM = new GridLayoutManager(getActivity(),1);
        mRcyView.setLayoutManager(mGLM);

        //Get all the photo data from database
        // Create a reference to the photos collection
        CollectionReference photoRef = db.collection("photos");
        // Create a query against the collection.
        db.collection("photos").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Get all photoInfo list
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PhotoInfo fi = document.toObject(PhotoInfo.class);
                                photoInfoList.add(fi);
                                Log.d("OUTPUT!!", document.getId() + " => " + fi.getPhoto_id());
                            }
                            //Collected all info need to download all images
                            for ( final PhotoInfo eachPhoto: photoInfoList) {

                                //check all folders created
                                File folder =  new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + eachPhoto.getUser_uid());
                                if (!folder.exists()){
                                    folder.mkdirs();
                                }
                                File download_image = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + eachPhoto.getUser_uid() + "/", eachPhoto.getPhoto_id());
                                if (!download_image.exists()) {
                                    String path = "photo/" + eachPhoto.getUser_uid() + "/" + eachPhoto.getPhoto_id();
                                    StorageReference displayPicRef = mStorageRef.child(path);
                                    displayPicRef.getFile(download_image)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    //Successfully download the image

                                                    //Now need to check if the image user icon downloaded:
                                                    File folder =  new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + eachPhoto.getUser_uid());
                                                    if (!folder.exists()){
                                                        folder.mkdirs();
                                                    }
                                                    final File icon_img = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + eachPhoto.getUser_uid() + "/", "displayPic.jpg");
                                                    if (!icon_img.exists()) {
                                                        String path = "user_icon/" + eachPhoto.getUser_uid() + "/" + "displayPic.jpg";
                                                        StorageReference displayPicRef = mStorageRef.child(path);
                                                        displayPicRef.getFile(icon_img)
                                                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                        //Successfully downloaded user icon and the image

                                                                        photoDownloadedList.add(eachPhoto);
                                                                        //sort list
                                                                        Collections.sort(photoDownloadedList);
                                                                        mAdapter.notifyDataSetChanged();


                                                                    }
                                                                });
                                                    }
                                                    else {
                                                        //downloaded user image but user icon exist
                                                        photoDownloadedList.add(eachPhoto);
                                                        //sort list
                                                        Collections.sort(photoDownloadedList);
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                } else {
                                    //Image already exist, but not sure if user icon exist
                                    //Now need to check if the image user icon downloaded:
                                    final File icon_img = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + eachPhoto.getUser_uid() + "/", "displayPic.jpg");
                                    if (!icon_img.exists()) {
                                        String path = "user_icon/" + eachPhoto.getUser_uid() + "/" + "displayPic.jpg";
                                        StorageReference displayPicRef = mStorageRef.child(path);
                                        displayPicRef.getFile(icon_img)
                                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        //Successfully downloaded user icon and the image

                                                        photoDownloadedList.add(eachPhoto);
                                                        //sort list
                                                        Collections.sort(photoDownloadedList);
                                                        mAdapter.notifyDataSetChanged();


                                                    }
                                                });
                                    }
                                    else {
                                        //downloaded user image but user icon exist
                                        photoDownloadedList.add(eachPhoto);
                                        //sort list
                                        Collections.sort(photoDownloadedList);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //now confirm with user to upload image
            final Dialog builder = new Dialog(getActivity());
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setContentView(R.layout.photo_confirm);
            ImageView imageView = builder.findViewById(R.id.photo_display_confirm);
            Button btn_confirm = builder.findViewById(R.id.photo_confirm_upload);
            Button btn_cancel = builder.findViewById(R.id.photo_confirm_cancel);
            final EditText photo_caption = builder.findViewById(R.id.photo_display_caption);
            imageView.setImageURI(takenImageUri);


            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //User confirm to upload image
                    Toast.makeText(getActivity(),"Photo uploading",Toast.LENGTH_SHORT).show();
                    //input into data base
                    String str_caption = photo_caption.getText().toString();
                    PhotoInfo photoObj = new PhotoInfo(FirebaseAuth.getInstance().getCurrentUser().getUid(),currentTakenImagename+".jpg",currentTakenImagename,str_caption,currentUserName);
                    db = FirebaseFirestore.getInstance();
                    db.collection("photos/").add(photoObj);
                    //input into storage
                    mStorageRef = FirebaseStorage.getInstance().getReference();
                    //upload photo to firebase
                    StorageReference riversRef = mStorageRef.child("photo/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+currentTakenImagename+".jpg");
                    riversRef.putFile(takenImageUri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(),"Photo uploaded",Toast.LENGTH_SHORT).show();
                                        refreshPhotoList();

                                    }
                                    else {
                                        Toast.makeText(getActivity(),"Failed to upload image",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    builder.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    builder.dismiss();
                }
            });
            builder.show();

        }
    }
}