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

    private static final String CLIENT_ID = "3557d42a211f301f0748ce528a3e44c3c6146d8fe460404fbdc9eee5519eff2d";
    private static final String CLIENT_SECRET = "812c6de60c3f06a0b2a881f543220763d070a5c40b8b086c25f69ad4b785bac5";
    private static final String SCOPE = "public+read_user+write_user+read_photos+write_photos+write_followers+read_collections+write_collections+write_likes";

    public static final String REDIRECT_URL = "http://www.google.com";
    private static final String RESPONSE_TYPE = "code";
    private static final String GRANT_TYPE = "authorization_code";

//https://unsplash.com/oauth/authorize?client_id=3557d42a211f301f0748ce528a3e44c3c6146d8fe460404fbdc9eee5519eff2d&redirect_uri=http://www.google.com&response_type=code&scope=public+read_user+write_user+read_photos+write_photos+write_followers+read_collections+write_collections+write_likes
//https://unsplash.com/oauth/token?client_id=3557d42a211f301f0748ce528a3e44c3c6146d8fe460404fbdc9eee5519eff2d&client_secret=812c6de60c3f06a0b2a881f543220763d070a5c40b8b086c25f69ad4b785bac5&redirect_uri=http://www.google.com&code=18bc4c9078e73ba29307c5a69ef998af3a0ffeaa82b1071cbbf622456cfd89d4&grant_type=authorization_code
//{
//    "access_token": "90fbb59b8facf6e8146fb66686c62edb3c8188361b95b6d4d18b1d8ca91c325c",
//    "token_type": "bearer",
//    "refresh_token": "727ead34171c659aec730bc1a59fdb2bfc0f50295ee216d69fe3637c61b3116b",
//    "scope": "public read_user write_user read_photos read_collections",
//    "created_at": 1536697832
//}
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
