package com.example.t.util;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListUtil {
    public static <T> ArrayList<Object> getElementList(List<T> list, String index) {
        Log.d("ListUtil",list.toString());
        ArrayList<Object> res = new ArrayList<>();
        try {
            for (int i = 0; i <= list.size() - 1; i++) {
                Map<String, Object> map = beanToMap(list.get(i));
                res.add( map.get(index));
            }
        } catch (Exception e) {
            Log.d("ListUtil", "error :"+e.getMessage());
        }
        Log.d("ListUtil",res.toString());
        return res;
    }

    public static Map<String, Object> beanToMap(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Log.d("ListUtil", "bean to map: " + field.getName()+" , "+field.get(object));
            map.put(field.getName(), field.get(object));
        }
        return map;
    }

}
