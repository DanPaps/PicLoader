package com.example.danielpappoe.picloader.chats;

/**
 * Created by Daniel Pappoe on 8/6/2017.
 */

public class Messages  {
    private  String id,sender, body , time;

    public Messages() {    }

    public Messages(String body, String id, String sender, String time) {
        this.body = body;
        this.id = id;
        this.sender = sender;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
