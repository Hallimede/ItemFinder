package com.example.t.model;

public class Base {
    public String name;
    public int id;
    public int owner_id;
    public String image;

    public String getName() {
        return name;
    }

    public void setNameo(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }
}
