package com.example.t.view;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.t.R;
import com.example.t.model.Room;
import com.example.t.util.DensityUtils;
import com.example.t.util.MyConstants;
import com.example.t.view.set.SetActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPageActivity extends BaseActivity{

    private static final String TAG = MainPageActivity.class.getSimpleName();

    private AudioFragment audioFragment;
    public TouchFragment touchFragment;
    private Button btnSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initStatusBar();
        setContentView(com.example.t.R.layout.activity_main_page);
        initLayout();
    }

    private void initLayout() {
        btnSet = findViewById(R.id.set_btn);
        btnSet.setOnClickListener(v -> {
            Intent intent = new Intent(this, SetActivity.class);
            intent.putExtra("user", (Parcelable) mUser);
            startActivity(intent);
        });

        TabLayout tl = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.vp2);
        viewPager2.setAdapter(new FragmentAdapter(this));
        TabLayoutMediator tab = new TabLayoutMediator(tl, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("语音查找");
                        break;
                    case 1:
                        tab.setText("手动查找");
                        break;
                }
            }
        });
        reflex(tl, 25);
        tab.attach();

    }

    public void setAudioFragment() {
        audioFragment = (AudioFragment) getSupportFragmentManager().findFragmentByTag("f0");
        if (audioFragment == null) {
            Log.d(TAG, "获取 AudioFragment = null");
        } else {
            Log.d(TAG, "获取" + audioFragment.toString());
        }
    }

    public void setTouchFragment() {
        touchFragment = (TouchFragment) getSupportFragmentManager().findFragmentByTag("f1");
        if (touchFragment == null) {
            Log.d(TAG, "获取 TouchFragment = null");
        } else {
            Log.d(TAG, "获取" + touchFragment.toString());
        }
    }

    public void uploadPic(String path,String roomName) {
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
                if (touchFragment==null) return;
                if (touchFragment.imageFrom == MyConstants.ADD_ROOM) {
                    Room room = new Room(roomName, response.body());
                    postRoom(room);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, Throwable t) {
                Log.d(TAG, "upload pic 请求失败信息: " + t.getMessage());
            }
        });
    }

    public static void reflex(final TabLayout tabLayout, int heigh) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);
                        //拿到tabView的m TextView 属性  tab 的字数不固定一定用反射取 textView
                        Field mTextViewField = tabView.getClass().getDeclaredField("textView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);
                        tabView.setPadding(0, 0, 0, 0);
                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }
                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = DensityUtils.dp2px(tabLayout.getContext(), heigh);
                        params.rightMargin = DensityUtils.dp2px(tabLayout.getContext(), heigh);
                        tabView.setLayoutParams(params);
                        tabView.invalidate();
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
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
                if (touchFragment != null) {
                    touchFragment.updateRooms(mRooms);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Room>> call, Throwable t) {
                Log.d(TAG, "mRooms 请求失败信息: " + t.getMessage());
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
                if (touchFragment != null)
                    touchFragment.updateRooms(mRooms);
            }

            @Override
            public void onFailure(@NonNull Call<Room> call, Throwable t) {
                Log.d(TAG, "del room 请求失败信息: " + t.getMessage());
                msc.speak("您的房间删除失败，请重试");
            }
        });
    }

}