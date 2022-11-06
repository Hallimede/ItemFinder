package com.example.t.view.storageSpace;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.t.AutoReceiver;
import com.example.t.R;
import com.example.t.model.Inventory;
import com.example.t.model.Item;
import com.example.t.model.Reminder;
import com.example.t.model.Reminders;
import com.example.t.model.Space;
import com.example.t.model.StorageSpace;
import com.example.t.msc.RecordListener;
import com.example.t.util.IdNameHelper;
import com.example.t.util.MyConstants;
import com.example.t.view.BaseActivity;
import com.example.t.view.animator.MyAlertDialog;
import com.example.t.view.item.ItemDetailWindow;
import com.example.t.view.room.CardListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StorageSpaceActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = StorageSpaceActivity.class.getSimpleName();

    private List<StorageSpace> mRoomStorageSpaces;
    private int roomId;
    private String roomName;
    public Reminders reminders;

    private RecyclerView storageSpaceRcl;
    private StorageSpaceAdapter mSpaceAdapter;
    private PopupWindow mSpacePopWindow;
    private PopupWindow mItemPopWindow;
    private View btnPageAddSpace;
    private View mSpaceMask;
    private View mItemMask;
    private TextView audioText;
    private TextView txtSpaceName;
    private ImageView spaceImageView;
    private TextView txtAddItemName;
    private TextView txtAddItemInfo;
    private ImageView itemImageView;

    private String imagePath;
    private int imageFrom = MyConstants.IMAGE_NULL;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_space_page);

        Log.d(TAG, this.toString() + "---onCreate");
        mSharedPreferences = getSharedPreferences(MyConstants.SP_NAME, MODE_PRIVATE);

        // 从 Intent 获取数据
        mRoomStorageSpaces = (List<StorageSpace>) getIntent().getSerializableExtra("storage_space");
        roomId = getIntent().getIntExtra("room_id", 0);
        roomName = getIntent().getStringExtra("room_name");

        getReminders();
        // 加载每个存储空间的 items
        loadAllInventory();
        initialLayout();
    }

    private void initialLayout() {
        initStatusBar();
        // 设置标题
        TextView pageTitle = findViewById(R.id.storage_page_title);
        pageTitle.setText(roomName);

        // 初始化 StorageSpace 的 RecyclerView
        storageSpaceRcl = findViewById(R.id.storage_space_recycler);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL);
        storageSpaceRcl.setLayoutManager(layoutManager);
        mSpaceAdapter = new StorageSpaceAdapter(this, mRoomStorageSpaces);
        storageSpaceRcl.setAdapter(mSpaceAdapter);
        mSpaceAdapter.setOnSpaceCardClickListener(new CardListener() {
            @Override
            public void onAddCardClick() {

            }

            @Override
            public void onRedPointClick(int index) {
                int spaceId = mRoomStorageSpaces.get(index).getId();
                MyAlertDialog alert = new MyAlertDialog(StorageSpaceActivity.this, "删除", "确认删除所有的item吗?");
                alert.btnOK.setOnClickListener(v -> {
                    deleteStorageSpaceInRoom(spaceId);
                    alert.dismiss();
                });
                alert.btnNotOK.setOnClickListener(v -> {
                    alert.dismiss();
                });
                alert.show(R.layout.storage_space_page);
            }

            @Override
            public void onCardAddClick(int index) {
                int spaceId = mRoomStorageSpaces.get(index).getId();
                showItemPopupWindow(spaceId);
            }
        });

        // 添加按钮
        btnPageAddSpace = findViewById(R.id.add_storage_space);
        btnPageAddSpace.setOnClickListener(this);

        FloatingActionButton fab = findViewById(R.id.space_fab);
        View bubble = findViewById(R.id.space_audio_bubble);
        View audioMask = findViewById(R.id.space_audio_mask);
        audioText = findViewById(R.id.space_isr_text);
        msc.setTextFeed(audioText);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioMask.setVisibility(View.VISIBLE);
                bubble.setVisibility(View.VISIBLE);
                msc.listen(new RecordListener() {
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
    protected void onDestroy() {
        super.onDestroy();
        Gson gson = new Gson();
        String json = gson.toJson(reminders);
        Log.d(TAG, "on destroy: " + json);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (!TextUtils.isEmpty(json)) {
            editor.putString(MyConstants.REMINDERS, json);
            Log.d(TAG, "on destroy: put string ");
        }
        editor.apply();
        Log.d(TAG, this.toString() + "---onDestroy");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_storage_space:
                showSpacePopupWindow();
        }
    }

    private void loadAllInventory() {
        for (int i = 0; i < mRoomStorageSpaces.size(); i++) {
            getInventoryBySpace(new Inventory(roomId, mRoomStorageSpaces.get(i).getId()));
        }
    }

    public void showItemDetailWindow(Inventory ivt) {
        ItemDetailWindow window = new ItemDetailWindow(this, ivt);
        window.show(R.layout.storage_space_page);
    }

    private void showSpacePopupWindow() {
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.add_space_popup, null);
        mSpacePopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mSpacePopWindow.setContentView(contentView);
        mSpacePopWindow.setClippingEnabled(false);
        mSpacePopWindow.setAnimationStyle(R.style.contextMenuAnim);

        mSpaceMask = contentView.findViewById(R.id.add_space_mask);
        mSpaceMask.setOnClickListener(v -> {
//            maskFadeOut();
            mSpacePopWindow.dismiss();
        });

        View panel = contentView.findViewById(R.id.add_space_panel);
        panel.setOnClickListener(v -> {

        });

        //设置各个控件的点击响应
        txtSpaceName = contentView.findViewById(R.id.add_space_name);
        Button btnAddSpace = contentView.findViewById(R.id.add_space_btn);
        View btnCloseAddSpace = contentView.findViewById(R.id.close_add_space);

        btnAddSpace.setOnClickListener(v -> {

            if (imagePath == null) {
                Log.d(TAG, "add space no image");
                String name = txtSpaceName.getText().toString();
                StorageSpace space = new StorageSpace(name, roomId);
                postStorageSpaceInRoom(space);
            } else {
                Log.d(TAG, "add space image:" + imagePath);
                uploadPic(imagePath, 0);
            }
            mSpacePopWindow.dismiss();
        });

        btnCloseAddSpace.setOnClickListener(v -> {
            mSpacePopWindow.dismiss();
        });

        //显示PopupWindow
        View rootview = LayoutInflater.from(this).inflate(R.layout.storage_space_page, null);
        mSpacePopWindow.showAsDropDown(rootview, 0, 0);

        contentView.findViewById(R.id.add_space_pic).setOnClickListener(v -> {

            verifyStoragePermissions(this);

            //相册
            Intent localIntent = new Intent();
            localIntent.setType("image/*");
            localIntent.setAction("android.intent.action.GET_CONTENT");
            Intent localIntent2 = Intent.createChooser(localIntent, "选择图片");
            startActivityForResult(localIntent2, PHOTO_PICKED_WITH_DATA);
            imageFrom = MyConstants.ADD_SPACE;

            //拍照
//            try {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_WITH_DATA);
//            } catch (ActivityNotFoundException e) {
//                e.printStackTrace();
//            }
        });

        spaceImageView = contentView.findViewById(R.id.space_image);

        maskFadeIn(mSpaceMask);
    }

    private void showItemPopupWindow(int spaceId) {
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.add_item_popup, null);
        mItemPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mItemPopWindow.setContentView(contentView);
        mItemPopWindow.setClippingEnabled(false);
        mItemPopWindow.setAnimationStyle(R.style.contextMenuAnim);

        mItemMask = contentView.findViewById(R.id.add_item_mask);
        mItemMask.setOnClickListener(v -> {
//            maskFadeOut();
            mItemPopWindow.dismiss();
        });

        View panel = contentView.findViewById(R.id.add_item_panel);
        panel.setOnClickListener(v -> {

        });

        //设置各个控件的点击响应
        txtAddItemName = contentView.findViewById(R.id.add_item_name);
        txtAddItemInfo = contentView.findViewById(R.id.add_item_info);
        Button btnAddItem = contentView.findViewById(R.id.add_item_btn);
        View btnCloseAddItem = contentView.findViewById(R.id.close_add_item);

        btnAddItem.setOnClickListener(v -> {
            Log.d(TAG, "img_path = " + imagePath);
            if (imagePath == null) {
                String name = txtAddItemName.getText().toString();
                Item item = new Item(name);
                Space space = new Space(roomId, spaceId);
                postItemInRoom(item, space);
            } else {
                uploadPic(imagePath, spaceId);
            }
            mItemPopWindow.dismiss();
        });

        btnCloseAddItem.setOnClickListener(v -> {
            mItemPopWindow.dismiss();
        });

        //显示PopupWindow
        View rootview = LayoutInflater.from(this).inflate(R.layout.storage_space_page, null);
        mItemPopWindow.showAsDropDown(rootview, 0, 0);

        maskFadeIn(mItemMask);

        contentView.findViewById(R.id.add_item_pic).setOnClickListener(v -> {

            verifyStoragePermissions(this);

            //相册
//            Intent localIntent = new Intent();
//            localIntent.setType("image/*");
//            localIntent.setAction("android.intent.action.GET_CONTENT");
//            Intent localIntent2 = Intent.createChooser(localIntent, "选择图片");
//            startActivityForResult(localIntent2, PHOTO_PICKED_WITH_DATA);

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);

            imageFrom = MyConstants.ADD_ITEM;

            //拍照
//            try {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_WITH_DATA);
//            } catch (ActivityNotFoundException e) {
//                e.printStackTrace();
//            }
        });

        itemImageView = contentView.findViewById(R.id.item_image);

    }

    /*用来标识请求照相功能的activity*/
    private static final int CAMERA_WITH_DATA = 1001;
    /*用来标识请求gallery的activity*/
    private static final int PHOTO_PICKED_WITH_DATA = 1002;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA: //从本地选择图片
                Uri selectedImageUri = data.getData();
                Log.d(TAG, "image url = " + selectedImageUri);
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, filePathColumn, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
//                Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imagePath = cursor.getString(columnIndex);
                    cursor.close();
                    Log.d(TAG, "image path = " + imagePath);
                }
                switch (imageFrom) {
                    case MyConstants.ADD_ITEM:
                        itemImageView.setImageURI(selectedImageUri);
                        break;
                    case MyConstants.ADD_SPACE:
                        spaceImageView.setImageURI(selectedImageUri);
                        break;
                    default:
                        break;
                }

                break;
            case CAMERA_WITH_DATA:  //拍照
                Bundle bundle = data.getExtras();

//                Uri selectedImageUri = data.getData();
//                Log.d(TAG, "url = " + selectedImageUri);
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
//                if (cursor != null) {
//                    cursor.moveToFirst();
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String imagePath = cursor.getString(columnIndex);
//                    testPicUpload(imagePath);
//                }
                break;
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

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

    public void getReminders() {
        String listJson = getSharedPreferences(MyConstants.SP_NAME, MODE_PRIVATE).getString(MyConstants.REMINDERS, "");
        Gson gson = new Gson();
        reminders = gson.fromJson(listJson, Reminders.class);
        if (reminders == null) {
            reminders = new Reminders();
            Log.d(TAG, "get reminders null");
        } else {
            Log.d(TAG, "get reminders not null");
        }
    }

    public void setReminder(Reminder reminder) {
        getItemsInRoom(reminder);
    }

    public void setReminderCallBack(Reminder reminder, String itemName) {

        if (reminder.repeatType == MyConstants.EVERY_DAY) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE), reminder.hour, reminder.min, 0);
            calendar.getTimeInMillis();

            String currentDateTimeString = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US).
                    format(calendar.getTime());
            Log.d("timer", currentDateTimeString);
            Log.d("timer", calendar.getTimeInMillis() + "");
            Log.d("timer", System.currentTimeMillis() + "");

            Intent intent = new Intent(this, AutoReceiver.class);
            intent.setAction("ITEM_TIMER");
            intent.putExtra("item_name", itemName);
            intent.putExtra("room_name", reminder.roomName);
            intent.putExtra("space_name", reminder.storageSpaceName);
            intent.putExtra("ivt_info", reminder.ivtInfo);
            // PendingIntent这个类用于处理即将发生的事情
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            // AlarmManager.ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用相对时间
            // SystemClock.elapsedRealtime()表示手机开始到现在经过的时间

            // 每过一分钟发起一次提醒方便测试
            am.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), /*24 * 60 * */60 * 1000, sender);

            reminders.items.add(reminder);
            Log.d(TAG, "set reminder");
        }

    }

    public Reminder findReminderById(int itemId) {
        if (reminders == null) return null;
        for (int i = 0; i < reminders.items.size(); i++) {
            if (reminders.items.get(i).itemId == itemId) return reminders.items.get(i);
        }
        return null;
    }

    public void uploadPic(String path, int spaceId) {
        File file = new File(path);
        String fileNameByTimeStamp = "new_pic.jpg";
        Log.d(TAG, "file test :" + file.getAbsolutePath());
        Log.d(TAG, "file test :" + file.getPath());

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileNameByTimeStamp, requestFile);
        //funName
        RequestBody funName = RequestBody.create(null, "ict_uploadpicture");
        Call<String> call = userService.uploadPic(funName, body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.d(TAG, "upload pic 请求成功信息: " + response.body());

                switch (imageFrom) {
                    case MyConstants.ADD_ITEM:
                        String name = txtAddItemName.getText().toString();
                        Item item = new Item(name, response.body());
                        Space space = new Space(roomId, spaceId);
                        postItemInRoom(item, space);
                        break;
                    case MyConstants.ADD_SPACE:
                        String newName = txtSpaceName.getText().toString();
                        StorageSpace newSpace = new StorageSpace(newName, roomId, response.body());
                        postStorageSpaceInRoom(newSpace);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, Throwable t) {
                Log.d(TAG, "upload pic 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void postStorageSpaceInRoom(StorageSpace space) {
        Call<StorageSpace> call = userService.postStorageSpace(mUser.getId(), space);
        call.enqueue(new Callback<StorageSpace>() {
            @Override
            public void onResponse(@NonNull Call<StorageSpace> call, @NonNull Response<StorageSpace> response) {
                Log.d(TAG, "post StorageSpace 请求成功信息: " + response.body());
                getStorageSpacesInRoom();
            }

            @Override
            public void onFailure(@NonNull Call<StorageSpace> call, Throwable t) {
                Log.d(TAG, "post StorageSpace 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void getStorageSpacesInRoom() {
        StorageSpace space = new StorageSpace(roomId);
        Call<List<StorageSpace>> call = userService.getStorageSpaces(mUser.getId(), space);
        call.enqueue(new Callback<List<StorageSpace>>() {
            @Override
            public void onResponse(@NonNull Call<List<StorageSpace>> call, @NonNull Response<List<StorageSpace>> response) {
                //List<StorageSpace> storageSpaces = response.body();
                Log.d(TAG, "room_id = " + space.getRoom_id() + " 的 storageSpaces 请求成功信息: " + response.body());
                mRoomStorageSpaces = response.body();
                mSpaceAdapter.updateSpaces(mRoomStorageSpaces);
            }

            @Override
            public void onFailure(@NonNull Call<List<StorageSpace>> call, Throwable t) {
                Log.d(TAG, "room_id = " + space.getRoom_id() + " 的 storageSpaces 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void deleteStorageSpaceInRoom(int spaceId) {
        Call<StorageSpace> call = userService.deleteStorageSpace(spaceId);
        call.enqueue(new Callback<StorageSpace>() {
            @Override
            public void onResponse(@NonNull Call<StorageSpace> call, @NonNull Response<StorageSpace> response) {
                Log.d(TAG, "del storage space 请求成功信息: " + response.body());
                getStorageSpacesInRoom();
            }

            @Override
            public void onFailure(@NonNull Call<StorageSpace> call, Throwable t) {
                Log.d(TAG, "del storage space 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void getItemsBySpace(Space space) {
        Call<List<Item>> call = userService.getItemsBySpace(mUser.getId(), space);
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                Log.d(TAG, "get items by space_id = " + space.getStorage_space_id() + " 请求成功信息: " + response.body());
                //mSpaceAdapter.updateItems(space.getStorage_space_id(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, Throwable t) {
                Log.d(TAG, "get items by space 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void getInventoryBySpace(Inventory ivt) {
        Call<List<Inventory>> call = userService.getInventoryBySpace(mUser.getId(), ivt);
        call.enqueue(new Callback<List<Inventory>>() {
            @Override
            public void onResponse(@NonNull Call<List<Inventory>> call, @NonNull Response<List<Inventory>> response) {
                Log.d(TAG, "get ivts by space_id = " + ivt.storage_space_id + " 请求成功信息: " + response.body());
                mSpaceAdapter.updateInventoryData(ivt.storage_space_id, response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Inventory>> call, Throwable t) {
                Log.d(TAG, "get ivts by space_id = " + ivt.storage_space_id + "请求失败信息: " + t.getMessage());
            }
        });
    }

    public void postItemInRoom(Item item, Space space) {
        Call<Item> call = userService.postItem(mUser.getId(), item);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                Log.d(TAG, "post item 请求成功信息: " + response.body());
                Item item = response.body();
                Inventory ivt = new Inventory(item.getId(), space.getRoom_id(),
                        space.getStorage_space_id(), txtAddItemInfo.getText().toString());
                postInventoryInRoom(ivt);
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, Throwable t) {
                Log.d(TAG, "post item 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void postInventoryInRoom(Inventory ivt) {
        Log.d(TAG, "post inventory 请求: " + ivt.toString());
        Call<Inventory> call = userService.postInventory(mUser.getId(), ivt);
        call.enqueue(new Callback<Inventory>() {
            @Override
            public void onResponse(@NonNull Call<Inventory> call, @NonNull Response<Inventory> response) {
                Log.d(TAG, "post inventory 请求成功信息: " + response.body());
//                getItemsBySpace(new Space(ivt.getRoom_id(), ivt.getStorage_space_id()));
                getInventoryBySpace(new Inventory(ivt.getRoom_id(), ivt.getStorage_space_id()));
            }

            @Override
            public void onFailure(@NonNull Call<Inventory> call, Throwable t) {
                Log.d(TAG, "ppost inventory 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void getItemInventoryInRoom(Inventory ivt) {
        Call<Inventory> call = userService.getItemInventory(mUser.getId(), ivt);
        call.enqueue(new Callback<Inventory>() {
            @Override
            public void onResponse(@NonNull Call<Inventory> call, @NonNull Response<Inventory> response) {
                Log.d(TAG, "get item inventory 请求成功信息: " + response.body());
                showItemDetailWindow(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Inventory> call, Throwable t) {
                Log.d(TAG, "get item inventory 请求失败信息: " + t.getMessage());

            }
        });
    }

    public void getItemsInRoom(Reminder reminder) {
        Call<List<Item>> call = userService.getItems(mUser.getId());

        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                List<Item> items = response.body();
                setReminderCallBack(reminder, IdNameHelper.findNameById(reminder.itemId, items));
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, Throwable t) {
                Log.d(TAG, "mItems 请求失败信息: " + t.getMessage());
            }
        });
    }

    private void maskFadeIn(View mask) {
        mask.setAlpha(0f);
        mask.setVisibility(View.VISIBLE);
        mask.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);
    }

}
