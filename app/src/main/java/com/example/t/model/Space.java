package com.example.t.model;

public class Space {
    int room_id;
    int storage_space_id;

    public Space(int room_id, int storage_space_id) {
        this.room_id = room_id;
        this.storage_space_id = storage_space_id;
    }

    public int getStorage_space_id() {
        return storage_space_id;
    }

    public int getRoom_id() {
        return room_id;
    }
}
