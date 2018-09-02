package com.jiuzhang.yeyuan.dribbbo.dribbble;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Dribbble {

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER = "user";
    private static final String SP_AUTH = "sp_auth";

    private static final String API_URL = "https://api.dribbble.com/v2/";
    private static final String USER_END_POINT = API_URL + "user";
    private static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};


    private static String accessToken;
    private static User user;

    public static void init (Context context) {
        accessToken = loadAccessToken(context);
        if (accessToken != null) {
            user = loadUser(context);
        }
    }

    public static boolean isLoggedIn () {
        return accessToken != null;
    }

    // save token to local memory also sharedPreferences, get user and save in sharedPreferences
    public static void login (Context context, String token) throws IOException {
        accessToken = token;
        storeAccessToken(context, accessToken);
        user = getUser();
        storeUser(context, user);
    }

    public static void logout (Context context) {
        storeAccessToken(context, null);
        storeUser(context, null);

        accessToken = null;
        user = null;
    }

    private static void storeAccessToken (Context context, String token) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    private static void storeUser (Context context, User user) {
        ModelUtils.save(context, KEY_USER, user);
    }

    private static User getUser () throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(USER_END_POINT)
                .build();
        Response response = client.newCall(request).execute();

        // Parse response
        String responseString = response.body().string();
        return ModelUtils.toObject(responseString, USER_TYPE);
    }

    public static User getCurrentUser () {
        return user;
    }

    private static String loadAccessToken (Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    private static User loadUser (Context context) {
        return ModelUtils.read(context, KEY_USER, USER_TYPE);
    }

}
