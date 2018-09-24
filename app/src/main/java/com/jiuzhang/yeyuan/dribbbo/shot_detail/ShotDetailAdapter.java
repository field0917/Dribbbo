package com.jiuzhang.yeyuan.dribbbo.shot_detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.EditBucketActivity;
import com.jiuzhang.yeyuan.dribbbo.dribbble.Dribbble;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShotDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SHOT_IMAGE = 0;
    private static final int VIEW_TYPE_SHOT_ACTION = 1;
    private static final int VIEW_TYPE_SHOT_INFO = 2;

    private Shot shot;
    private ShotDetailFragment shotDetailFragment;
    private ArrayList<Integer> collectedBucketIds;


    public ShotDetailAdapter (ShotDetailFragment shotDetailFragment, Shot shot) {
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

        collectedBucketIds = getCollectedBucketIDs();

        int viewType = getItemViewType(position);
        final Context context = holder.itemView.getContext();
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                ShotImageViewHolder shotImageViewHolder = (ShotImageViewHolder) holder;

                Glide.with(context)
                        .load(shot.getImageUrl())
                        .placeholder(R.drawable.shot_image_placeholder)
                        .error(R.drawable.error_image_not_found)
                        .thumbnail(0.01f)
                        .into(shotImageViewHolder.imageView);
                break;
            case VIEW_TYPE_SHOT_ACTION:
                ShotActionViewHolder shotActionViewHolder = (ShotActionViewHolder) holder;
                shotActionViewHolder.viewCount.setText(String.valueOf(shot.views));
                shotActionViewHolder.likeCount.setText(String.valueOf(shot.likes));
                shotActionViewHolder.saveCount.setText(String.valueOf(shot.downloads));

                shotActionViewHolder.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bucket(context);
                    }
                });

                break;
            case VIEW_TYPE_SHOT_INFO:
                ShotInfoViewHolder shotInfoViewHolder = (ShotInfoViewHolder) holder;

                Glide.with(context)
                        .load(shot.user.getProfileImageURL())
                        .placeholder(R.drawable.user_picture_placeholder)
                        .thumbnail(0.01f)
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

    private ArrayList<Integer> getCollectedBucketIDs () { // The *current user's* collections that this photo belongs to.
        List<Bucket> collectedBucket = shot.current_user_collections;
        ArrayList<Integer> bucketIds = new ArrayList<>();
        for (Bucket bucket : collectedBucket) {
            bucketIds.add(bucket.id);
        }
        return bucketIds;
    }

    public List<Integer> getReadOnlyCollectedBucketIds () {
        return Collections.unmodifiableList(collectedBucketIds);
    }

    public List<Integer> updateCollectedBucketIDs (List<Integer> added, List<Integer> removed) {
        if (collectedBucketIds == null) {
            collectedBucketIds = new ArrayList<>();
        }

        collectedBucketIds.addAll(added);
        collectedBucketIds.removeAll(removed);

        return collectedBucketIds;
    }

    public List<Integer> updateCollectedBucketIDs (List<Integer> bucketIds) {
        if (collectedBucketIds == null) {
            collectedBucketIds = new ArrayList<>();
        }

        collectedBucketIds.clear();
        collectedBucketIds.addAll(bucketIds);

        return collectedBucketIds;
    }

    private void bucket (Context context) {
        if (collectedBucketIds != null) {
            Intent intent = new Intent(context, EditBucketActivity.class);
            intent.putExtra(BucketListFragment.KEY_CHOSEN_BUCKET_ID_LIST, collectedBucketIds);//give the selectedBuckets to EditBucketActivity
            shotDetailFragment.startActivityForResult(intent, ShotDetailFragment.REQ_CHOSEN_BUCKET);
        }

    }
}
