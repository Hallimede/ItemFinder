package com.example.t.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.t.R;
import com.example.t.model.Inventory;
import com.example.t.model.Item;
import com.example.t.model.Room;
import com.example.t.model.StorageSpace;
import com.example.t.model.User;
import com.example.t.msc.MscController;
import com.example.t.network.ServiceCreator;
import com.example.t.network.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    public List<Item> mItems = null;
    public List<Room> mRooms = null;
    public List<StorageSpace> mStorageSpaces = null;
    public User mUser;
    public UserService userService = (UserService) ServiceCreator.INSTANCE.create(UserService.class);

    public MscController msc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = getIntent().getParcelableExtra("user");
        Log.d(TAG, "mUser = " + mUser.toString());
        msc = new MscController(this);
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public void loadData() {
        getItems();
        getRooms();
        getAllStorageSpaces();
    }

    public void getItems() {
        Call<List<Item>> call = userService.getItems(mUser.getId());

        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                mItems = response.body();
                Log.d(TAG, "mItems 请求成功信息: " + response.body());
                msc.setmItems(mItems);
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, Throwable t) {
                Log.d(TAG, "mItems 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void getRooms() {
        Call<List<Room>> call = userService.getRooms(mUser.getId());
        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(@NonNull Call<List<Room>> call, @NonNull Response<List<Room>> response) {
                mRooms = response.body();
                Log.d(TAG, "mRooms 请求成功信息: " + response.body());
                msc.setmRooms(mRooms);
            }

            @Override
            public void onFailure(@NonNull Call<List<Room>> call, Throwable t) {
                Log.d(TAG, "mRooms 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void getAllStorageSpaces() {
        Call<List<StorageSpace>> call = userService.getAllStorageSpaces(mUser.getId());
        call.enqueue(new Callback<List<StorageSpace>>() {
            @Override
            public void onResponse(@NonNull Call<List<StorageSpace>> call, @NonNull Response<List<StorageSpace>> response) {
                mStorageSpaces = response.body();
                Log.d(TAG, "mStorageSpaces 请求成功信息: " + response.body());
                msc.setmStorageSpaces(mStorageSpaces);
            }

            @Override
            public void onFailure(@NonNull Call<List<StorageSpace>> call, Throwable t) {
                Log.d(TAG, "mStorageSpaces 请求失败信息: " + t.getMessage());
            }
        });
    }

    protected void initStatusBar() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.white));
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    public void getItemInventory(Inventory ivt) {
        Log.d(TAG, "get item inventory . item_id = " + ivt.getItem_id());
        Call<Inventory> call = userService.getItemInventory(mUser.getId(), ivt);
        call.enqueue(new Callback<Inventory>() {
            @Override
            public void onResponse(@NonNull Call<Inventory> call, @NonNull Response<Inventory> response) {
                Log.d(TAG, "get item inventory 请求成功信息: " + response.body());
                msc.findCallBack(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Inventory> call, Throwable t) {
                Log.d(TAG, "get item inventory 请求失败信息: " + t.getMessage());

            }
        });
    }

    public void postItem(Item item) {
        Call<Item> call = userService.postItem(mUser.getId(), item);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                Log.d(TAG, "post item 请求成功信息: " + response.body());
                loadData();
                if (response.body() != null) {
                    msc.speak("您已新建" + item.name);
                } else {
                    msc.speak("抱歉，新建物件失败，请重试");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, Throwable t) {
                Log.d(TAG, "post item 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void postRoom(Room room) {
        Call<Room> call = userService.postRoom(mUser.getId(), room);
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(@NonNull Call<Room> call, @NonNull Response<Room> response) {
                Log.d(TAG, "post room 请求成功信息: " + response.body());
                loadData();
                if (response.body() != null) {
                    msc.speak("您已新建" + room.name);
                } else {
                    msc.speak("抱歉，新建房间失败，请重试");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Room> call, Throwable t) {
                Log.d(TAG, "post room 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void postStorageSpace(StorageSpace space) {
        Call<StorageSpace> call = userService.postStorageSpace(mUser.getId(), space);
        call.enqueue(new Callback<StorageSpace>() {
            @Override
            public void onResponse(@NonNull Call<StorageSpace> call, @NonNull Response<StorageSpace> response) {
                Log.d(TAG, "post StorageSpace 请求成功信息: " + response.body());
                loadData();
                if (response.body() != null) {
                    msc.speak("您已新建" + space.name);
                } else {
                    msc.speak("抱歉，新建储物空间失败，请重试");
                }
            }

            @Override
            public void onFailure(@NonNull Call<StorageSpace> call, Throwable t) {
                Log.d(TAG, "post StorageSpace 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void postInventory(Inventory ivt) {
        Call<Inventory> call = userService.postInventory(mUser.getId(), ivt);
        call.enqueue(new Callback<Inventory>() {
            @Override
            public void onResponse(@NonNull Call<Inventory> call, @NonNull Response<Inventory> response) {
                Log.d(TAG, "put item 请求成功信息: " + response.body());
                loadData();
                if (response.body() != null) {
                    msc.speak("您的物件位置信息已记录");
                } else {
                    msc.speak("抱歉，存物失败，请重试");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Inventory> call, Throwable t) {
                Log.d(TAG, "put item 请求失败信息: " + t.getMessage());
                msc.speak("记录位置信息发生错误，请重试");
            }
        });
    }

    public void deleteItem(int itemId) {
        Call<Item> call = userService.deleteItem(itemId);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                Log.d(TAG, "del item 请求成功信息: " + response.body());
                loadData();
                msc.speak("您的物件已删除");
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, Throwable t) {
                Log.d(TAG, "del item 请求失败信息: " + t.getMessage());
                msc.speak("您的物件删除失败，请重试");
            }
        });
    }

    public void deleteRoom(int roomId) {
        Call<Room> call = userService.deleteRoom(roomId);
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(@NonNull Call<Room> call, @NonNull Response<Room> response) {
                Log.d(TAG, "del room 请求成功信息: " + response.body());
                loadData();
                msc.speak("您的房间已删除");
            }

            @Override
            public void onFailure(@NonNull Call<Room> call, Throwable t) {
                Log.d(TAG, "del room 请求失败信息: " + t.getMessage());
                msc.speak("您的房间删除失败，请重试");
            }
        });
    }

    public void deleteStorageSpace(int spaceId) {
        Call<StorageSpace> call = userService.deleteStorageSpace(spaceId);
        call.enqueue(new Callback<StorageSpace>() {
            @Override
            public void onResponse(@NonNull Call<StorageSpace> call, @NonNull Response<StorageSpace> response) {
                Log.d(TAG, "del storage space 请求成功信息: " + response.body());
                loadData();
                msc.speak("您的储物空间已删除");
            }

            @Override
            public void onFailure(@NonNull Call<StorageSpace> call, Throwable t) {
                Log.d(TAG, "del storage space 请求失败信息: " + t.getMessage());
                msc.speak("您的储物空间删除失败，请重试");
            }
        });
    }

    public void deleteInventory(int itemId) {
        Call<Inventory> call = userService.deleteInventory(itemId);
        call.enqueue(new Callback<Inventory>() {
            @Override
            public void onResponse(@NonNull Call<Inventory> call, @NonNull Response<Inventory> response) {
                Log.d(TAG, "del item ivt 请求成功信息: " + response.body());
                loadData();
                msc.speak("您的物件位置信息已删除");
            }

            @Override
            public void onFailure(@NonNull Call<Inventory> call, Throwable t) {
                Log.d(TAG, "del item ivt 请求失败信息: " + t.getMessage());
                msc.speak("您的物件位置信息删除失败，请重试");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        msc.close();
    }
}
