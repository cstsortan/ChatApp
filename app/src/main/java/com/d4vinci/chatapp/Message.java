package com.d4vinci.chatapp;

/**
 * Created by D4Vinci on 6/22/2017.
 */

public class Message {
    private String uid="";
    private String text="";
    private String time="";
    private String photo="";

    public Message() {
        //for Firebase
    }

    public Message(String uid, String text, String time, String photo) {
        this.uid = uid;
        this.text = text;
        this.time = time;
        this.photo = photo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
