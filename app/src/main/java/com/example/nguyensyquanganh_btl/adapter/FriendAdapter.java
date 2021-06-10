package com.example.nguyensyquanganh_btl.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nguyensyquanganh_btl.MessageActivity;
import com.example.nguyensyquanganh_btl.R;
import com.example.nguyensyquanganh_btl.model.Chat;
import com.example.nguyensyquanganh_btl.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    private String lastMsg;
    private boolean isChatting;
    private String isSeen;

    public FriendAdapter(Context mContext, List<User> mUsers, boolean isChatting) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChatting = isChatting;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cell, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.tvDisplayName.setText(user.getFullName());
        if (user.getImageUri() == null) {
            holder.ivAvtFr.setImageResource(R.drawable.avatar_default);
        } else {
            Glide.with(mContext).load(user.getImageUri()).into(holder.ivAvtFr);
        }

        if (isChatting) getLastMessage(user.getId(), holder.tvLastMsg);
        else {
            holder.tvLastMsg.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("user", mUsers.get(position));
                mContext.startActivity(intent);
            }
        });

        if (user.getStatus().equals("online")) {
            holder.ivOn.setVisibility(View.VISIBLE);
            holder.ivOff.setVisibility(View.GONE);
        } else {
            holder.ivOff.setVisibility(View.VISIBLE);
            holder.ivOn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivAvtFr;
        private TextView tvDisplayName;
        private TextView tvLastMsg;
        private CircleImageView ivOn;
        private CircleImageView ivOff;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvtFr = itemView.findViewById(R.id.ivAvtFr);
            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvLastMsg = itemView.findViewById(R.id.tvLastMsg);
            ivOn = itemView.findViewById(R.id.ivOn);
            ivOff = itemView.findViewById(R.id.ivOff);
        }
    }

    private void getLastMessage(String other, TextView tv) {
        lastMsg = "";
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("chats");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isSeen = "";
                Chat lastChat = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat != null) {
                        if (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(other)
                                || chat.getReceiver().equals(other) && chat.getSender().equals(currentUser.getUid())) {
                            lastMsg = chat.getMessage();
                            lastChat = chat;
                        }
                    }
                }
                if (lastChat.getReceiver().equals(currentUser.getUid())) {
                    if (lastChat.isSeen()) isSeen = "true";
                    else isSeen = "false";
                }

                if (!lastMsg.isEmpty()) {
                    tv.setText(lastMsg);
                    if (!isSeen.isEmpty() && isSeen.equals("false")) {
                        tv.setTextColor(Color.BLACK);
                        tv.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                } else {
                    tv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
