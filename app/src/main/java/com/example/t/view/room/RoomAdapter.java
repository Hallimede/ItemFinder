package com.example.t.view.room;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t.R;

import com.example.t.model.Room;
import com.example.t.model.StorageSpace;
import com.example.t.model.User;
import com.example.t.view.MainPageActivity;
import com.example.t.view.storageSpace.StorageSpaceActivity;

import java.io.Serializable;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomViewHolder> {

    private Context mContext;
    private List<Room> mRooms;
    private CardListener mCardListener;
    private int ROOM_CARD = 0;
    private int ADD_ROOM_CARD = 1;

    public RoomAdapter(Context mContext, List<Room> rooms) {
        Log.d("roomadapter  ", "init");
        this.mContext = mContext;
        this.mRooms = rooms;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == ROOM_CARD) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.room_card, parent, false);
            Log.d("add room test", "normal card");
        } else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.add_room_card, parent, false);
            Log.d("add room test", "add card");
        }
        return new RoomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {

        View redPoint = holder.itemView.findViewById(R.id.room_red_point);

        holder.itemView.setOnClickListener(v -> {
            if (redPoint != null && redPoint.getVisibility() == View.VISIBLE) {
                redPoint.setVisibility(View.GONE);
                return;
            }
            if (position < mRooms.size()) {
                startStorageSpacePage(mRooms.get(position).id, ((MainPageActivity) mContext).mUser,
                        mRooms.get(position).storage_spaces);
            } else if (position == mRooms.size()) {
                mCardListener.onAddCardClick();
            }
        });

        if (position == mRooms.size()) return;

        holder.txtRoomName.setText(mRooms.get(position).getName());
        if (mRooms.get(position).image != null) {
            holder.imgRoom.setImageURI(mRooms.get(position).image);
        }
        if (mRooms.get(position).storage_spaces != null) {
            holder.txtStorageSpaceNo.setText(mRooms.get(position).storage_spaces.size() + "个储物空间");
        }

        holder.itemView.setOnLongClickListener(v -> {
            redPoint.setVisibility(View.VISIBLE);
            return true;
        });
        redPoint.setOnClickListener(v -> {
            mCardListener.onRedPointClick(position);
            redPoint.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        //Log.d("room adapter test", mRooms.toString());
        if (mRooms == null) return 0;
        return mRooms.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mRooms.size()) {
            Log.d("add room test", "add card type");
            return ADD_ROOM_CARD;
        } else {

            return ROOM_CARD;
        }
    }

    public void setOnRoomCardClickListener(CardListener cardListener) {
        this.mCardListener = cardListener;
    }

    public void startStorageSpacePage(int roomId, User user, List<StorageSpace> storageSpaces) {
        Log.d("room_adapter", "user = " + user);
        Intent intent = new Intent(mContext, StorageSpaceActivity.class);
        intent.putExtra("storage_space", (Serializable) storageSpaces);
        intent.putExtra("room_id", roomId);
        intent.putExtra("room_name", findRoomName(roomId));
        intent.putExtra("user", (Parcelable) user);
        mContext.startActivity(intent);
    }

    private String findRoomName(int roomId) {
        for (int i = 0; i < mRooms.size(); i++) {
            if (mRooms.get(i).getId() == roomId) return mRooms.get(i).getName();
        }
        return "";
    }

    public void updateRooms(List<Room> rooms) {
        mRooms = rooms;
        notifyDataSetChanged();
        Log.d("roomadapter  ", "updaterooms");
    }
}