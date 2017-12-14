package com.example.hp.firechat;

/**
 * Created by hp on 12/7/2017.
 */

public class Chat {
    public String from;
    public String message;
    public String seen;
    public String time;
    public String type;
    public Chat(){
    }
    public Chat(String from,String message,String seen,String time,String type){
        this.from=from;
        this.message=message;
        this.seen=seen;
        this.time=time;
        this.type=type;
    }

    public String getFrom() {
        return from;
    }
    public String getMessage(){
        return message;
    }
    public String getSeen(){
        return seen;
    }
    public String getTime(){
        return time;
    }
    public String getType(){
        return type;
    }
    public void setFrom(String from){
        this.from=from;
    }
    public void setMessage(String message){
        this.message=message;
    }
    public void setSeen(String seen){
        this.seen=seen;
    }
    public void setTime(String time){
        this.time=time;
    }
    public void setType(String type){
        this.type=type;
    }
}