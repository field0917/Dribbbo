package com.jiuzhang.yeyuan.dribbbo.bucket_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;
import com.jiuzhang.yeyuan.dribbbo.shot_list.ShotListFragment;

import java.util.ArrayList;
import java.util.List;

import static com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment.KEY_BUCKET_ID;
import static com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment.KEY_BUCKET_TITLE;

public class BucketListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_BUCKET_LIST = 0;
    private static final int VIEW_TYPE_LOAD_MORE = 1;

    private List<Bucket> bucketList;
    private LoadMoreListener loadMoreListener;
    private boolean showLoading;
    private boolean isEditMode;
//    private String username;

    public BucketListAdapter (List<Bucket> bucketList,
                              LoadMoreListener loadMoreListener,
                              boolean isEditMode) {
        this.bucketList = bucketList;
        this.loadMoreListener = loadMoreListener;
        this.showLoading = true;
        this.isEditMode = isEditMode;
//        this.username = username;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_BUCKET_LIST:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bucket_item, parent, false);
                return new BucketListViewHolder(view);
            case VIEW_TYPE_LOAD_MORE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.loading_item_activity, parent, false);
                return new RecyclerView.ViewHolder(view) {};

        }
        return null;
    }

    @Override
    public void onBindViewHolder (@NonNull RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        final Context context = holder.itemView.getContext();

        if (viewType == VIEW_TYPE_BUCKET_LIST) {

            final Bucket bucket = bucketList.get(position);
            final BucketListViewHolder bucketListViewHolder = (BucketListViewHolder) holder;

            if (bucket.cover_photo != null) {
                Glide.with(context)
                        .load(bucket.cover_photo.getImageUrl())
                        .apply(new RequestOptions().placeholder(R.drawable.shot_image_placeholder))
                        .into(bucketListViewHolder.bucketCoverImage);
            }

            bucketListViewHolder.bucketTitleTextView.setText(bucket.title);

            if (bucket.total_photos <= 1) {
                bucketListViewHolder.shotNumberTextView.setText(bucket.total_photos + " shot");
            } else {
                bucketListViewHolder.shotNumberTextView.setText(bucket.total_photos + " shots");
            }

            if (isEditMode) {
                bucketListViewHolder.checkBox.setVisibility(View.VISIBLE);

                if (bucket.isChosen) {
                    bucketListViewHolder.checkBox.setImageResource(R.drawable.ic_check_box_black_24dp);
                } else {
                    bucketListViewHolder.checkBox.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                }

                bucketListViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bucket.isChosen = !bucket.isChosen;
                        notifyItemChanged(position);

                    }
                });
            } else {
                bucketListViewHolder.checkBox.setVisibility(View.GONE);
                bucketListViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // if not in choosing mode, we need to open a new Activity to show
                        // what shots are in this bucket, we will need ShotListFragment here!
                        Intent intent = new Intent(context, BucketShotListActivity.class);
                        intent.putExtra(KEY_BUCKET_ID, bucket.id);
                        intent.putExtra(KEY_BUCKET_TITLE, bucket.title);
                        context.startActivity(intent);
                    }
                });
            }

        } else if (viewType == VIEW_TYPE_LOAD_MORE) {
            loadMoreListener.onLoadMore();
        }

    }

    @Override
    public int getItemCount () {
        return showLoading ? bucketList.size() + 1 : bucketList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position < bucketList.size() ? VIEW_TYPE_BUCKET_LIST : VIEW_TYPE_LOAD_MORE;
    }

    public int getDataCount () {
        return bucketList.size();
    }

    public void append(List<Bucket> buckets) {
        //bucketList.clear();
        bucketList.addAll(buckets);
        notifyDataSetChanged();
    }

    public void prepend (Bucket bucket) {
        bucketList.add(0, bucket);
        notifyItemInserted(0); //item reflected at position 0 has been newly inserted. The item previously at position is now at position position + 1
    }

    public void setShowLoading (boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    public List<Bucket> getCurrentSelectedBuckets () {
        List<Bucket> selectedBuckets = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            if (bucket.isChosen) {
                selectedBuckets.add(bucket);
            }
        }
        return selectedBuckets;
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }
}
