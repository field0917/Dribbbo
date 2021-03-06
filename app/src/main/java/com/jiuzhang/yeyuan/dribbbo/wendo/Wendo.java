package com.jiuzhang.yeyuan.dribbbo.wendo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;
import com.jiuzhang.yeyuan.dribbbo.model.SearchBucketResult;
import com.jiuzhang.yeyuan.dribbbo.model.SearchPhotoResult;
import com.jiuzhang.yeyuan.dribbbo.model.SearchUserResult;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Wendo {

    public static final int BUCKET_COUNT_PER_PAGE = 3;
    public static final int SHOT_COUNT_PER_PAGE = 10;
    private static final String KEY_PER_PAGE = "per_page";

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER = "user";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String SP_AUTH = "sp_auth";
    public static final String KEY_SHOT_ID = "photo_id";
    public static final String KEY_BUCKET_ID = "collection_id";
    public static final String KEY_ID = "id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PORTFOLIO = "url";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_BIO = "bio";
    private static final String KEY_INSTAGRAM = "instagram_username";


    private static final String API_URL = "https://api.unsplash.com/";
    private static final String CURRENT_USER_END_POINT = API_URL + "me";
    private static final String USER_END_POINT = API_URL + "users/";
    private static final String SHOTS_END_POINT = API_URL + "photos";
    private static final String BUCKET_CREATE_END_POINT = API_URL + "collections";
    private static final String BUCKET_END_POINT = API_URL + "collections/";
    private static final String SEARCH_SHOT_END_POINT = API_URL + "search/photos?query=";
    private static final String SEARCH_USER_END_POINT = API_URL + "search/users?query=";
    private static final String SEARCH_BUCKET_END_POINT = API_URL + "search/collections?query=";


    public static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};
    public static final TypeToken<List<Shot>> SHOT_LIST_TYPE = new TypeToken<List<Shot>>(){};
    public static final TypeToken<List<Bucket>> BUCKET_LIST_TYPE = new TypeToken<List<Bucket>>(){};
    public static final TypeToken<Bucket> BUCKET_TYPE = new TypeToken<Bucket>(){};
    public static final TypeToken<Shot> SHOT_TYPE = new TypeToken<Shot>(){};
    private static final TypeToken<SearchPhotoResult> SEARCH_SHOT_LIST_TYPE = new TypeToken<SearchPhotoResult>(){};
    private static final TypeToken<SearchUserResult> SEARCH_USER_LIST_TYPE = new TypeToken<SearchUserResult>(){};
    private static final TypeToken<SearchBucketResult> SEARCH_BUCKET_LIST_TYPE = new TypeToken<SearchBucketResult>(){};

    public static final String DOWNLOAD_PHOTO_FORMAT = ".jpg";
    public static final String DOWNLOAD_PATH = "/Pictures/Wendo/";

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

    public static void storeUser (Context context, User user) {
        ModelUtils.save(context, KEY_USER, user);
    }

    public static User getUser () throws IOException {
        return parseResponse(makeGetRequest(CURRENT_USER_END_POINT), USER_TYPE);
    }

    private static String loadAccessToken (Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    private static User loadUser (Context context) {
        return ModelUtils.read(context, KEY_USER, USER_TYPE);
    }

    public static User getCurrentUser () {
        return user;
    }

//    private static boolean isJasonObject (String s) {
//
//    }

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

    private static Response makePutRequest (String url, RequestBody requestBody) throws IOException {
        Request request = authRequestBuilder(url)
                .put(requestBody)
                .build();
        return getResponseFromRequest(request);
    }

    private static Response getResponseFromRequest (Request request) throws IOException {
        Response response = client.newCall(request).execute();
        return response;
    }

    public static List<Shot> getShots (int page) throws IOException {
        String url = SHOTS_END_POINT + "?page=" + page + "&" + KEY_PER_PAGE + "=" + SHOT_COUNT_PER_PAGE;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getCuratedShots (int page) throws IOException {
        String url = SHOTS_END_POINT + "/curated" + "?page=" + page + "&" + KEY_PER_PAGE + "=" + SHOT_COUNT_PER_PAGE;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static Shot getShot (String shotId) throws IOException {
        String url = SHOTS_END_POINT + "/" + shotId;
        return parseResponse(makeGetRequest(url), SHOT_TYPE);
    }

    public static List<Bucket> getBuckets (int page) throws IOException {
        String url = BUCKET_CREATE_END_POINT + "/?page=" + page + "&" + KEY_PER_PAGE + "=" + BUCKET_COUNT_PER_PAGE;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static Bucket getBucket (int bucketId) throws IOException {
        String url = BUCKET_CREATE_END_POINT + "/" + bucketId;
        return parseResponse(makeGetRequest(url), BUCKET_TYPE);
    }

    public static Bucket createNewBucket (String title, String description) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add(KEY_TITLE, title)
                .add(KEY_DESCRIPTION, description)
                .build();
        Response response = makePostRequest(BUCKET_CREATE_END_POINT, requestBody);
        return parseResponse(response, BUCKET_TYPE);
    }

    public static Bucket removeABucket (int bucketId) throws IOException {
        String url = BUCKET_END_POINT + bucketId;
        RequestBody requestBody = new FormBody.Builder()
                .add(KEY_ID, String.valueOf(bucketId))
                .build();
        Response response = makeDeleteRequest(url, requestBody);
        return parseResponse(response, BUCKET_TYPE);
    }

    public static Shot addShotToBucket (String shotID, int bucketID) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotID)
                .add(KEY_BUCKET_ID, String.valueOf(bucketID))
                .build();
        Response response = makePostRequest(BUCKET_END_POINT + bucketID + "/add", requestBody);
        return parseResponse(response, SHOT_TYPE);
    }

    public static Shot removeShotFromBucket (String shotID, int bucketID) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotID)
                .add(KEY_BUCKET_ID, String.valueOf(bucketID))
                .build();
        Response response = makeDeleteRequest(BUCKET_END_POINT + bucketID + "/remove", requestBody);
        return parseResponse(response, SHOT_TYPE);
    }

    public static List<Shot> getBucketShots (int page, int bucketID) throws IOException {
        String url = BUCKET_END_POINT + bucketID + "/photos?page=" + page +"&" + KEY_PER_PAGE + "=" + SHOT_COUNT_PER_PAGE;
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

//    public static List<Shot> getLikedShots (int page) throws IOException {
//        String url = API_URL + "users/" + user.username + "/likes?page=" + page;
//        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
//    }

    public static List<Shot> getUserShots (String username, int page) throws IOException {
        String url = USER_END_POINT + username + "/photos?page=" + page + "&" + KEY_PER_PAGE + "=" + SHOT_COUNT_PER_PAGE;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getUserLikes (String username, int page) throws IOException {
        String url = USER_END_POINT + username + "/likes?page=" + page + "&" + KEY_PER_PAGE + "=" + SHOT_COUNT_PER_PAGE;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Bucket> getUserBuckets (String username, int page) throws IOException {
        String url = USER_END_POINT + username + "/collections?page=" + page + "&" + KEY_PER_PAGE + "=" + BUCKET_COUNT_PER_PAGE;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static List<Shot> getSearchedShots (String query, int page) throws IOException {
        String url = SEARCH_SHOT_END_POINT + query + "&page=" + page;
        SearchPhotoResult result = parseResponse(makeGetRequest(url), SEARCH_SHOT_LIST_TYPE);
        return result.results;
    }

    public static List<User> getSearchedUsers (String query, int page) throws IOException {
        String url = SEARCH_USER_END_POINT + query + "&page=" + page;
        SearchUserResult result = parseResponse(makeGetRequest(url), SEARCH_USER_LIST_TYPE);
        return result.results;
    }

    public static List<Bucket> getSearchedBuckets (String query, int page) throws IOException {
        String url = SEARCH_BUCKET_END_POINT + query + "&page=" + page;
        SearchBucketResult result = parseResponse(makeGetRequest(url), SEARCH_BUCKET_LIST_TYPE);
        return result.results;
    }

    public static User updateCurrentUserProfile (Context context, String[] info) throws IOException {
        String[] keys = {KEY_FIRST_NAME, KEY_LAST_NAME, KEY_USERNAME, KEY_EMAIL, KEY_PORTFOLIO, KEY_LOCATION, KEY_BIO, KEY_INSTAGRAM};
        FormBody.Builder builder = new FormBody.Builder();
        for (int i = 0; i < info.length; i++) {
          if (info[i] != null && !info[i].equals("")) {
              builder.add(keys[i], info[i]);
          }
        }
        RequestBody requestBody = builder.build();
        user = parseResponse(makePutRequest(CURRENT_USER_END_POINT, requestBody), USER_TYPE);
        storeUser(context, user);
        return user;
    }
}
