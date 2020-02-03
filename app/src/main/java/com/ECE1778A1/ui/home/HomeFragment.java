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

package com.ECE1778A1.ui.home;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ECE1778A1.R;
import com.ECE1778A1.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private String currentuser, currentUserName, currentUserEmail, currentUserBio;
    private Button btnSignOut;
    private FirebaseFirestore db;
    private String displayImagePath;
    User user;
    private StorageReference picStorage;
    private ImageView pofileimg;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        Button logoutBtn = root.findViewById(R.id.btn_main_logout);
        final TextView userEmail = root.findViewById(R.id.home_page_user_email);
        final TextView userName = root.findViewById(R.id.home_page_user_name);
        final TextView userBio = root.findViewById(R.id.home_page_user_bio);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        pofileimg = root.findViewById(R.id.user_icon);
        picStorage = (picStorage == null) ? FirebaseStorage.getInstance().getReference() : picStorage;
        //input into data base
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                UserInfo userInfo = task.getResult().toObject(UserInfo.class);
                userEmail.setText("Email: "+userInfo.getUserEmail());
                userName.setText(userInfo.getUserName());
                userBio.setText("Bio: "+ userInfo.getUserBio());
            }
        });
        File folder =  new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + user.getUid());
        if (!folder.exists()){
            folder.mkdirs();
        }
        final File img = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + user.getUid() + "/", "displayPic.jpg");
        if (!img.exists()) {
            String path = "user_icon/" + user.getUid() + "/" + "displayPic.jpg";
            StorageReference displayPicRef = picStorage.child(path);
            displayPicRef.getFile(img)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            pofileimg.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
                        }
                    });
        } else {
            pofileimg.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
        }

        return root;
    }
}