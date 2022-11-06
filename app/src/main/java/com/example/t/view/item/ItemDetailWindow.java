package com.example.t.view.item;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.t.R;
import com.example.t.model.Inventory;
import com.example.t.model.Item;
import com.example.t.model.Reminder;
import com.example.t.util.MyConstants;
import com.example.t.util.NotificationsUtils;
import com.example.t.view.storageSpace.StorageSpaceActivity;
import com.facebook.drawee.view.SimpleDraweeView;

public class ItemDetailWindow {

    PopupWindow mPopWindow;
    View mContentView;
    Context mContext;
    Inventory mInventory;
    String info;
    String time;
    String every = "每天";
    public View btnClose;
    TextView mTimerDetail;


    private Spinner spinner;
    private ArrayAdapter<String> adapter;

    public ItemDetailWindow(Context context, Inventory ivt) {
        if (ivt == null) return;
        this.mContext = context;
        this.mInventory = ivt;

        mContentView = LayoutInflater.from(mContext).inflate(R.layout.item_detail, null);
        mPopWindow = new PopupWindow(mContentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopWindow.setContentView(mContentView);
        //让popup window覆盖状态栏
        mPopWindow.setClippingEnabled(false);
        btnClose = mContentView.findViewById(R.id.close_item_detail);

        btnClose.setOnClickListener(v -> {
            dismiss();
        });

        if (ivt.item != null) {
            ((TextView) mContentView.findViewById(R.id.item_detail_name)).setText(ivt.item.name);
            if (ivt.info != null) {
                ((TextView) mContentView.findViewById(R.id.item_detail_info)).setText(ivt.info);
            }
            ((TextView) mContentView.findViewById(R.id.item_detail_time)).setText(ivt.getTimeStr() + "存");
            if (ivt.item.image != null) {
                ((SimpleDraweeView) mContentView.findViewById((R.id.item_detail_img))).setImageURI(ivt.item.image);
            }
        }

        initSpinner();

        mContentView.findViewById(R.id.show_add_timer).setOnClickListener(v -> {
            if (!NotificationsUtils.isNotificationEnabled(context)) {
                NotificationsUtils.requestNotify(context);
            }
            mContentView.findViewById(R.id.add_timer_panel).setVisibility(View.VISIBLE);
        });

        mTimerDetail = mContentView.findViewById(R.id.timer_detail_text);
        Reminder timer = ((StorageSpaceActivity) mContext).findReminderById(ivt.item_id);
        if (timer != null) {
            mContentView.findViewById(R.id.show_add_timer).setVisibility(View.GONE);
            mContentView.findViewById(R.id.timer_info).setVisibility(View.VISIBLE);
            mTimerDetail.setText(timer.repeatType + " " + timer.hour + " : " + timer.min + " 提醒");
        }

    }

    public void dismiss() {
        mPopWindow.dismiss();
    }

    public void show(int rootViewId) {
        //显示PopupWindow
        View rootview = LayoutInflater.from(mContext).inflate(rootViewId, null);
        //mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        mPopWindow.showAsDropDown(rootview, 0, 0);
    }

    public void initSpinner() {

        spinner = (Spinner) mContentView.findViewById(R.id.timer_spinner);

        //第二步：为下拉列表定义一个适配器
        adapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, mContext.getResources().getStringArray(R.array.every));
        //第三步：设置下拉列表下拉时的菜单样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        spinner.setAdapter(adapter);
        //第五步：添加监听器，为下拉列表设置事件的响应
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemDetailWindow.this.every = parent.getItemAtPosition(position).toString();
                //String res = parent.getItemAtPosition(position).toString();
                // TODO Auto-generated method stub
                /* 将 spinnertext 显示^*/
                parent.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> argO) {
                // TODO Auto-generated method stub
                argO.setVisibility(View.VISIBLE);
            }
        });

        //将spinnertext添加到OnTouchListener对内容选项触屏事件处理
        spinner.setOnTouchListener(new Spinner.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                // 将mySpinner隐藏
                v.setVisibility(View.INVISIBLE);
                Log.d("spinner", "Spinner Touch事件被触发!");
                return false;
            }
        });
        //焦点改变事件处理
        spinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                v.setVisibility(View.VISIBLE);
                Log.d("spinner", "Spinner FocusChange事件被触发！");
            }
        });

        mContentView.findViewById(R.id.set_timer_btn).setOnClickListener(v -> {

            TextView txtHour = mContentView.findViewById(R.id.timer_hour);
            TextView txtMin = mContentView.findViewById(R.id.timer_min);

            int hour = Integer.parseInt(txtHour.getText().toString());
            int min = Integer.parseInt(txtMin.getText().toString());
            String type;

            switch (every) {
                case "每天":
                    type = MyConstants.EVERY_DAY;
                    break;
                case "每周":
                    type = MyConstants.EVERY_WEEK;
                    break;
                case "每月":
                    type = MyConstants.EVERY_MONTH;
                    break;
                default:
                    type = MyConstants.EVERY_NULL;
                    break;
            }

            Reminder reminder = new Reminder(mInventory.item_id, type, hour, min);
            reminder.addDetails(mInventory.room.name, mInventory.storage_space.name, mInventory.info);
            ((StorageSpaceActivity) mContext).setReminder(reminder);
            mTimerDetail.setText(every + " " + hour + " : " + min + " 提醒");
            mContentView.findViewById(R.id.timer_info).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.show_add_timer).setVisibility(View.GONE);
            mContentView.findViewById(R.id.add_timer_panel).setVisibility(View.GONE);

        });


    }
}

