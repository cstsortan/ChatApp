package com.d4vinci.chatapp;

/**
 * Created by D4Vinci on 6/22/2017.
 */

public class MyUser {
    private String uid="";
    private String name="";
    private String photo="";

    public MyUser(String uid, String name, String photo) {
        this.uid = uid;
        this.name = name;
        this.photo = photo;
    }

    public MyUser() {
        // needed for Firebase
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
