package com.example.hp.firechat;

/**
 * Created by hp on 12/8/2017.
 */

public class GoodChat {

    private String from,message,seen,type;
    private long time;
    public GoodChat(){
    }
    public GoodChat(String from,String message,String seen,long time,String type){
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
    public long getTime(){
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
    public void setTime(long time){
        this.time=time;
    }
    public void setType(String type){
        this.type=type;
    }

}
