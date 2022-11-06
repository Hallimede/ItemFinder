package com.example.t.util;

import android.util.Log;


import com.example.t.model.Base;

import java.util.List;

public class IdNameHelper {

//    static public Space findSpaceById(int posId, List<Space> spaces) {
//        for (int i = 0; i <= spaces.size() - 1; i++) {
//            if (spaces.get(i).getId() == posId) {
//                return spaces.get(i);
//            }
//        }
//        return null;
//    }

    private static final String TAG = IdNameHelper.class.getSimpleName();

    static public <T> int findIdByName(String name, List<T> list) {
        int id = 0;
        if (name == null) return 0;
        for (int i = 0; i <= list.size() - 1; i++) {
            Base base = (Base) list.get(i);
            if (base.getName().equals(name)) {
                id = base.getId();
                Log.d(TAG, "【findIdByName】" + base.getClass() + "  id = " + id);
                return id;
            }
        }
        return 0;
    }

    static public <T> String findNameById(int id, List<T> list) {
        String name = "";
        if (id <= 0) return name;
        for (int i = 0; i <= list.size() - 1; i++) {
            Base base = (Base) list.get(i);
            if (base.id == id) {
                name = base.getName();
                Log.d(TAG, "【findNameById】" + base.getClass() + "name = " + name);
                return name;
            }
        }
        return name;
    }


}
