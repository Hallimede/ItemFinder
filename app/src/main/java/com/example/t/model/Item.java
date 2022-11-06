package com.example.t.model;

import androidx.annotation.NonNull;

public class Item extends Base {

    @NonNull
    @Override
    public String toString() {
        return "Item [name=" + name + ", id=" + id + ", owner_id=" + owner_id + "]";
    }

    public Item(String name) {
        this.name = name;
    }

    public Item(String name, String image){
        this.name = name;
        this.image = image;
    }

    public Item(int id){
        this.id = id;
    }

}
