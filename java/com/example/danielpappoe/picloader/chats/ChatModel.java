package com.example.danielpappoe.picloader.chats;

/**
 * Created by Daniel Pappoe on 7/3/2017.
 */

public class ChatModel {
    public String username, profile,status,seen;

    public ChatModel() {}

    public ChatModel(String username, String profile, String status, String seen) {
        this.username = username;
        this.profile = profile;
        this.status = status;
        this.seen = seen;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}
