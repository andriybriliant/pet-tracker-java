package com.apps.pettracker.objects;

import java.util.UUID;

public class Category {
    String id;
    String name;
    String petId;

    public Category(){

    }

    public Category(String name, String petId){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.petId = petId;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getPetId() {
        return petId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }
}
