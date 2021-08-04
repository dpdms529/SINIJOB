package org.techtown.hanieum;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

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
    // for test
    public static final String CAREER_JOB_CODE = "careerJobCode";
    public static final String CAREER_PERIOD = "careerPeriod";
    public static final String CERTIFICATE_CODE = "certificateCode";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    SharedPreference(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    // for test
    public void setStringArrayPref(ArrayList<String> arrayList, String key) {
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(key, json);
        editor.commit();
    }

    // for test
    public ArrayList<String> getStringArrayPref(String key) {
        Gson gson = new Gson();
        String json = preferences.getString(key, null);
        if(json == null){
            setStringArrayPref(new ArrayList<String>(),key);
            json = preferences.getString(key,null);
        }
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    // chipList를 저장
    public void setArrayPref(ArrayList<ChipList> chipList, String key) {
        Gson gson = new Gson();
        String json = gson.toJson(chipList);
        editor.putString(key, json);
        editor.commit();
    }

    // chipList를 불러옴
    public ArrayList<ChipList> getArrayPref(String key) {
        Gson gson = new Gson();
        String json = preferences.getString(key, null);
        if(json == null){
            setArrayPref(new ArrayList<ChipList>(),key);
            json = preferences.getString(key,null);
        }
        Type type = new TypeToken<ArrayList<ChipList>>() {
        }.getType();
        ArrayList<ChipList> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

}
