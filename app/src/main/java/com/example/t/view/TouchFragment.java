package com.example.t.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.t.R;
import com.example.t.model.Room;
import com.example.t.model.StorageSpace;
import com.example.t.msc.RecordListener;
import com.example.t.util.MyConstants;
import com.example.t.view.animator.MyAlertDialog;
import com.example.t.view.room.RoomAdapter;
import com.example.t.view.room.CardListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TouchFragment extends Fragment {

    private static final String TAG = TouchFragment.class.getSimpleName();

    private MainPageActivity pageActivity;

    private View view;
    private RoomAdapter mRoomAdapter;
    private RecyclerView roomRcl;

    private PopupWindow mPopWindow;
    private Button btnAddRoom;
    private View mMask;
    private TextView audioText;
    private ImageView roomImageView;
    public String imagePath;
    public int imageFrom = MyConstants.IMAGE_NULL;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageActivity = (MainPageActivity) getActivity();
        pageActivity.setTouchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.touch_page, container, false);
        Log.d(TAG, "break point 1");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomRcl = view.findViewById(R.id.room_recycler);

        //线性布局
        if (mRoomAdapter == null) {

            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL);
            roomRcl.setLayoutManager(layoutManager);
            List<Room> rooms = pageActivity.mRooms;
            mRoomAdapter = new RoomAdapter(this.getContext(), rooms);
            roomRcl.setAdapter(mRoomAdapter);
            mRoomAdapter.setOnRoomCardClickListener(new CardListener() {
                @Override
                public void onAddCardClick() {
                    showPopupWindow();
                }

                @Override
                public void onRedPointClick(int index) {
                    int roomId = pageActivity.mRooms.get(index).getId();
                    MyAlertDialog alert = new MyAlertDialog(getContext(), "删除", "确认删除房间里所有的物件吗?");
                    alert.btnOK.setOnClickListener(v -> {
                        pageActivity.deleteRoom(roomId);
                        alert.dismiss();
                    });
                    alert.btnNotOK.setOnClickListener(v -> alert.dismiss());
                    alert.show(R.layout.touch_page);
                }

                @Override
                public void onCardAddClick(int index) {

                }
            });
        }

        FloatingActionButton fab = view.findViewById(R.id.room_fab);
        View bubble = view.findViewById(R.id.room_audio_bubble);
        View audioMask = view.findViewById(R.id.room_audio_mask);
        audioText = view.findViewById(R.id.room_isr_text);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioMask.setVisibility(View.VISIBLE);
                bubble.setVisibility(View.VISIBLE);
                pageActivity.msc.listen(new RecordListener() {
                    @Override
                    public void onRecordBegin() {

                    }

                    @Override
                    public void onRecordEnd() {

                    }
                });

            }
        });
        audioMask.setOnClickListener(v -> {
            bubble.setVisibility(View.GONE);
            audioMask.setVisibility(View.GONE);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        pageActivity.msc.setTextFeed(audioText);
    }

    public void setSpaces(int roomId, int userId, List<StorageSpace> storageSpaces) {
        //mRoomAdapter.startStorageSpacePage(roomId, userId, storageSpaces);
    }

    public void updateRooms(List<Room> rooms) {
        mRoomAdapter.updateRooms(rooms);
    }

    public int getUserId() {
        return ((MainPageActivity) getActivity()).mUser.getId();
    }

    public void showPopupWindow() {
        //设置contentView
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.add_room_popup, null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopWindow.setContentView(contentView);
        //让popup window覆盖状态栏
        mPopWindow.setClippingEnabled(false);
        mPopWindow.setAnimationStyle(R.style.contextMenuAnim);

        mMask = contentView.findViewById(R.id.add_room_mask);
        mMask.setOnClickListener(v -> {
//            maskFadeOut();
            mPopWindow.dismiss();
        });

        View panel = contentView.findViewById(R.id.add_room_panel);
        panel.setOnClickListener(v -> {

        });

        //设置各个控件的点击响应
        TextView txtRoomName = contentView.findViewById(R.id.add_room_name);
        Button btnAddRoom = contentView.findViewById(R.id.add_room_btn);
        View btnCloseAddRoom = contentView.findViewById(R.id.close_add_room);

        btnAddRoom.setOnClickListener(v -> {
            Log.d(TAG, "img_path = " + imagePath);
            String name = txtRoomName.getText().toString();
            if (imagePath == null) {
                Room room = new Room(name);
                pageActivity.postRoom(room);
            } else {
                pageActivity.uploadPic(imagePath,name);
            }
//            maskFadeOut();
            mPopWindow.dismiss();
        });

        btnCloseAddRoom.setOnClickListener(v -> {
//            maskFadeOut();
            mPopWindow.dismiss();
        });

        //显示PopupWindow
        View rootview = LayoutInflater.from(getContext()).inflate(R.layout.touch_page, null);
        //mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        mPopWindow.showAsDropDown(rootview, 0, 0);

        maskFadeIn();

        contentView.findViewById(R.id.add_room_pic).setOnClickListener(v -> {
            verifyStoragePermissions(pageActivity);

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);

            imageFrom = MyConstants.ADD_ROOM;

        });

        roomImageView = contentView.findViewById(R.id.room_image);
    }

    /*用来标识请求照相功能的activity*/
    private static final int CAMERA_WITH_DATA = 1001;
    /*用来标识请求gallery的activity*/
    private static final int PHOTO_PICKED_WITH_DATA = 1002;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "on activity result");
        Log.d(TAG, resultCode + " : result code");
        Log.d(TAG, requestCode + " : request code");
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA: //从本地选择图片
                Uri selectedImageUri = data.getData();
                Log.d(TAG, "image url = " + selectedImageUri);
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                CursorLoader cursorLoader = new CursorLoader(pageActivity, selectedImageUri, filePathColumn, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
//                Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imagePath = cursor.getString(columnIndex);
                    Log.d(TAG, "image path = " + imagePath);
                    cursor.close();
                }
                if (imageFrom == MyConstants.ADD_ROOM) {
                    roomImageView.setImageURI(selectedImageUri);
                }
                break;
            case CAMERA_WITH_DATA:  //拍照
                Bundle bundle = data.getExtras();
                break;
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
            Log.d(TAG, "request");
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private void maskFadeIn() {
        mMask.setAlpha(0f);
        mMask.setVisibility(View.VISIBLE);
        mMask.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);
    }

    private void maskFadeOut() {
        mMask.setAlpha(1f);
        mMask.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(null);
        mMask.setVisibility(View.INVISIBLE);
    }
}


