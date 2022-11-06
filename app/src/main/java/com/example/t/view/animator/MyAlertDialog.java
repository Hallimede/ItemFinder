package com.example.t.view.animator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.t.R;

public class MyAlertDialog {

    PopupWindow mPopWindow;
    public Button btnOK;
    public Button btnNotOK;
    private Context mContext;

    public MyAlertDialog(Context context, String title, String info) {
        mContext = context;
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.my_alert_dialog, null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopWindow.setContentView(contentView);
        //让popup window覆盖状态栏
        mPopWindow.setClippingEnabled(false);
        btnOK = contentView.findViewById(R.id.ok_button);
        btnNotOK = contentView.findViewById(R.id.not_ok_button);
        TextView txtTitle = contentView.findViewById(R.id.alert_title);
        txtTitle.setText(title);
        TextView txtInfo = contentView.findViewById(R.id.alert_info);
        txtInfo.setText(info);
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

}
