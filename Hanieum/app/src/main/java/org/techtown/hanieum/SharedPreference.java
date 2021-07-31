package org.techtown.hanieum;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreference {
    public static final String JOB_LIST = "jobList";
    public static final String REGION_LIST = "regionList";
    // chipList를 저장
    public static void setArrayPref(Context context, ArrayList<ChipList> chipList, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chipList);
        editor.putString(key, json);
        editor.commit();
    }

    // chipList를 불러옴
    public static ArrayList<ChipList> getArrayPref(Context context, String key) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(key, null);
        if(json == null){
            setArrayPref(context,new ArrayList<ChipList>(),key);
            json = sharedPrefs.getString(key,null);
        }
        Type type = new TypeToken<ArrayList<ChipList>>() {
        }.getType();
        ArrayList<ChipList> arrayList = gson.fromJson(json, type);
        return arrayList;
    }
}
