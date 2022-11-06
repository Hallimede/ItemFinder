package com.example.t;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class AutoReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_FLAG = 1;
    private NotificationManager manager;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("ITEM_TIMER")) {
            String itemName = intent.getStringExtra("item_name");
            String roomName = intent.getStringExtra("room_name");
            String spaceName = intent.getStringExtra("space_name");
            String ivtInfo = intent.getStringExtra("ivt_info");

            Log.d("auto receiver:",itemName);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class), 0);

            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT <26) {

                // 通过Notification.Builder来创建通知，注意API Level
                // API16之后才支持
                Notification notify = new Notification.Builder(context)
                        .setSmallIcon(R.drawable.logo_round)
                        .setTicker("定时提醒:"+itemName)
                        .setContentTitle("定时提醒:"+itemName)
                        .setContentText("您的"+itemName+"在"+roomName+"的"+spaceName+"的"+ivtInfo+"噢")
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                        .setLights(Color.BLUE, 0, 1)
                        .setAutoCancel(true)
                        .setNumber(1)
                        .build(); // 需要注意build()是在API

                // level16及之后增加的，API11可以使用getNotificatin()来替代
                notify.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
                // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
                manager.notify(NOTIFICATION_FLAG, notify);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示

            }
            else {

                int importance = NotificationManager.IMPORTANCE_HIGH;
                String id = "channel_1"; //自定义设置通道ID属性
                String description = "123";//自定义设置通道描述属性
                NotificationChannel mChannel = new NotificationChannel(id, "123", importance);
                manager.createNotificationChannel(mChannel);//最后在notificationmanager中创建该通知渠道
                Notification notification = new Notification.Builder(context, id)//创建Notification对象。
                        .setContentTitle("定时提醒:"+itemName)  //设置通知标题
                        .setSmallIcon(R.drawable.logo_round)//设置通知小图标
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_round))//设置通知大图标
                        .setContentText("您的"+itemName+"在"+roomName+"的"+spaceName+"的"+ivtInfo+"噢")//设置通知内容
                        .setAutoCancel(true)//设置自动删除通知
                        .build();//运行
                manager.notify((int) System.currentTimeMillis(),notification);

            }

        }
    }
}