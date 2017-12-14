package com.example.hp.firechat;

/**
 * Created by hp on 11/12/2017.
 */

public class Users {
    public String name; //this should match with the database;
    public String image;
    public String status;
    public String thumb_image;
    public Users(){

    }
    public Users(String name,String image,String status,String thumb_image){
        this.name=name;
        this.image=image;
        this.status=status;
        this.thumb_image=thumb_image;
    }
    public String getThumb_image(){
        return thumb_image;
    }
    public void setThumb_image(String thumb_image){
                this.thumb_image=thumb_image;
    }

    public String getName() {return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String thumb_image){
        this.image=thumb_image;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
}
