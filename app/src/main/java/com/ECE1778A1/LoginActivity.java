package com.ECE1778A1;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLOutput;

public class LoginActivity extends AppCompatActivity {
    //user input
    public EditText email, password;
    public Button btnSignUp,bthLogin;
    public FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener myAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //google auth
        myFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editText_login_email);
        password = findViewById(R.id.editText_login_password);
        bthLogin = findViewById(R.id.btn_login_login);
        btnSignUp = findViewById(R.id.btn_signup_signup);

        myAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser loginFirebaseUser = myFirebaseAuth.getCurrentUser();
                if( loginFirebaseUser != null ){
                    //user exist
                    Toast.makeText(LoginActivity.this,"You are successfully logged in", Toast.LENGTH_SHORT).show();
                    Intent indexActivity_int = new Intent(LoginActivity.this, IndexActivity.class);
                    startActivity(indexActivity_int);
                }
                else{
                    Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                }
            }
        };

        bthLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //read account and password
                String account = email.getText().toString();
                String pwd = password.getText().toString();
                if (!account.isEmpty() && !pwd.isEmpty()){
                    myFirebaseAuth.signInWithEmailAndPassword(account, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent Index_int = new Intent(LoginActivity.this,IndexActivity.class);
                                startActivity(Index_int);
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"Login failed, please login again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else if(account.isEmpty()){
                    email.setError("Please enter user email");
                    email.requestFocus();
                }
                else  if(pwd.isEmpty()){
                    password.setError("Please enter password");
                    password.requestFocus();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Please fill email and password",Toast.LENGTH_SHORT).show();
                }

            }
        });


        //redirect to signup page
        btnSignUp.setOnClickListener(new View.OnClickListener() {
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
