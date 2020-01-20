package com.ECE1778A1;

import com.ECE1778A1.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText email, password,password2,userInputBio, userInputName;
    private Button btnSignUp,bthLogin;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db;
    private UserInfo userInfo;


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
                }
                else  if(str_pwd.isEmpty()){
                    password.setError("Please fill in your password");
                    password.requestFocus();
                }
                else  if(str_pwd2.isEmpty()){
                    password2.setError("Please re-enter your password");
                    password2.requestFocus();
                }
                else  if(str_userName.isEmpty()){
                    userInputName.setError("Please enter your user name");
                    userInputName.requestFocus();
                }
                else  if(!str_email.isEmpty() && !str_pwd.isEmpty() && !str_pwd2.isEmpty() && !str_userName.isEmpty()){
                    //check email and password
                    if (!Patterns.EMAIL_ADDRESS.matcher(str_email).matches()){
                        email.setError("The email address is in correct");
                        email.requestFocus();
                    }
                    else if (str_pwd.length()<6){//check if password length correct
                        password.setError("Password should be no less than 6 characters");
                        password.requestFocus();
                    }
                    else if (!str_pwd.equals(str_pwd2)){
                        Toast.makeText(SignupActivity.this,"The passwords do not match",Toast.LENGTH_SHORT).show();
                        password.setError("The passwords do not match");
                        password2.setError("The passwords do not match");
                        password.requestFocus();
                    }
                    else{
                        mFirebaseAuth.createUserWithEmailAndPassword(str_email, str_pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                        email.setError("The email is already in use");
                                        email.requestFocus();
                                    }
                                    else{
                                        Toast.makeText(SignupActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    // create a map object
                                    UserInfo dataObj = new UserInfo(str_email,str_userName,str_userBio);

                                    //input into data base
                                    db = FirebaseFirestore.getInstance();
                                    db.collection("users")
                                            .add(dataObj)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d("Insert DB", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Insert DB", "Error adding document", e);
                                                }
                                            });

                                    Toast.makeText(SignupActivity.this,"Signup Succeed, You are logged in",Toast.LENGTH_LONG).show();
                                    //todo:add auto navito main page
                                    finish();
                                }
                            }
                        });
                    }
                }
                else{
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
    }
}
