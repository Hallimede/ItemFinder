package com.example.t.view.set;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t.MainActivity;
import com.example.t.MyApplication;
import com.example.t.R;
import com.example.t.model.User;
import com.example.t.util.MyConstants;
import com.example.t.util.StringUtil;
import com.example.t.view.animator.ColoredToast;

public class SetActivity extends AppCompatActivity {

    private static final String TAG = SetActivity.class.getSimpleName();

    private User mUser;
    private PopupWindow mLockPopWindow;
    private PopupWindow mFontPopWindow;
    private PopupWindow mThemePopWindow;
    public SharedPreferences mSharedPreferences;
    private Toast mToast;

    private Button btnStandard;
    private Button btnBigger;
    private Button btnBiggest;
    private Button btnOld;

    private Button btnBlue;
    private Button btnGreen;
    private Button btnRed;
    private Button btnThemeOld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initStatusBar();
        mSharedPreferences = getSharedPreferences(MyConstants.SP_NAME, MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        mUser = getIntent().getParcelableExtra("user");

        TextView txtLockStatus = findViewById(R.id.lock_status);
        TextView txtFontStatus = findViewById(R.id.font_status);
        TextView txtThemeStatus = findViewById(R.id.theme_status);

        // 字体状态
        String fontType = mSharedPreferences.getString(MyConstants.FONT_TYPE, "标准");
        txtFontStatus.setText(fontType);

        // 主题状态
        String themeType = mSharedPreferences.getString(MyConstants.THEME_TYPE, "经典蓝");
        txtThemeStatus.setText(themeType);

        // 安全锁状态
        String lockPassword = mSharedPreferences.getString(MyConstants.LOCK_PASSWORD, "");
        if (lockPassword != null && !lockPassword.equals("")) {
            // 有锁
            txtLockStatus.setText("已设置");
            findViewById(R.id.show_set_lock).setClickable(false);
        }

        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
        findViewById(R.id.show_set_lock).setOnClickListener(v -> showLockPopupWindow(txtLockStatus));
        findViewById(R.id.show_set_theme).setOnClickListener(v -> showThemePopupWindow(txtThemeStatus));
        findViewById(R.id.show_set_font).setOnClickListener(v -> showFontPopupWindow(txtFontStatus));

        findViewById(R.id.logout_btn).setOnClickListener(v->{
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initStatusBar() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.bg_light_gray));

        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void showLockPopupWindow(TextView status) {
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.add_lock_popup, null);
        mLockPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mLockPopWindow.setContentView(contentView);
        //让popup window覆盖状态栏
        mLockPopWindow.setClippingEnabled(false);
        mLockPopWindow.setAnimationStyle(R.style.contextMenuAnim);

        View mask = contentView.findViewById(R.id.set_lock_mask);
        mask.setOnClickListener(v -> {
            mLockPopWindow.dismiss();
        });

        View panel = contentView.findViewById(R.id.set_lock_panel);
        panel.setOnClickListener(v -> {

        });

        // 设置各个控件的点击响应
        TextView txtLockNumber = contentView.findViewById(R.id.lock_number);
        Button btnSetLock = contentView.findViewById(R.id.set_lock_btn);
        View btnCloseSet = contentView.findViewById(R.id.close_set_lock);

        btnSetLock.setOnClickListener(v -> {
            String password = txtLockNumber.getText().toString();
            // 判断合法性
            if (!StringUtil.isAllNumValid(password, 3)) {
                //showTip("密码不合法");
                ColoredToast.showToast(this,"密码只能输入三位数字", false,
                        getResources().getColor(R.color.white), getResources().getColor(R.color.warning_red));
                return;
            }
            // 存储在本地
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            if (!TextUtils.isEmpty(password)) {
                editor.putString(MyConstants.LOCK_PASSWORD, password);
                Log.d(TAG, "add lock password to sp");
            }
            editor.apply();

            status.setText("已设置");

            mLockPopWindow.dismiss();
        });

        btnCloseSet.setOnClickListener(v -> {
            mLockPopWindow.dismiss();
        });

        //显示PopupWindow
        View rootview = LayoutInflater.from(this).inflate(R.layout.activity_set, null);
        mLockPopWindow.showAsDropDown(rootview, 0, 0);

        mask.setAlpha(0f);
        mask.setVisibility(View.VISIBLE);
        mask.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);
    }

    private void showFontPopupWindow(TextView status) {
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.set_font_popup, null);
        mFontPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mFontPopWindow.setContentView(contentView);
        //让popup window覆盖状态栏
        mFontPopWindow.setClippingEnabled(false);
        mFontPopWindow.setAnimationStyle(R.style.contextMenuAnim);

        View mask = contentView.findViewById(R.id.set_font_mask);
        mask.setOnClickListener(v -> {
            mFontPopWindow.dismiss();
        });

        View panel = contentView.findViewById(R.id.set_font_panel);
        panel.setOnClickListener(v -> {

        });

        // 设置各个控件的点击响应
        btnStandard = contentView.findViewById(R.id.btn_standard);
        btnBigger = contentView.findViewById(R.id.btn_bigger);
        btnBiggest = contentView.findViewById(R.id.btn_biggest);
        Button btnSetFont = contentView.findViewById(R.id.set_font_btn);
        View btnCloseSet = contentView.findViewById(R.id.close_set_font);

        switch (status.getText().toString()) {
            case "较大":
                btnOld = btnBigger;
                break;
            case "更大":
                btnOld = btnBiggest;
                break;
            default:
                btnOld = btnStandard;
                break;
        }

        btnOld.setBackground(getDrawable(R.drawable.choice_selected));
        btnOld.setTextColor(getResources().getColor(R.color.white));

        btnStandard.setOnClickListener(v -> {
            if (btnOld == btnStandard) return;
            updateFontButtons(btnStandard);
            btnOld = btnStandard;
        });
        btnBigger.setOnClickListener(v -> {
            if (btnOld == btnBigger) return;
            updateFontButtons(btnBigger);
            btnOld = btnBigger;
        });
        btnBiggest.setOnClickListener(v -> {
            if (btnOld == btnBiggest) return;
            updateFontButtons(btnBiggest);
            btnOld = btnBiggest;
        });

        btnSetFont.setOnClickListener(v -> {
            String fontType = btnOld.getText().toString();
            status.setText(btnOld.getText());

            // 存储在本地
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            if (!TextUtils.isEmpty(fontType)) {
                editor.putString(MyConstants.FONT_TYPE, fontType);
                Log.d(TAG, "add font type to sp");
            }
            editor.apply();

            float size;
            switch (fontType) {
                case "较大":
                    size = 1.2f;
                    break;
                case "更大":
                    size = 1.4f;
                    break;
                default:
                    size = 1.0f;
                    break;
            }
            MyApplication.setAppFontSize(size);

            mFontPopWindow.dismiss();
        });

        btnCloseSet.setOnClickListener(v -> {
            mFontPopWindow.dismiss();
        });

        //显示PopupWindow
        View rootview = LayoutInflater.from(this).inflate(R.layout.activity_set, null);
        mFontPopWindow.showAsDropDown(rootview, 0, 0);

        mask.setAlpha(0f);
        mask.setVisibility(View.VISIBLE);
        mask.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);
    }

    private void updateFontButtons(Button current) {
        current.setBackground(getDrawable(R.drawable.choice_selected));
        current.setTextColor(getResources().getColor(R.color.white));
        btnOld.setBackground(getDrawable(R.drawable.choice_not_selected));
        btnOld.setTextColor(getResources().getColor(R.color.font_gray));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showThemePopupWindow(TextView status) {
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.set_theme_popup, null);
        mThemePopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mThemePopWindow.setContentView(contentView);
        //让popup window覆盖状态栏
        mThemePopWindow.setClippingEnabled(false);
        mThemePopWindow.setAnimationStyle(R.style.contextMenuAnim);

        View mask = contentView.findViewById(R.id.set_theme_mask);
        mask.setOnClickListener(v -> {
            mThemePopWindow.dismiss();
        });

        View panel = contentView.findViewById(R.id.set_theme_panel);
        panel.setOnClickListener(v -> {

        });

        // 设置各个控件的点击响应
        btnBlue = contentView.findViewById(R.id.btn_blue);
        btnGreen = contentView.findViewById(R.id.btn_green);
        btnRed = contentView.findViewById(R.id.btn_red);
        Button btnSetTheme = contentView.findViewById(R.id.set_theme_btn);
        View btnCloseSet = contentView.findViewById(R.id.close_set_theme);

        switch (status.getText().toString()) {
            case MyConstants.THEME_GREEN:
                btnThemeOld = btnGreen;
                Log.d(TAG,"theme status: green");
                break;
            case MyConstants.THEME_RED:
                btnThemeOld = btnRed;
                Log.d(TAG,"theme status: red");
                break;
            default:
                btnThemeOld = btnBlue;
                Log.d(TAG,"theme status: blue");
                break;
        }
        Log.d(TAG,"theme status: "+ status.getText().toString());

        btnThemeOld.setBackground(getDrawable(R.drawable.choice_selected));
        btnThemeOld.setTextColor(getResources().getColor(R.color.white));

        btnBlue.setOnClickListener(v -> {
            if (btnThemeOld == btnBlue) return;
            updateThemeButtons(btnBlue);
            btnThemeOld = btnBlue;
        });
        btnGreen.setOnClickListener(v -> {
            if (btnThemeOld == btnGreen) return;
            updateThemeButtons(btnGreen);
            btnThemeOld = btnGreen;
        });
        btnRed.setOnClickListener(v -> {
            if (btnThemeOld == btnRed) return;
            updateThemeButtons(btnRed);
            btnThemeOld = btnRed;
        });

        btnSetTheme.setOnClickListener(v -> {
            String themeType = btnThemeOld.getText().toString();
            status.setText(btnThemeOld.getText());

            // 存储在本地
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            if (!TextUtils.isEmpty(themeType)) {
                editor.putString(MyConstants.THEME_TYPE, themeType);
                Log.d(TAG, "add theme type to sp");
            }
            editor.apply();

            int themeId;
            switch (themeType) {
                case MyConstants.THEME_GREEN:
                    themeId = R.style.GreenTheme;
                    break;
                case MyConstants.THEME_RED:
                    themeId = R.style.RedTheme;
                    break;
                default:
                    themeId = R.style.BlueTheme;
                    break;
            }
            MyApplication.setAppThemeColor(themeId);

            mThemePopWindow.dismiss();
        });

        btnCloseSet.setOnClickListener(v -> {
            mThemePopWindow.dismiss();
        });

        //显示PopupWindow
        View rootview = LayoutInflater.from(this).inflate(R.layout.activity_set, null);
        mThemePopWindow.showAsDropDown(rootview, 0, 0);

        mask.setAlpha(0f);
        mask.setVisibility(View.VISIBLE);
        mask.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);
    }

    private void updateThemeButtons(Button current) {
        current.setBackground(getDrawable(R.drawable.choice_selected));
        //current.setBackground(getResources().getDrawable(R.drawable.choice_selected));
        current.setTextColor(getResources().getColor(R.color.white));
        btnThemeOld.setBackground(getDrawable(R.drawable.choice_not_selected));
        btnThemeOld.setTextColor(getResources().getColor(R.color.font_gray));
    }

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

}