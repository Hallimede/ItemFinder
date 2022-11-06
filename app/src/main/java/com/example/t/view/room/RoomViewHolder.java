package com.example.t.view.room;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t.R;
import com.example.t.view.MainPageActivity;
import com.facebook.drawee.view.SimpleDraweeView;

public class RoomViewHolder extends RecyclerView.ViewHolder {

    public TextView txtRoomName;
    public TextView txtStorageSpaceNo;
    public SimpleDraweeView imgRoom;

    public RoomViewHolder(@NonNull View itemView) {
        super(itemView);
        txtRoomName = itemView.findViewById(R.id.room_name);
        txtStorageSpaceNo = itemView.findViewById(R.id.storage_space_no);
        imgRoom = itemView.findViewById(R.id.room_img);
    }
}