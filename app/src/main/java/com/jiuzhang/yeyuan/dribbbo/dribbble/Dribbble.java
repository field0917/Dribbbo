package com.jiuzhang.yeyuan.dribbbo.dribbble;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Dribbble {

    public static final int COUNT_PER_PAGE = 10;
    private static final String KEY_PER_PAGE = "per_page";

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER = "user";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String SP_AUTH = "sp_auth";
    public static final String KEY_SHOT_ID = "photo_id";
    public static final String KEY_BUCKET_ID = "collection_id";
    public static final String KEY_ID = "id";

    private static final String API_URL = "https://api.unsplash.com/";
    private static final String USER_END_POINT = API_URL + "me";
    private static final String SHOTS_END_POINT = API_URL + "photos";
    private static final String BUCKET_CREATE_END_POINT = API_URL + "collections";
    private static final String BUCKET_SHOT_END_POINT = API_URL + "collections/";


    public static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};
    private static final TypeToken<List<Shot>> SHOT_LIST_TYPE = new TypeToken<List<Shot>>(){};
    private static final TypeToken<List<Bucket>> BUCKET_LIST_TYPE = new TypeToken<List<Bucket>>(){};
    private static final TypeToken<Bucket> BUCKET_TYPE = new TypeToken<Bucket>(){};
    private static final TypeToken<Shot> SHOT_TYPE = new TypeToken<Shot>(){};



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

    private static Response makePostRequest (String url, RequestBody requestBody) throws IOException {

        Request request = authRequestBuilder(url)
                .post(requestBody)
                .build();

        return getResponseFromRequest(request);
    }

    private static Response makeDeleteRequest (String url, RequestBody requestBody) throws IOException {
        Request request = authRequestBuilder(url)
                .delete(requestBody)
                .build();
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

    public static List<Shot> getShots (int page) throws IOException {
        String url = SHOTS_END_POINT + "?page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static Shot getShot (String shotId) throws IOException {
        String url = SHOTS_END_POINT + "/" + shotId;
        return parseResponse(makeGetRequest(url), SHOT_TYPE);
    }

    public static List<Bucket> getBuckets () throws IOException {
        String url = getCollectionsEndPoint ();
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static Bucket getBucket (int bucketId) throws IOException {
        String url = BUCKET_CREATE_END_POINT + "/" + bucketId;
        return parseResponse(makeGetRequest(url), BUCKET_TYPE);
    }

    private static String getCollectionsEndPoint () {
        String url = API_URL + "users/";
        url += getCurrentUser().username;
        url += "/collections";
        return url;
    }

    public static Bucket createNewBucket (String title, String description) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add(KEY_TITLE, title)
                .add(KEY_DESCRIPTION, description)
                .build();
        Response response = makePostRequest(BUCKET_CREATE_END_POINT, requestBody);
        return parseResponse(response, BUCKET_TYPE);
    }

    public static Shot addShotToBucket (String shotID, int bucketID) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotID)
                .add(KEY_BUCKET_ID, String.valueOf(bucketID))
                .build();
        Response response = makePostRequest(BUCKET_SHOT_END_POINT + bucketID + "/add", requestBody);
        return parseResponse(response, SHOT_TYPE);
    }

    public static Shot removeShotFromBucket (String shotID, int bucketID) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotID)
                .add(KEY_BUCKET_ID, String.valueOf(bucketID))
                .build();
        Response response = makeDeleteRequest(BUCKET_SHOT_END_POINT + bucketID + "/remove", requestBody);
        return parseResponse(response, SHOT_TYPE);
    }

    public static List<Shot> getBucketShots (int page, int bucketID) throws IOException {
        String url = BUCKET_SHOT_END_POINT + bucketID + "/photos?page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static void likeTheShot (String shotID) throws IOException {
        String url = SHOTS_END_POINT + "/" + shotID + "/like";
        RequestBody requestBody = new FormBody.Builder()
                .add(KEY_ID, shotID)
                .build();
        Response response = makePostRequest(url, requestBody);
    }

    public static void unlikeTheShot (String shotID) throws IOException {
        String url = SHOTS_END_POINT + "/" + shotID + "/like";
        RequestBody requestBody = new FormBody.Builder()
                .add(KEY_ID, shotID)
                .build();
        Response response = makeDeleteRequest(url, requestBody);
    }

    public static List<Shot> getLikedShots (int page) throws IOException {
        String url = API_URL + "users/" + user.username + "/likes?page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

}
