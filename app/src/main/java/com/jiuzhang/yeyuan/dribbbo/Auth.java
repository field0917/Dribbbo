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
    private static final String KEY_REDIRECT_URL = "redirect_url";
    private static final String KEY_SCOPE = "scope";
    private static final String KEY_CODE = "code";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static final String URI_AUTHORIZE = "https://dribbble.com/oauth/authorize";
    private static final String URI_TOKEN = "https://dribbble.com/oauth/token";

    private static final String CLIENT_ID = "401963981b138f18f81c37d543f3f9f716d247c6fba156e063e6756dd2d20cbe";
    private static final String CLIENT_SECRET = "20583200f57f721db73aa082fccb2b56befe1162b7e18225727f049f6b228266";
    private static final String SCOPE = "public+write";

    public static final String REDIRECT_URL = "http://www.google.com";

//https://dribbble.com/oauth/authorize?client_id=401963981b138f18f81c37d543f3f9f716d247c6fba156e063e6756dd2d20cbe&scope=public+write
//code=f41b5de1eb828a0cd2b1d288f12a491164ea4db18d9e3bc0235332a2dc74b3e1
    //token=6f2ff496fd3f8231ac8162608e83474c99cadb8c9757b2bb466511118609c860
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

        //url += "&" + KEY_REDIRECT_URL + "=" + REDIRECT_URL;
        url += "&" + KEY_SCOPE + "=" + SCOPE;

        return url;
    }

    // use code to send a post request to get the token, code in body
    public static String getAccessToken(String code) {
        OkHttpClient client = new OkHttpClient();

        RequestBody postBody = new FormBody.Builder()
                .add(KEY_CLIENT_ID, CLIENT_ID)
                .add(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .add(KEY_CODE, code)
                .add(KEY_REDIRECT_URL, REDIRECT_URL)
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
