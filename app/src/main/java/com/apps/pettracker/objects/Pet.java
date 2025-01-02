package com.apps.pettracker.objects;

import android.graphics.Bitmap;

import com.google.firebase.firestore.Exclude;

import java.util.UUID;

public class Pet {
    private String petId;
    private String name;
    private String gender;
    private String dateOfBirth;
    private String type;
    private String breed;
    private String color;
    private String imageURL;
    public double weight;
    private boolean neutered;
    private boolean indoor;

    public Pet(){

    }

    public Pet(String name, String gender, String age, String type, String breed, String color, double weight, boolean neutered, boolean indoor){
        this.petId = UUID.randomUUID().toString();
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = age;
        this.type = type;
        this.breed = breed;
        this.color = color;
        this.weight = weight;
        this.neutered = neutered;
        this.indoor = indoor;
    }

    public String getPetId(){
        return this.petId;
    }

    public void setPetId(String petId){
        this.petId = petId;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getGender(){
        return this.gender;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

    public String getDateOfBirth(){
        return this.dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }

    public String getBreed(){
        return this.breed;
    }

    public void setBreed(String breed){
        this.breed = breed;
    }

    public String getColor(){
        return this.color;
    }

    public void setColor(String color){
        this.color = color;
    }

    public double getWeight(){
        return this.weight;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public String getType(){
        return this.type;
    }

    public void setType(String type){
        this.type = type;
    }

    public boolean isNeutered(){
        return this.neutered;
    }

    public void setNeutered(boolean neutered){
        this.neutered = neutered;
    }

    public boolean isIndoor(){
        return this.indoor;
    }

    public void setIndoor(boolean indoor){
        this.indoor = indoor;
    }

    public void setImageURL(String imageURL){
        this.imageURL = imageURL;
    }

    public String getImageURL(){
        return this.imageURL;
    }
}
