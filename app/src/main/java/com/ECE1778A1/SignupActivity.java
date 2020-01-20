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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ECE1778A1.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private EditText email, password,password2,userInputBio, userInputName;
    private Button btnSignUp,bthLogin;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db;
    private UserInfo userInfo;
    private ImageView cameraIcon;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.editText_signup_email);
        password = findViewById(R.id.editText_signup_password);
        password2 = findViewById(R.id.editText_signup_password2);
        userInputName = findViewById(R.id.editText_signup_username);
        userInputBio = findViewById(R.id.editText_signup_user_bio);
        bthLogin = findViewById(R.id.btn_signup_login);
        btnSignUp = findViewById(R.id.btn_signup_signup);

        cameraIcon = findViewById(R.id.camera_icon);

        mFirebaseAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String str_email = email.getText().toString();
                String str_pwd = password.getText().toString();
                String str_pwd2 = password2.getText().toString();
                final String str_userName = userInputName.getText().toString();
                final String str_userBio = userInputBio.getText().toString();

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
                    } else{
                        mFirebaseAuth.createUserWithEmailAndPassword(str_email, str_pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                        email.setError("The email is already in use");
                                        email.requestFocus();
                                    } else{
                                        Toast.makeText(SignupActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // create a map object
                                    UserInfo dataObj = new UserInfo(str_email,str_userName,str_userBio);

                                    //input into data base
                                    db = FirebaseFirestore.getInstance();
                                    db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(dataObj);

                                    Toast.makeText(SignupActivity.this,"Signup Succeed, You are logged in",Toast.LENGTH_LONG).show();
                                    //todo:add auto navito main page
                                    finish();
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
        bthLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the system os >= current version, request runtime permission
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

                    //permission not eabled
                    if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED
                            || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //popup to use permission
                        requestPermissions(permission,1000);
                    }
                    //permission enabled
                    else{
                        openCamera();

                    }

                }
            }
        });
    }

    private void openCamera(){
        ContentValues val = new ContentValues();
        val.put(MediaStore.Images.Media.TITLE,"Picture");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,val);
        //open camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(cameraIntent,1001);
    }

    //to handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            cameraIcon.setImageURI(imageUri);
        }
    }
}
