package com.example.t.model;

import androidx.annotation.NonNull;

import java.util.List;

public class Room extends Base {

    public List <StorageSpace> storage_spaces;

    @NonNull
    @Override
    public String toString() {
        return "Room [name=" + name + ", id=" + id + ", owner_id=" + owner_id + "]";
    }

    public Room(String name) {
        this.name = name;
    }

    public Room(String name, String image) {
        this.name = name;
        this.image = image;
    }

}
