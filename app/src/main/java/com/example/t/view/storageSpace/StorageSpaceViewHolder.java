package com.example.t.view.storageSpace;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class StorageSpaceViewHolder extends RecyclerView.ViewHolder {

    public TextView txtStorageSpaceName;
    public TextView txtItemNo;
    public SimpleDraweeView imgSpace;

    public StorageSpaceViewHolder(@NonNull View itemView) {
        super(itemView);
        txtStorageSpaceName = itemView.findViewById(R.id.storage_space_name);
        txtItemNo = itemView.findViewById(R.id.item_no);
        imgSpace = itemView.findViewById(R.id.storage_space_img);
    }
}