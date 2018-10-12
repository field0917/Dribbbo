package com.jiuzhang.yeyuan.dribbbo.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;

public abstract class CustomTarget<Bitmap> implements Target<Bitmap> {

    private Shot shot;
    private View view;

    public CustomTarget (Shot shot, View view) {
        this.shot = shot;
        this.view = view;
    }

    @Override
    public void onLoadStarted(@Nullable Drawable placeholder) {
        Snackbar.make(view, "Loading...", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        Snackbar.make(view, "Download Failed!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void getSize(@NonNull SizeReadyCallback cb) {
        cb.onSizeReady(shot.width, shot.height);
    }

    @Override
    public abstract void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition);

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {

    }

    @Override
    public void removeCallback(@NonNull SizeReadyCallback cb) {

    }

    @Override
    public void setRequest(@Nullable Request request) {

    }

    @Nullable
    @Override
    public Request getRequest() {
        return null;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }
}
