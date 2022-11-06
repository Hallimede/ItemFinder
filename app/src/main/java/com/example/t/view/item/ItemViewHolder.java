package com.example.t.view.item;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public TextView txtItemName;
    public TextView txtItemPosition;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        txtItemName = itemView.findViewById(R.id.item_name);
        txtItemPosition = itemView.findViewById(R.id.item_position);
    }
}
