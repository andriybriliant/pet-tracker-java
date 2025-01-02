package com.apps.pettracker.objects;

public class Log {
    private String id;
    private String name;
    private String description;
    private String date;

    public Log(){

    }

    public Log(String name, String description, String date){
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}
