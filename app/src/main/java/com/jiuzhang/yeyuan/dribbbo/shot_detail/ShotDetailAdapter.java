package com.jiuzhang.yeyuan.dribbbo.shot_detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.EditBucketActivity;
import com.jiuzhang.yeyuan.dribbbo.dribbble.Dribbble;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.security.AccessController.getContext;

public class ShotDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SHOT_IMAGE = 0;
    private static final int VIEW_TYPE_SHOT_ACTION = 1;
    private static final int VIEW_TYPE_SHOT_INFO = 2;

    private Shot shot;
    private ShotDetailFragment shotDetailFragment;

    public ShotDetailAdapter (ShotDetailFragment shotDetailFragment,
                              Shot shot) {
        this.shotDetailFragment = shotDetailFragment;
        this.shot = shot;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shot_detail_image, parent, false);
                return new ShotImageViewHolder(view);

            case VIEW_TYPE_SHOT_ACTION:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shot_detail_action, parent, false);
                return new ShotActionViewHolder(view);

            case VIEW_TYPE_SHOT_INFO:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shot_detail_info, parent, false);
                return new ShotInfoViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        final Context context = holder.itemView.getContext();
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                ShotImageViewHolder shotImageViewHolder = (ShotImageViewHolder) holder;

                Glide.with(context)
                        .load(shot.getImageUrl())
                        .apply(new RequestOptions().placeholder(R.drawable.shot_image_placeholder)
                                .error(R.drawable.error_image_not_found))
                        .thumbnail(0.01f)
                        .into(shotImageViewHolder.imageView);
                break;
            case VIEW_TYPE_SHOT_ACTION:
                ShotActionViewHolder shotActionViewHolder = (ShotActionViewHolder) holder;
                shotActionViewHolder.viewCount.setText(String.valueOf(shot.views));
                shotActionViewHolder.likeCount.setText(String.valueOf(shot.likes));

                shotActionViewHolder.likeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotDetailFragment.like(shot.liked_by_user);
                    }
                });

                shotActionViewHolder.collect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotDetailFragment.bucket(context);
                    }
                });

                shotActionViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotDetailFragment.share(context);
                    }
                });

                // Set the bucket icon according to isBucketed
                Drawable bucketImg = shot.isBucketed() ? context.getResources()
                                                         .getDrawable(R.drawable.ic_inbox_red_200_18dp)
                                                       : context.getResources()
                                                         .getDrawable(R.drawable.ic_inbox_black_18dp);
                shotActionViewHolder.collect
                        .setCompoundDrawablesWithIntrinsicBounds(null, bucketImg, null, null);

                // Set the like icon according to liked_by_user
                Drawable likeImg = shot.liked_by_user ? context.getResources()
                                                        .getDrawable(R.drawable.ic_favorite_red_200_18dp)
                                                      : context.getResources()
                                                        .getDrawable(R.drawable.ic_favorite_border_black_18dp);
                shotActionViewHolder.likeIcon.setImageDrawable(likeImg);

                break;
            case VIEW_TYPE_SHOT_INFO:
                ShotInfoViewHolder shotInfoViewHolder = (ShotInfoViewHolder) holder;

                Glide.with(context)
                        .load(shot.user.getProfileImageURL())
                        .apply(new RequestOptions().placeholder(R.drawable.user_picture_placeholder))
                        .apply(RequestOptions.circleCropTransform())
                        .thumbnail(0.01f)
                        .into(shotInfoViewHolder.shotAuthorImage);
                shotInfoViewHolder.shotAuthor.setText(shot.user.name);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        if (position == 0) {
            return VIEW_TYPE_SHOT_INFO;
        } else if (position == 1) {
            return VIEW_TYPE_SHOT_IMAGE;
        } else if (position == 2){
            return VIEW_TYPE_SHOT_ACTION;
        } else {
            return -1;
        }
    }
}
