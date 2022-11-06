package com.example.t.view.storageSpace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.t.R;
import com.example.t.model.Inventory;
import com.example.t.model.Item;
import com.example.t.model.StorageSpace;
import com.example.t.view.item.ItemAdapter;
import com.example.t.view.room.CardListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageSpaceAdapter extends RecyclerView.Adapter<StorageSpaceViewHolder> {

    private Context mContext;
    private List<StorageSpace> mStorageSpaces;
    private CardListener mCardListener;
    public Map<Integer, List<Item>> mItemsMap = new HashMap<>();
    public Map<Integer, List<Inventory>> mIvtMap = new HashMap<>();
    public ItemAdapter mItemAdapter;

    public StorageSpaceAdapter(Context mContext, List<StorageSpace> storageSpaces) {
        this.mContext = mContext;
        this.mStorageSpaces = storageSpaces;
    }

    @NonNull
    @Override
    public StorageSpaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.storage_space_card, parent, false);
        return new StorageSpaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StorageSpaceViewHolder holder, int position) {
        holder.txtStorageSpaceName.setText(mStorageSpaces.get(position).getName());
        View redPoint = holder.itemView.findViewById(R.id.space_red_point);
        Button btnAddItem = holder.itemView.findViewById(R.id.space_add_item_btn);
        RecyclerView mItemRcl = holder.itemView.findViewById(R.id.item_recycler);
        holder.itemView.findViewById(R.id.storage_space_card).setOnLongClickListener(v -> {
            redPoint.setVisibility(View.VISIBLE);
            return true;
        });
        holder.itemView.findViewById(R.id.storage_space_card).setOnClickListener(v -> {
            if (redPoint.getVisibility() == View.VISIBLE) {
                redPoint.setVisibility(View.GONE);
                return;
            }
            if (mItemRcl.getVisibility() == View.GONE) {
                mItemRcl.setVisibility(View.VISIBLE);
            } else {
                mItemRcl.setVisibility(View.GONE);
            }
        });
        redPoint.setOnClickListener(v -> {
            mCardListener.onRedPointClick(position);
            redPoint.setVisibility(View.GONE);
        });
        btnAddItem.setOnClickListener(v -> {
            mCardListener.onCardAddClick(position);
        });

        if (mStorageSpaces.get(position).image != null) {
            holder.imgSpace.setImageURI(mStorageSpaces.get(position).image);
        }

        List<Inventory> inventoryList = mIvtMap.get(mStorageSpaces.get(position).getId());
        if (inventoryList == null) return;

//        List<Item> items = mItemsMap.get(mStorageSpaces.get(position).getId());
//        if (items == null) return;
        holder.txtItemNo.setText(inventoryList.size()+"个物品");
        mItemAdapter = new ItemAdapter(mContext, inventoryList);
        //线性布局
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL);
        mItemRcl.setLayoutManager(layoutManager);
        mItemRcl.setAdapter(mItemAdapter);

    }

    @Override
    public int getItemCount() {
        return mStorageSpaces.size();
    }

    public void setOnSpaceCardClickListener(CardListener cardListener) {
        this.mCardListener = cardListener;
    }

    public void updateSpaces(List<StorageSpace> storageSpacess) {
        mStorageSpaces = storageSpacess;
        notifyDataSetChanged();
    }

//    public void updateItems(int spaceId, List<Item> items) {
//        mItemsMap.put(spaceId, items);
//        int position = findPositionById(spaceId);
//        if (position > -1) {
//            notifyItemChanged(position);
//        } else {
//            notifyDataSetChanged();
//        }
//    }

    public void updateInventoryData(int spaceId, List<Inventory> inventoryList) {
        mIvtMap.put(spaceId, inventoryList);
        int position = findPositionById(spaceId);
        if (position > -1) {
            notifyItemChanged(position);
        } else {
            notifyDataSetChanged();
        }
    }

    private int findPositionById(int spaceId) {
        for (int i = 0; i < mStorageSpaces.size(); i++) {
            if (mStorageSpaces.get(i).getId() == spaceId) return i;
        }
        return -1;
    }
}