package com.ECE1778A1;

import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText email, password,password2;
    private Button btnSignUp,bthLogin;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.editText_signup_email);
        password = findViewById(R.id.editText_signup_password);
        password2 = findViewById(R.id.editText_signup_password2);
        bthLogin = findViewById(R.id.btn_signup_login);
        btnSignUp = findViewById(R.id.btn_signup_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString();
                String str_pwd = password.getText().toString();
                String str_pwd2 = password2.getText().toString();
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
                else  if(!str_email.isEmpty() && !str_pwd.isEmpty() && !str_pwd2.isEmpty()){

                    try{
                        if (str_pwd.equals(str_pwd2)){
                            mFirebaseAuth.createUserWithEmailAndPassword(str_email, str_pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(SignupActivity.this,"SignUp Unsuccessful, Please Try Again",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(SignupActivity.this,"SignUp Successful, You may login now",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(SignupActivity.this,"The passwords do not match",Toast.LENGTH_SHORT).show();
                            password.requestFocus();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
                else{
                    Toast.makeText(SignupActivity.this,"Please fill in all the fields",Toast.LENGTH_SHORT).show();

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
