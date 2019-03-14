package com.jiuzhang.yeyuan.dribbbo.base;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.jiuzhang.yeyuan.dribbbo.utils.Utils;

import java.io.IOException;

public abstract class WendoTask<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {

    private Exception e;

    public abstract void checkInternetConnection ();

    public abstract Result doOnNewThread (Params... params) throws Exception;

    public abstract void onSuccess (Result result);

    public abstract void onFailed (Exception e);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        checkInternetConnection();
    }

    @Override
    protected Result doInBackground(Params... params) {
        try {
            return doOnNewThread(params);

        } catch (Exception e) {
            e.printStackTrace();
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (e == null) {
            onSuccess(result);
        } else {
            onFailed(e);
        }
    }
}
