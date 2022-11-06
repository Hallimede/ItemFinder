package com.example.t.model;

public class Reminder {
    public int itemId;
    public String repeatType;
    public int hour;
    public int min;
    public String roomName;
    public String storageSpaceName;
    public String ivtInfo;

    public Reminder(int itemId, String repeatType, int hour, int min) {
        this.itemId = itemId;
        this.repeatType = repeatType;
        this.hour = hour;
        this.min = min;
    }

    public void addDetails(String room, String space, String info) {
        roomName = room;
        storageSpaceName = space;
        ivtInfo = info;
    }

}
