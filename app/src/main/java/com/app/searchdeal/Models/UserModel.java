package com.app.searchdeal.Models;

import java.util.HashMap;

public class UserModel {
    private String uid,email,name,phone,userStatus;

    public UserModel() {
    }

    public UserModel(String uid, String email, String name, String phone, String userStatus) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.userStatus = userStatus;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
