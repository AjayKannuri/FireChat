package com.example.hp.firechat;

/**
 * Created by hp on 11/23/2017.
 */

public class Messages {
    private String message,seen,type,from;
    private long time;
    public Messages(){

    }
    public Messages(String message,String seen,long time,String type,String from){
        this.message=message;
        this.seen=seen;
        this.time=time;
        this.type=type;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message){
        this.message=message;
    }
    public String getSeen(){
        return seen;
    }
    public void setSeen(String seen){
        this.seen=seen;
    }
    public long getTime(){
        return time;
    }
    public void setTime(long time){
        this.time=time;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type=type;
    }
    public String getFrom(){
        return from;
    }
    public void setFrom(String from){
        this.from=from;
    }

}
