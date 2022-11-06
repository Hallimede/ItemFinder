package com.example.t.model;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;

public class Inventory {
    public int id;
    public int owner_id;
    public int item_id;
    public int room_id;
    public int storage_space_id;
    public String info;
    public String time;

    public Item item;
    public Room room;
    public StorageSpace storage_space;

    public Inventory(int item_id, int room_id, int storage_space_id) {
        this.item_id = item_id;
        this.room_id = room_id;
        this.storage_space_id = storage_space_id;
    }

    public Inventory(int item_id, int room_id, int storage_space_id, String info) {
        this.item_id = item_id;
        this.room_id = room_id;
        this.storage_space_id = storage_space_id;
        this.info = info;
    }

    public Inventory(int item_id) {
        this.item_id = item_id;
    }

    public Inventory(int room_id, int storage_space_id) {
        this.room_id = room_id;
        this.storage_space_id = storage_space_id;
    }

    @NonNull
    @Override
    public String toString() {
        return "Inventory [id=" + id + ", item_id=" + item_id + ", room_id=" + room_id + ", box_id=" + storage_space_id + "]";
    }

    public String getTimeStr(){
        String[] t = (time.split("T"))[0].split("-");
        return /*t[0]+"年"+*/t[1]+"月"+t[2]+"日";

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

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getStorage_space_id() {
        return storage_space_id;
    }

    public void setStorage_space_id(int storage_space_id) {
        this.storage_space_id = storage_space_id;
    }
}
