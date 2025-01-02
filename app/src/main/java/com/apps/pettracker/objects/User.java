package com.apps.pettracker.objects;

public class User {
    String name;
    String email;
    String userId;

    public User(){

    }

    public User(String name, String email, String userId){
        this.name = name;
        this.email = email;
        this.userId = userId;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getName(){
        return this.name;
    }

    public String getEmail(){
        return this.email;
    }

    public String getUserId(){
        return this.userId;
    }
}
