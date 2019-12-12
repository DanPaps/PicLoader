package com.example.danielpappoe.picloader.chats;

/**
 * Created by Daniel Pappoe on 7/3/2017.
 */

public class ContactModel {
    public String username,status,profile,email;

    public ContactModel() {}

    public ContactModel(String name, String status, String profile, String email) {
        this.username = name;
        this.status = status;
        this.profile = profile;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

}
