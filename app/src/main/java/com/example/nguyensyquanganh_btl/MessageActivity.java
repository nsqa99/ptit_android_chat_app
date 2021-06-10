package com.example.nguyensyquanganh_btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nguyensyquanganh_btl.adapter.MessageDetailAdapter;
import com.example.nguyensyquanganh_btl.model.Chat;
import com.example.nguyensyquanganh_btl.model.User;
import com.example.nguyensyquanganh_btl.utils.ChatUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.time.SystemClock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView ivAvtMsg, btnSend;
    private TextView tvDisplayNameMsg;
    private RecyclerView rvMsgDetail;
    private EditText etMsg;
    private MessageDetailAdapter adapter;
    private List<Chat> mChats;
    private FirebaseUser sender;
    private ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();

        Intent intent = getIntent();
        sender = FirebaseAuth.getInstance().getCurrentUser();
        User receiver = (User) intent.getSerializableExtra("user");
        getMessages(sender.getUid(), receiver.getId(), receiver.getImageUri());
        if (receiver.getImageUri() != null)
            Glide.with(getApplicationContext()).load(receiver.getImageUri()).into(ivAvtMsg);
        else {
            ivAvtMsg.setImageResource(R.drawable.avatar_default);
        }
        tvDisplayNameMsg.setText(receiver.getFullName());
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMsg.getText().toString();
                message = message.trim();
                if (!message.equals("")) {
                    sendMessage(sender.getUid(), receiver.getId(), etMsg.getText().toString());
                    etMsg.setText("");
                }
            }
        });

        seenMessage(receiver.getId());
    }


    private void initView() {
        ivAvtMsg = findViewById(R.id.ivAvtMsg);
        tvDisplayNameMsg = findViewById(R.id.tvDisplayNameMsg);
        rvMsgDetail = findViewById(R.id.rvMsgDetail);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMsgDetail.setLayoutManager(layoutManager);
        rvMsgDetail.setHasFixedSize(true);
        btnSend = findViewById(R.id.btnSend);
        etMsg = findViewById(R.id.etMsg);
        mChats = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbarMsg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void seenMessage(String sender) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("chats");
        seenListener = db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && chat.getSender().equals(sender)) {
                        Map update = new HashMap();
                        update.put("seen", true);
                        dataSnapshot.getRef().updateChildren(update);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("chats");

        Map content = new HashMap();
        content.put("sender", sender);
        content.put("receiver", receiver);
        content.put("message", message);
        content.put("seen", false);
        content.put("timestamp", SystemClock.getInstance().currentTimeMillis());

        db.push().setValue(content);
    }

    private void getMessages(String sender, String receiver, String imageUri) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("chats");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChats.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat != null) {
                        if (chat.getReceiver().equals(receiver) && chat.getSender().equals(sender)
                                || chat.getReceiver().equals(sender) && chat.getSender().equals(receiver)) {
                            mChats.add(chat);
                        }
                    }
                }
                System.out.println(mChats.size());
                adapter = new MessageDetailAdapter(getApplicationContext(), mChats, imageUri);
                rvMsgDetail.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ChatUtils.updateUserStatus(sender.getUid(),"online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference("chats").removeEventListener(seenListener);
    }

    private void setStatus(String status) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Map update = new HashMap();
        update.put("status", status);
        db.updateChildren(update);
    }
}