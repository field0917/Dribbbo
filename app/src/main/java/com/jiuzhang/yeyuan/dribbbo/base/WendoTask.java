package com.jiuzhang.yeyuan.dribbbo.base;

import android.os.AsyncTask;

import java.io.IOException;

public abstract class WendoTask<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {

    private Exception e;

    public abstract Result doOnNewThread (Params... params) throws Exception;

    public abstract void onSuccess (Result result);

    public abstract void onFailed (Exception e);

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
