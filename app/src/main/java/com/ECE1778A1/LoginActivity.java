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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //user input
    private EditText userInputEmail, userInputPassword;
    private Button buttonSignUp, buttonLogin;
    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener myAuthStateListener;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        //get google firebase Auth
        myFirebaseAuth = FirebaseAuth.getInstance();
        //get user inputs
        userInputEmail = findViewById(R.id.editText_login_email);
        userInputPassword = findViewById(R.id.editText_login_password);
        //get buttons
        buttonLogin = findViewById(R.id.btn_login_login);
        buttonSignUp = findViewById(R.id.btn_signup_signup);
        FirebaseUser loginFirebaseUser = myFirebaseAuth.getCurrentUser();

        if( loginFirebaseUser != null ){
            //user exist
            Toast.makeText(LoginActivity.this,"You are successfully logged in", Toast.LENGTH_SHORT).show();
            Intent indexActivity_int = new Intent(LoginActivity.this, IndexActivity.class);
            startActivity(indexActivity_int);
        } else{
            Toast.makeText(LoginActivity.this,"Need to re-login",Toast.LENGTH_SHORT).show();
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                //read account and userInputPassword
                String account = userInputEmail.getText().toString();
                String pwd = userInputPassword.getText().toString();
                if (!account.isEmpty() && !pwd.isEmpty()){
                    myFirebaseAuth.signInWithEmailAndPassword(account, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()){
                                Intent Index_int = new Intent(LoginActivity.this,IndexActivity.class);
                                startActivity(Index_int);
                            } else{
                                Toast.makeText(LoginActivity.this,"Login failed, please try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if(account.isEmpty()){
                    userInputEmail.setError("Please enter user userInputEmail");
                    userInputEmail.requestFocus();
                } else  if(pwd.isEmpty()){
                    userInputPassword.setError("Please enter userInputPassword");
                    userInputPassword.requestFocus();
                } else{
                    Toast.makeText(LoginActivity.this,"Please fill userInputEmail and userInputPassword",Toast.LENGTH_SHORT).show();
                }
            }
        });


        //redirect to signup page
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup_int = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(signup_int);
            }
        });

//        //init toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
//                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

}
