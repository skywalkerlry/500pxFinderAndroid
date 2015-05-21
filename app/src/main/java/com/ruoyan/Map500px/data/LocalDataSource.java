package com.ruoyan.map500px.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ruoyan.map500px.bean.PhotoInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ruoyan on 3/5/15.
 */
public class LocalDataSource {
    private static final String PREF_FAVORITE = "favorites";
    private static final String PREF_SETTINGS = "settings";
    private static final String PREF_SETTINGS_ORDER = "settings_order";
    private SharedPreferences favorPrefs, settingsPrefs, settingsOrderPrefs;

    public LocalDataSource(Context context) {
        favorPrefs = context.getSharedPreferences(PREF_FAVORITE, Context.MODE_PRIVATE);
        settingsPrefs = context.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE);
        settingsOrderPrefs = context.getSharedPreferences(PREF_SETTINGS_ORDER,
                Context.MODE_PRIVATE);
    }

    public List<PhotoInfo> findAllFavorites() {
        List<PhotoInfo> favorList = new ArrayList<>();
        Gson gson = new Gson();
        Map<String,?> photoInfoMap = favorPrefs.getAll();
        for (String key:photoInfoMap.keySet()) {
            String photoJson = (String) photoInfoMap.get(key);
            PhotoInfo photoInfo = gson.fromJson(photoJson, PhotoInfo.class);
            favorList.add(photoInfo);
        }
        return favorList;
    }

    public PhotoInfo getPhoto(String photoId) {
        PhotoInfo photoInfo = null;
        String photoJson = favorPrefs.getString(photoId,"");
        if (!photoJson.equals("")) {
            Gson gson = new Gson();
            photoInfo = gson.fromJson(photoJson, PhotoInfo.class);
        }
        return photoInfo;
    }

    public void updateFavorites(PhotoInfo photoInfo) {
        SharedPreferences.Editor editor = favorPrefs.edit();
        Gson gson = new Gson();
        String photoJson = gson.toJson(photoInfo);
        editor.putString(photoInfo.getId(),photoJson);
        editor.commit();
    }

    public void removePhoto(String photoId) {
        SharedPreferences.Editor editor = favorPrefs.edit();
        if (favorPrefs.contains(photoId)) {
            editor.remove(photoId);
            editor.commit();
        }
    }

    public boolean hasPhoto(String photoId) {
        return favorPrefs.contains(photoId);
    }

    public void updateSettings(String type, String setting) {
        SharedPreferences.Editor editor = settingsPrefs.edit();
        editor.putString(type,setting);
        editor.commit();
    }

    public String getSettings(String type) {
        return settingsPrefs.getString(type,"");
    }

    public boolean hasSettings(String setting) {
        return settingsPrefs.contains(setting);
    }

    public void updateSettingsOrder(String type, int order) {
        SharedPreferences.Editor editor = settingsOrderPrefs.edit();
        editor.putInt(type,order);
        editor.commit();
    }

    public int getSettingsOrder(String type) {
        return settingsOrderPrefs.getInt(type, -1);
    }
}
