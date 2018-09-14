package com.jiuzhang.yeyuan.dribbbo.shot_detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.squareup.picasso.Picasso;

public class ShotDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SHOT_IMAGE = 0;
    private static final int VIEW_TYPE_SHOT_ACTION = 1;
    private static final int VIEW_TYPE_SHOT_INFO = 2;

    private Shot shot;

    public ShotDetailAdapter (Shot shot) {
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
        Context context = holder.itemView.getContext();
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                ShotImageViewHolder shotImageViewHolder = (ShotImageViewHolder) holder;
//                Picasso.with(context)
//                        .load(shot.coverImageURL)
//                        .into(shotImageViewHolder.imageView);

                Glide.with(context)
                        .load(shot.getImageUrl())
                        .placeholder(R.drawable.shot_image_placeholder)
                        .error(R.drawable.error_image_not_found)
                        .thumbnail(0.1f)
                        .into(shotImageViewHolder.imageView);
                break;
            case VIEW_TYPE_SHOT_ACTION:
                ShotActionViewHolder shotActionViewHolder = (ShotActionViewHolder) holder;
                shotActionViewHolder.viewCount.setText(String.valueOf(shot.views));
                shotActionViewHolder.likeCount.setText(String.valueOf(shot.likes));
                shotActionViewHolder.saveCount.setText(String.valueOf(shot.downloads));
                break;
            case VIEW_TYPE_SHOT_INFO:
                ShotInfoViewHolder shotInfoViewHolder = (ShotInfoViewHolder) holder;

                Glide.with(context)
                        .load(shot.user.getProfileImageURL())
                        .placeholder(R.drawable.user_picture_placeholder)
                        .thumbnail(0.1f)
                        .into(shotInfoViewHolder.shotAuthorImage);
                shotInfoViewHolder.shotAuthor.setText(shot.user.name);
                //shotInfoViewHolder.shotTitle.setText(shot.title);
                //shotInfoViewHolder.shotInfo.setText(shot.description);
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
