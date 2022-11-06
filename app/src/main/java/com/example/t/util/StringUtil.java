package com.example.t.util;

import android.util.Log;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static <T> String join(List<T> list, String joiner){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= list.size() - 1; i++) {
            if (i < list.size() - 1) {
                sb.append(list.get(i) + joiner);
            } else {
                sb.append(list.get(i));
            }
        }
        return sb.toString();
    }

    public static boolean isAllNumValid(String password, int length) {

        if (password.length() > 0) {
            //判断是否有空格字符串
            for (int t = 0; t < password.length(); t++) {
                String b = password.substring(t, t + 1);
                if (b.equals(" ")) {
                    Log.d("StringUtil:","有空字符串");
                    return false;
                }
            }

            //判断是否有汉字
            int count = 0;
            String regEx = "[\\u4e00-\\u9fa5]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(password);
            while (m.find()) {
                for (int i = 0; i <= m.groupCount(); i++) {
                    count = count + 1;
                }
            }

            if(count>0){
                Log.d("StringUtil:","有汉字");
                return false;
            }


            //判断是否是字母和数字
            int numberCounter = 0;
            for (int i = 0; i < password.length(); i++) {
                char c = password.charAt(i);
                if (Character.isLetter(c)) {
                    Log.d("StringUtil:","有字母");
                    return false;
                }
                if (Character.isDigit(c)) {
                    numberCounter++;
                }
            }

            if(numberCounter==length) return true;

        } else {
            return false;
        }
        return true;
    }

    public static String format(String s){
        String str=s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }

}
