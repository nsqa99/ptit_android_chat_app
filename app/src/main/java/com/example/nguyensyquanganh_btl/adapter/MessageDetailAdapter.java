package com.example.nguyensyquanganh_btl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nguyensyquanganh_btl.R;
import com.example.nguyensyquanganh_btl.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class  MessageDetailAdapter extends RecyclerView.Adapter<MessageDetailAdapter.ViewHolder> {
    private Context mContext;
    private List<Chat> mChats;
    private String imageUri;
    private FirebaseUser sender;

    private final int MSG_RECEIVED = 0;
    private final int MSG_SEND = 1;

    public MessageDetailAdapter(Context mContext, List<Chat> mChats, String imageUri) {
        this.mContext = mContext;
        this.mChats = mChats;
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == MSG_SEND) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_send, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_receive, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageDetailAdapter.ViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        holder.tvMsg.setText(chat.getMessage());
        if (holder.ivAvtMsgDetail != null) {
            if (imageUri == null) {
                holder.ivAvtMsgDetail.setImageResource(R.drawable.avatar_default);
            } else {
                Glide.with(mContext).load(imageUri).into(holder.ivAvtMsgDetail);
            }
        }
        if (holder.tvSeen != null) {
            if (position == mChats.size()-1) {
                if (chat.isSeen()) {
                    holder.tvSeen.setText("Seen");
                } else {
                    holder.tvSeen.setText("Sent");
                }
            } else {
                holder.tvSeen.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivAvtMsgDetail;
        private TextView tvMsg;
        private TextView tvSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvtMsgDetail = itemView.findViewById(R.id.ivAvtMsgDetail);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvSeen = itemView.findViewById(R.id.tvSeen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        sender = FirebaseAuth.getInstance().getCurrentUser();
        if (mChats.get(position).getSender().equals(sender.getUid())) {
            return MSG_SEND;
        }
        return MSG_RECEIVED;
    }
}
