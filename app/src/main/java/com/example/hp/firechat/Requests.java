package com.example.hp.firechat;

/**
 * Created by hp on 12/5/2017.
 */

public class Requests {
    public String request_type;
    public Requests(){
    }
    public Requests(String request_type){
        this.request_type=request_type;
    }
    public String getRequest_type() {
        return request_type;
    }
    public void setRequest_type(String request_type){
        this.request_type=request_type;
    }
}
