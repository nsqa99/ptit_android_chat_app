package com.example.nguyensyquanganh_btl.model;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String fullName;
    private String imageUri;
    private String status;

    public User() {
    }

    public User(String id, String fullName, String imageUri) {
        this.id = id;
        this.fullName = fullName;
        this.imageUri = imageUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
