package com.d4vinci.chatapp;

/**
 * Created by D4Vinci on 6/7/2017.
 */
public class Chat {
    private String name;
    private String text;

    public Chat() {
    }

    public Chat(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}