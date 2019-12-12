package com.example.danielpappoe.picloader;

import java.util.Comparator;

/**
 * Created by Daniel Pappoe on 6/26/2017.
 */
public class Blog {
    //things you will get from the database
    private String  desc;
    private String image;
    private String profile;
    private String username;
    private String email;


    public Blog(){}
    //REQUIRED DEFAULT CONSTRUCTOR


    public Blog( String desc, String image, String profile, String username, String email) {
        this.desc = desc;
        this.image = image;
        this.profile = profile;
        this.username = username;
        this.email =email;
    }

    public String getDesc() {
        return desc;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getProfile() {
        return profile;
    }

    public String getUsername() {
        return username;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setUsername(String username) {
        this.username = username;
    }

/*    public static Comparator<Blog> BY_USERNAME = new Comparator<Blog>() {
        @Override
        public int compare(Blog blog, Blog t1) {
            return blog.username.compareTo(t1.username);
        }
    };*/
}
