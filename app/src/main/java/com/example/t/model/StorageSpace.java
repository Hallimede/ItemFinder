package com.example.t.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class StorageSpace extends Base implements Parcelable {

    public int room_id;

    public StorageSpace(){

    }

    public StorageSpace(String name, int room_id) {
        this.name = name;
        this.room_id = room_id;
    }

    public StorageSpace(String name, int room_id, String image) {
        this.name = name;
        this.room_id = room_id;
        this.image = image;
    }

    public StorageSpace(int room_id){
        this.room_id = room_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    @NonNull
    @Override
    public String toString() {
        return "StorageSpace [name=" + name + ", id=" + id + ", owner_id=" + owner_id + ", room_id=" + room_id + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
        parcel.writeInt(owner_id);
        parcel.writeInt(room_id);
        parcel.writeString(image);
    }

    public static final Parcelable.Creator<StorageSpace> CREATOR = new Creator<StorageSpace>() {
        //实现从source中创建出类的实例的功能
        @Override
        public StorageSpace createFromParcel(Parcel source) {
            StorageSpace storageSpace = new StorageSpace();

            storageSpace.name = source.readString();
            storageSpace.id = source.readInt();
            storageSpace.owner_id = source.readInt();
            storageSpace.room_id = source.readInt();
            storageSpace.image = source.readString();
            return storageSpace;
        }

        //创建一个类型为T，长度为size的数组
        @Override
        public StorageSpace[] newArray(int size) {
            return new StorageSpace[size];
        }
    };
}
