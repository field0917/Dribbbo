package com.jiuzhang.yeyuan.dribbbo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;

public class ModelUtils {

    private static final String PREF_NAME = "com.jiuzhang.yeyuan.dribbbo.utils";

    private static Gson gson = new Gson();

    public static void save (Context context, String key, Object object) {
        SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String jsonString = gson.toJson(object);
        sharedPreferences.edit().putString(key, jsonString).apply();
    }

    public static <T> T read (Context context, String key, TypeToken<T> typeToken) {
        SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return gson.fromJson(sharedPreferences.getString(key, ""), typeToken.getType());
    }

    public static <T> T toObject (String jsonString, TypeToken<T> typeToken) {
        return gson.fromJson(jsonString, typeToken.getType());
    }

    public static <T> String toString (T object, TypeToken<T> typeToken) {
        return gson.toJson(object, typeToken.getType());
    }
}
