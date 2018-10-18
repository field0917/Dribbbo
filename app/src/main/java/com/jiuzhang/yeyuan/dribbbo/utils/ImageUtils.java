package com.jiuzhang.yeyuan.dribbbo.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jiuzhang.yeyuan.dribbbo.R;

public class ImageUtils {

    public static void loadShotImage (@NonNull Context context,
                                      @NonNull String url,
                                      @NonNull ImageView imageView) {

        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.shot_image_placeholder)
                        .error(R.drawable.error_image_not_found))
                .thumbnail(0.1f)
                .into(imageView);
    }

    public static void loadCircleUserImage (@NonNull Context context,
                                            @NonNull String url,
                                            @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().placeholder(R.drawable.user_picture_placeholder))
                .apply(RequestOptions.circleCropTransform()) // make the image circle
                .thumbnail(0.1f)
                .into(imageView);
    }
}
