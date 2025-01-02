package com.apps.pettracker.objects;

import java.util.UUID;

public class Category {
    String id;
    String name;

    public Category(){

    }

    public Category(String name){
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }
}
