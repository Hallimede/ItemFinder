package com.example.t.view.animator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t.R;
import com.example.t.util.DisplayUtil;

public class ColoredToast {

    public static void showToast(@NonNull Context context, String content, boolean longTime, @ColorInt
            int textColor, @ColorInt int toastBackgroundColor) {
        int type = longTime ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, content, type);
        ViewGroup toastView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout
                .toast_colored, null, false);
        if (toastBackgroundColor != 0) {
            toastView.setBackgroundDrawable(getToastBackground(context, toastBackgroundColor));
        }
        TextView textView = (TextView) toastView.findViewById(R.id.toast_message);
        textView.setText(content);
        // 内部已经作非空判断了
        if (textColor != 0) {
            textView.setTextColor(textColor);
        }
        toast.setView(toastView);
//        toast.setGravity(Gravity.TOP,0,150);
        toast.show();
    }

    private static Drawable getToastBackground(@NonNull Context context, @ColorInt int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(DisplayUtil.dp2px(context, 24));
        gradientDrawable.setColor(color);
        return gradientDrawable;
    }

}