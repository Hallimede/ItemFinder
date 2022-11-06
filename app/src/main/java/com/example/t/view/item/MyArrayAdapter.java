package com.example.t.view.item;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.t.R;

public class MyArrayAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private String[] mStringArray;

    public MyArrayAdapter(Context context, String[] stringArray) {
        super(context, android.R.layout.simple_spinner_item, stringArray);
        mContext = context;
        mStringArray = stringArray;
        Log.d("spinner", mStringArray[0]);
        Log.d("spinner", mStringArray[1]);
        Log.d("spinner", mStringArray[2]);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        //修改Spinner展开后的字体颜色
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        //此处text1是Spinner默认的用来显示文字的TextView
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(mStringArray[position]);
        Log.d("spinner open", mStringArray[position]);
        tv.setTextSize(R.dimen.font_size_small);
        tv.setTextColor(mContext.getResources().getColor(R.color.font_gray));

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 修改Spinner选择后结果的字体颜色
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        //此处text1是Spinner默认的用来显示文字的TextView
        TextView tv = convertView.findViewById(android.R.id.text1);
        tv.setText(mStringArray[position]);
        Log.d("spinner not open", mStringArray[position]);
        tv.setTextSize(R.dimen.font_size_small);
        tv.setTextColor(Color.BLUE);
        return convertView;
    }

}