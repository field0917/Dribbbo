package com.jiuzhang.yeyuan.dribbbo.dribbble;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Dribbble {

    public static final int COUNT_PER_PAGE = 10;
    private static final String KEY_PER_PAGE = "per_page";

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER = "user";
    private static final String SP_AUTH = "sp_auth";

    private static final String API_URL = "https://api.unsplash.com/";
    private static final String USER_END_POINT = API_URL + "me";
    private static final String SHOTS_END_POINT = API_URL + "photos";

    public static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};
    private static final TypeToken<List<Shot>> SHOT_LIST_TYPE = new TypeToken<List<Shot>>(){};


    private static String accessToken;
    private static User user;
    private static OkHttpClient client = new OkHttpClient();

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
//        storeAccessToken(context, null);
//        storeUser(context, null);

        accessToken = null;
        user = null;

        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().remove(KEY_ACCESS_TOKEN).apply();
        sp.edit().remove(KEY_USER).apply();
    }

    private static void storeAccessToken (Context context, String token) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    private static void storeUser (Context context, User user) {
        ModelUtils.save(context, KEY_USER, user);
    }

    private static User getUser () throws IOException {
        return parseResponse(makeGetRequest(USER_END_POINT), USER_TYPE);
    }

    private static <T> T parseResponse (Response response, TypeToken<T> typeToken) throws IOException {
        String responseString = response.body().string();
        return ModelUtils.toObject(responseString, typeToken);
    }

    private static Request.Builder authRequestBuilder (String url) {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url);
    }

    private static Response makeGetRequest (String url) throws IOException {
        Request request = authRequestBuilder(url).build();
        return getResponseFromRequest(request);
    }

    private static Response getResponseFromRequest (Request request) throws IOException {
        return client.newCall(request).execute();
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

    public static List<Shot> getShots(int page) throws IOException {
        String url = SHOTS_END_POINT + "?page=" + page;
        Response response = makeGetRequest(url);

        List<Shot> result = parseResponse(response, SHOT_LIST_TYPE);
        return result;
    }

}
