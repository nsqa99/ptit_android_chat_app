package com.example.nguyensyquanganh_btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.example.nguyensyquanganh_btl.fragment.ChatFragment;
import com.example.nguyensyquanganh_btl.fragment.FriendFragment;
import com.example.nguyensyquanganh_btl.model.User;
import com.example.nguyensyquanganh_btl.utils.ChatUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements Serializable {
    private FrameLayout frame_container;
    private BottomNavigationView bottomNavigationView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        if (user == null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            user = new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getPhotoUrl().toString());
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                Log.d("MainActivity", String.valueOf(item.getItemId()));
                switch (item.getItemId()) {
                    case R.id.itMessage:
                        fragment = new ChatFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.itFriends:
                        fragment = new FriendFragment();
                        loadFragment(fragment);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatUtils.updateUserStatus(user.getId(),"online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatUtils.updateUserStatus(user.getId(),"offline");
    }

    private void initView() {
        frame_container = findViewById(R.id.frame_container);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        ChatFragment chatFragment = new ChatFragment();
        loadFragment(chatFragment);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}