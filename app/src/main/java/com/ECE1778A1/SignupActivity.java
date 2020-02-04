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

package com.ECE1778A1;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.ECE1778A1.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class SignupActivity extends AppCompatActivity {

    private EditText email, password,password2,userInputBio, userInputName;
    private ImageView camera_icon;
    private Button btnSignUp;
    private TextView textBthLogin;
    private FirebaseAuth myFirebaseAuth;
    private FirebaseFirestore db;
    private UserInfo userInfo;
    private ImageView cameraIcon;
    private Uri imageUri;
    private FirebaseAuth.AuthStateListener myAuthStateListener;
    private ProgressBar progressBar;
    private TextView camera_tip;

    //image
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        progressBar = findViewById(R.id.signup_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        cameraIcon = findViewById(R.id.camera_icon);
        email = findViewById(R.id.editText_signup_email);
        password = findViewById(R.id.editText_signup_password);
        password2 = findViewById(R.id.editText_signup_password2);
        userInputName = findViewById(R.id.editText_signup_username);
        userInputBio = findViewById(R.id.editText_signup_user_bio);
        textBthLogin = findViewById(R.id.textView_sign_login_btn);
        btnSignUp = findViewById(R.id.btn_signup_signup);
        camera_tip = findViewById(R.id.camera_tip);

        myFirebaseAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String str_email = email.getText().toString().trim();
                final String str_pwd = password.getText().toString().trim();
                String str_pwd2 = password2.getText().toString().trim();
                final String str_userName = userInputName.getText().toString().trim();
                final String str_userBio = userInputBio.getText().toString().trim();



                //Null all errors
                email.setError(null);
                password.setError(null);
                password2.setError(null);
                userInputName.setError(null);
                camera_tip.setError(null);

                if(str_email.isEmpty()){
                    email.setError("Please fill in the email");
                    email.requestFocus();
                } else  if(str_pwd.isEmpty()){
                    password.setError("Please fill in your password");
                    password.requestFocus();
                } else  if(str_pwd2.isEmpty()){
                    password2.setError("Please re-enter your password");
                    password2.requestFocus();
                } else  if(str_userName.isEmpty()){
                    userInputName.setError("Please enter your user name");
                    userInputName.requestFocus();
                } else  if(!str_email.isEmpty() && !str_pwd.isEmpty() && !str_pwd2.isEmpty() && !str_userName.isEmpty()){
                    //check email and password
                    if (!Patterns.EMAIL_ADDRESS.matcher(str_email).matches()){
                        email.setError("The email address is in correct");
                        email.requestFocus();
                    } else if (str_pwd.length()<6){//check if password length correct
                        password.setError("Password should be no less than 6 characters");
                        password.requestFocus();
                    } else if (!str_pwd.equals(str_pwd2)){
                        Toast.makeText(SignupActivity.this,"The passwords do not match",Toast.LENGTH_SHORT).show();
                        password.setError("The passwords do not match");
                        password2.setError("The passwords do not match");
                        password.requestFocus();
                    }
                    else if(imageUri == null){
                        camera_tip.setError("Please Take a Profile Photo");
                        Toast.makeText(SignupActivity.this,"Please take a photo",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        progressBar.setVisibility(View.VISIBLE);
                        myFirebaseAuth.createUserWithEmailAndPassword(str_email, str_pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.INVISIBLE);
                                if(!task.isSuccessful()){
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                        email.setError("The email is already in use");
                                        email.requestFocus();
                                    }
                                    else{
                                        Toast.makeText(SignupActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                } else {
                                    // create a map object
                                    UserInfo dataObj = new UserInfo(str_email,str_userName,str_userBio);

                                    //input into data base
                                    db = FirebaseFirestore.getInstance();
                                    db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(dataObj);
                                    final FirebaseUser loginFirebaseUser = myFirebaseAuth.getCurrentUser();

                                    //upload user profile  image to storage
                                    mStorageRef = FirebaseStorage.getInstance().getReference();

                                    //upload photo to firebase
                                    StorageReference riversRef = mStorageRef.child("user_icon/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/displayPic.jpg");
                                    System.out.println(imageUri.getPath());
                                    riversRef.putFile(imageUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    //navigate to main page
                                                    if( loginFirebaseUser != null ){
                                                        //user exist
                                                        Toast.makeText(SignupActivity.this,"Signup Succeed, You are successfully logged in", Toast.LENGTH_SHORT).show();
                                                        Intent indexActivity_int = new Intent(SignupActivity.this, IndexActivity.class);
                                                        startActivity(indexActivity_int);
                                                    } else{
                                                        Toast.makeText(SignupActivity.this,"Need to re-login",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Toast.makeText(SignupActivity.this,"Failed to upload display picture",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
                    }
                } else{
                    Toast.makeText(SignupActivity.this,"An unknown error occurred",Toast.LENGTH_SHORT).show();
                }
            }

        });

        //redirect to login page
        textBthLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //if the system os >= current version, request runtime permission
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                    //permission not eabled
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //popup to use permission
                        requestPermissions(permission, 1000);
                    }
                    //permission enabled
                    else {
                        openCamera();

                    }
                }
            }
        });
    }

    private File createImageFile() throws IOException {
        String imageFileName = "displayPic";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private void openCamera(){
        ContentValues val = new ContentValues();
        val.put(MediaStore.Images.Media.TITLE,"Picture");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,val);
        //open camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            //create temp path
            File folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/temp");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/temp/displayPic.jpg");
            imageUri = FileProvider.getUriForFile(SignupActivity.this, "com.ECE1778A1.path", photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, 1001);
        }
    }

    //to handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode==1000){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                openCamera();
            } else{
                Toast.makeText(SignupActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //after image captured from camera
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            cameraIcon.setImageURI(imageUri);
            File icon_img = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/temp/displayPic.jpg");
            cameraIcon.setImageBitmap(BitmapFactory.decodeFile(icon_img.getAbsolutePath()));
            if (camera_tip.getError()!=null){
                camera_tip.setError(null);
            }
        }
    }


}
