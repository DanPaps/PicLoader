package com.example.danielpappoe.picloader.chats;

/**
 * Created by Daniel Pappoe on 7/3/2017.
 */

public class GroupModel {
    public String name, icon, admin, gid,date;

    public GroupModel() {}

    public GroupModel(String name, String icon, String admin, String gid, String date) {
        this.name = name;
        this.icon = icon;
        this.admin = admin;
        this.gid = gid;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
