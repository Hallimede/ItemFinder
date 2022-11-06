package com.example.t;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.t.util.MyConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private List<Activity> activityList;
    private float fontScale;
    private int themeId;
    private SharedPreferences preferences;
    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        init();

        // 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用“,”分隔。
        // 设置你申请的应用appid

        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误

        Fresco.initialize(this);

        StringBuffer param = new StringBuffer();
        param.append("appid=" + getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(MyApplication.this, param.toString());
//        private  void setBaseTheme() {

//            int themeId;
//            switch (themeType) {
//                case "蓝色主题":
//                    themeId = R.style.blueTheme;
//                    break;
//                case "粉色主题":
//                    themeId = R.style.pinkTheme;
//                    break;
//                case "彩色主题":
//                    themeId = R.style.AppTheme;
//                    break;
//                default:
//                    themeId = R.style.blueTheme;
//            }
        setTheme(R.style.GreenTheme);
        super.onCreate();
    }


    private void init() {
        myApplication = this;
        preferences = getSharedPreferences(MyConstants.SP_NAME, MODE_PRIVATE);
        fontScale = getFontScale();
        themeId = getThemeId();
        registerActivityLifecycleCallbacks(this);
    }

    public static float getFontScale() {
        float fontScale = 1.0f;
        if (myApplication != null) {
            fontScale = myApplication.preferences.getFloat("fontScale", 1.0f);
        }
        return fontScale;
    }

    public static int getThemeId() {
        int themeId = R.style.BlueTheme;
        if (myApplication != null) {
            themeId = myApplication.preferences.getInt("themeId", R.style.BlueTheme);
        }
        return themeId;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        if (activityList == null) {
            activityList = new ArrayList<>();
        }
        // 禁止字体大小随系统设置变化
        Resources resources = activity.getResources();
        activity.setTheme(themeId);
        if (resources != null && resources.getConfiguration().fontScale != fontScale) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = fontScale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        activityList.add(activity);
    }

    public static void setAppFontSize(float fontScale) {
        if (myApplication != null) {
            List<Activity> activityList = myApplication.activityList;
            if (activityList != null) {
                for (Activity activity : activityList) {
//                    if (activity instanceof SettingActivity) {
//                        continue;
//                    }
                    Resources resources = activity.getResources();
                    if (resources != null) {
                        android.content.res.Configuration configuration = resources.getConfiguration();
                        configuration.fontScale = fontScale;
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                        activity.recreate();
                        if (fontScale != myApplication.fontScale) {
                            myApplication.fontScale = fontScale;
                            myApplication.preferences.edit().putFloat("fontScale", fontScale).apply();
                        }
                    }
                }
            }
        }
    }

    public static void setAppThemeColor(int themeId) {
        if (myApplication != null) {
            List<Activity> activityList = myApplication.activityList;
            if (activityList != null) {
                for (Activity activity : activityList) {
//                    if (activity instanceof SettingActivity) {
//                        continue;
//                    }
                    Resources resources = activity.getResources();
                    if (resources != null) {
                        activity.setTheme(themeId);
                        Log.d("MyApplication", "set theme id = " + activity.getClass());
//                        android.content.res.Configuration configuration = resources.getConfiguration();
//                        configuration.fontScale = fontScale;
//                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                        activity.recreate();
                        if (themeId != myApplication.themeId) {
                            myApplication.themeId = themeId;
                            myApplication.preferences.edit().putInt("themeId", themeId).apply();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activityList != null) {
            activityList.remove(activity);
        }
    }

}