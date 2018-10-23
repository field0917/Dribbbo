package com.jiuzhang.yeyuan.dribbbo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.jiuzhang.yeyuan.dribbbo.AuthActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Auth {

    public static final int REQ_CODE = 100;

    public static final String KEY_URL = "auth URL";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_CLIENT_SECRET = "client_secret";
    private static final String KEY_REDIRECT_URL = "redirect_uri";
    private static final String KEY_RESPONSE_TYPE = "response_type";
    private static final String KEY_SCOPE = "scope";
    private static final String KEY_CODE = "code";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_GRANT_TYPE = "grant_type";

    private static final String URI_AUTHORIZE = "https://unsplash.com/oauth/authorize";
    private static final String URI_TOKEN = "https://unsplash.com/oauth/token";

    private static final String CLIENT_ID = "8a0a0cae5afbfbc0d419cfb47884ed096fe10267225fdb3a4488f97428b89f08";
    private static final String CLIENT_SECRET = "dc93c10796feb82ef1d5f78feed092755b40f501383f4f0463896670d06d94ec";
    private static final String SCOPE = "public+read_user+write_user+read_photos+write_photos+write_followers+read_collections+write_collections+write_likes";

    public static final String REDIRECT_URL = "http://www.google.com";
    private static final String RESPONSE_TYPE = "code";
    private static final String GRANT_TYPE = "authorization_code";

//https://unsplash.com/oauth/authorize?client_id=8a0a0cae5afbfbc0d419cfb47884ed096fe10267225fdb3a4488f97428b89f08&redirect_uri=http://www.google.com&response_type=code&scope=public+read_user+write_user+read_photos+write_photos+write_followers+read_collections+write_collections+write_likes
//https://unsplash.com/oauth/token?client_id=8a0a0cae5afbfbc0d419cfb47884ed096fe10267225fdb3a4488f97428b89f08&client_secret=dc93c10796feb82ef1d5f78feed092755b40f501383f4f0463896670d06d94ec&redirect_uri=http://www.google.com&code=2c631a813e70e37fb8eaa5a3bf2d5e1eaee56ccef997371f200dc9aec0ce7165&grant_type=authorization_code
//    {
//        "access_token": "72ca996f78090ed8dacd935b67dcc4aab9648ffd2614e96699ed6e442da4b420",
//            "token_type": "bearer",
//            "refresh_token": "aedce8ef7dc45c2ab4a13964eb54d931391cfc826330ce6ab436a0fc26b70708",
//            "scope": "public read_user write_user read_photos write_photos write_followers read_collections write_collections write_likes",
//            "created_at": 1540330645
//    }
    public static void openAuthActivity(Activity activity) {
        Intent intent = new Intent(activity, AuthActivity.class);
        String url = getAuthorizeURL();
        intent.putExtra(KEY_URL, url);
        activity.startActivityForResult(intent, REQ_CODE);
    }

    private static String getAuthorizeURL () {
        String url = Uri.parse(URI_AUTHORIZE)
                .buildUpon()
                .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                .build()
                .toString();

        url += "&" + KEY_REDIRECT_URL + "=" + REDIRECT_URL;
        url += "&" + KEY_RESPONSE_TYPE + "=" + RESPONSE_TYPE;
        url += "&" + KEY_SCOPE + "=" + SCOPE;

        return url;
    }

    // use code to send a post request to get the token, code in body
    public static String getAccessToken(String code) {
        OkHttpClient client = new OkHttpClient();

        RequestBody postBody = new FormBody.Builder()
                .add(KEY_CLIENT_ID, CLIENT_ID)
                .add(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .add(KEY_REDIRECT_URL, REDIRECT_URL)
                .add(KEY_CODE, code)
                .add(KEY_GRANT_TYPE, GRANT_TYPE)
                .build();

        Request request = new Request.Builder()
                .url(URI_TOKEN)
                .post(postBody)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String responseString = response.body().string();

            JSONObject jsonObject = new JSONObject(responseString);
            return jsonObject.getString(KEY_ACCESS_TOKEN);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
