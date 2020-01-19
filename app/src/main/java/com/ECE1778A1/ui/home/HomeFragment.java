package com.ECE1778A1.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ECE1778A1.IndexActivity;
import com.ECE1778A1.LoginActivity;
import com.ECE1778A1.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private String currentuser;
    private Button btnSignOut;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Button logoutBtn = (Button)root.findViewById(R.id.btn_main_logout);
        TextView text = (TextView)root.findViewById(R.id.home_page_user_name);
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        text.setText(currentuser);

        //Prepare the signout button for the user
        btnSignOut = (Button)root.findViewById(R.id.btn_main_logout);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent signInPage = new Intent(getActivity(), LoginActivity.class);
//                signInPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().finish();
                startActivity(signInPage);
            }
        });

        return root;
    }
}