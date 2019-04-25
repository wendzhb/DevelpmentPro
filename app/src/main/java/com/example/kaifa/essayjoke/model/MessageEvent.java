package com.example.kaifa.essayjoke.model;

/**
 * Created by zhb on 2019/3/27.
 */
public class MessageEvent {
    private String message;
    public  MessageEvent(String message){
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
