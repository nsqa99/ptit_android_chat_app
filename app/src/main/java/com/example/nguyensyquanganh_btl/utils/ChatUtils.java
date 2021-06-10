package com.example.nguyensyquanganh_btl.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ChatUtils {
    public static void updateUserStatus(String userId, String status) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userId);
        Map update = new HashMap();
        update.put("status", status);
        db.updateChildren(update);
    }
}
