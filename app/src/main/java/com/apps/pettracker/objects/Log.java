package com.apps.pettracker.objects;

import java.util.UUID;

public class Log {
    private String id;
    private String name;
    private String description;
    private long date;

    public Log(){

    }

    public Log(String name, String description, long date){
        this.id = UUID.randomUUID().toString();
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

    public void setDate(long date) {
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

    public long getDate() {
        return date;
    }
}
