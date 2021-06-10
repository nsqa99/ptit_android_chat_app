package com.example.nguyensyquanganh_btl.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.widget.SearchView;

import com.example.nguyensyquanganh_btl.R;
import com.example.nguyensyquanganh_btl.adapter.FriendAdapter;
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
import java.util.List;

public class FriendFragment extends Fragment {
    private RecyclerView rvFriends;
    private FriendAdapter adapter;
    private List<User> mUsers;
    private SearchView searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend, container, false);
        init(v);
        getUsers();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
        return v;
    }

    private void search(String name) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query q = FirebaseDatabase.getInstance().getReference("users").orderByChild("fullName")
                .startAt(name).endAt(name+"\uf8ff");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUsers.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }
                    }
                    adapter = new FriendAdapter(getContext(), mUsers, false);
                    rvFriends.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }

                adapter = new FriendAdapter(getContext(), mUsers, false);
                rvFriends.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void init(View v) {
        rvFriends = v.findViewById(R.id.rvFriends);
        searchView = v.findViewById(R.id.searchView);
        rvFriends.setHasFixedSize(true);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();

    }
}