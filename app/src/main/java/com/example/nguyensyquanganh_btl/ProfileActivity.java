package com.example.nguyensyquanganh_btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nguyensyquanganh_btl.model.User;
import com.example.nguyensyquanganh_btl.utils.ChatUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView ivAvtPro;
    private Button btnLogOut;
    private TextView tvFullName;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ivAvtPro = findViewById(R.id.ivAvtPro);
        btnLogOut = findViewById(R.id.btnLogOut);
        tvFullName = findViewById(R.id.tvFullName);
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("mainUser");
        if (currentUser.getImageUri() == null) {
            ivAvtPro.setImageResource(R.drawable.avatar_default);
        } else {
            Glide.with(this).load(currentUser.getImageUri()).into(ivAvtPro);
        }

        tvFullName.setText(currentUser.getFullName());


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });
    }

    private void logOut() {
        ChatUtils.updateUserStatus(currentUser.getId(),"offline");
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
        FirebaseAuth.getInstance().signOut();
    }

    private void setStatus(String status) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Map update = new HashMap();
        update.put("status", status);
        db.updateChildren(update);
    }
}