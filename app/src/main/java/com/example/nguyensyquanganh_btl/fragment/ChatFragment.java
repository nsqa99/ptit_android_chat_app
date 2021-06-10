package com.example.nguyensyquanganh_btl.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.nguyensyquanganh_btl.ProfileActivity;
import com.example.nguyensyquanganh_btl.R;
import com.example.nguyensyquanganh_btl.adapter.FriendAdapter;
import com.example.nguyensyquanganh_btl.model.Chat;
import com.example.nguyensyquanganh_btl.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {
    private CircleImageView ivAvt;
    private RecyclerView rvChat;
    private FriendAdapter adapter;
    private List<User> mUsers;
    private List<String> listUsers;
    private FirebaseUser currentUser;
    DatabaseReference db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        initView(v);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser.getPhotoUrl() == null) {
            ivAvt.setImageResource(R.drawable.avatar_default);
        } else {
            Glide.with(this).load(currentUser.getPhotoUrl()).into(ivAvt);
        }

        ivAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getContext(), ProfileActivity.class);
                newIntent.putExtra("mainUser", new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getPhotoUrl().toString()));
                startActivity(newIntent);
            }
        });
        db = FirebaseDatabase.getInstance().getReference("chats");
        Query query = db.orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getSender().equals(currentUser.getUid())) {
                        listUsers.remove(chat.getReceiver());
                        listUsers.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(currentUser.getUid())) {
                        listUsers.remove(chat.getSender());
                        listUsers.add(chat.getSender());
                    }
                }
                Collections.reverse(listUsers);
                getChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return v;
    }

    private void getChats() {
        mUsers = new ArrayList<>(Collections.nCopies(listUsers.size(), new User()));
        db = FirebaseDatabase.getInstance().getReference("users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers = new ArrayList<>(Collections.nCopies(listUsers.size(), new User()));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for (int i=0; i<listUsers.size(); i++) {
                        if (user.getId().equals(listUsers.get(i)) && !mUsers.contains(user)) {
                            mUsers.set(i, user);
                        }
                    }
                }
                adapter = new FriendAdapter(getContext(), mUsers, true);
                rvChat.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView(View v) {
        ivAvt = v.findViewById(R.id.ivAvt);
        rvChat = v.findViewById(R.id.rvChat);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setHasFixedSize(true);
        listUsers = new ArrayList<>();
        mUsers = new ArrayList<>();
    }
}