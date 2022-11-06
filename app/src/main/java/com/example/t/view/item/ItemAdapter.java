package com.example.t.view.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t.R;
import com.example.t.model.Inventory;
import com.example.t.view.room.CardListener;
import com.example.t.view.storageSpace.StorageSpaceActivity;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private Context mContext;
    private List<Inventory> mInventoryList;
    private CardListener mCardListener;

    public ItemAdapter(Context mContext, List<Inventory> inventoryList) {
        this.mContext = mContext;
        this.mInventoryList = inventoryList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_card, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.txtItemName.setText(mInventoryList.get(position).item.name);
        holder.txtItemPosition.setText(mInventoryList.get(position).info);
        if (position == mInventoryList.size() - 1) {
            holder.itemView.findViewById(R.id.item_base_line).setVisibility(View.GONE);
        }
        holder.itemView.setOnLongClickListener(v -> {
            holder.itemView.findViewById(R.id.delete_item).setVisibility(View.VISIBLE);
            return true;
        });
        holder.itemView.setOnClickListener(v -> {
            if (holder.itemView.findViewById(R.id.delete_item).getVisibility() == View.VISIBLE) {
                holder.itemView.findViewById(R.id.delete_item).setVisibility(View.GONE);
                return;
            }
            ((StorageSpaceActivity)mContext).showItemDetailWindow(mInventoryList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return mInventoryList.size();
    }

    public void setOnItemCardClickListener(CardListener cardListener) {
        this.mCardListener = cardListener;
    }

}