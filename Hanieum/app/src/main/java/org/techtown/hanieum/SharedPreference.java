package org.techtown.hanieum;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.PhantomReference;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreference {
    public static final String JOB_LIST = "jobList";
    public static final String REGION_LIST = "regionList";
    public static final String JOB_TMP = "jobTmp";
    public static final String REGION_TMP = "regionTmp";
    public static final String WORKFORM_STATUS = "workForm";
    public static final String CAREER_STATUS = "careerStatus";
    public static final String LICENSE_STATUS = "licenseStatus";

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

    // 근무 형태 저장
    public static void setWorkFormPref(Context context, ArrayList<String> arrayList, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        JSONArray jsonArray = new JSONArray();

        for(int i=0;i<arrayList.size();i++) {
            jsonArray.put(arrayList.get(i));
        }
        if(!arrayList.isEmpty()) {
            editor.putString(key, jsonArray.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    // 근무 형태를 불러옴
    public static ArrayList<String> getWorkFormPref(Context context, String key) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sharedPrefs.getString(key, null);
        ArrayList<String> arrayList = new ArrayList<String>();

        if(json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for(int i=0;i<jsonArray.length(); i++) {
                    String workForm = jsonArray.optString(i);
                    arrayList.add(workForm);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}
