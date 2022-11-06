package com.example.t.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    String phone_number;
    String name;
    int id;
    String password;

    public User() {

    }

    public User(String phone_number, String name, int id) {
        this.phone_number = phone_number;
        this.name = name;
        this.id = id;
    }

    // Login
    public User(String phone_number, String password) {
        this.phone_number = phone_number;
        this.password = password;
    }

    // Register
    public User(String phone_number, String password, String name) {
        this.phone_number = phone_number;
        this.password = password;
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(phone_number);
        parcel.writeInt(id);
        parcel.writeString(password);
    }

    public static final Parcelable.Creator<User> CREATOR = new Creator<User>() {
        //实现从source中创建出类的实例的功能
        @Override
        public User createFromParcel(Parcel source) {
            User user = new User();
            user.name = source.readString();
            user.phone_number = source.readString();
            user.id = source.readInt();
            user.password = source.readString();
            return user;
        }

        //创建一个类型为T，长度为size的数组
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "User [name=" + name + ", phone_number=" + phone_number + ", id=" + id + ", password=" + password + "]";
    }
}
