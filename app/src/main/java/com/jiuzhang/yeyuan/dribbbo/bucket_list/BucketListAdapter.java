package com.jiuzhang.yeyuan.dribbbo.bucket_list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;

import java.util.List;

public class BucketListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_BUCKET_LIST = 0;
    private static final int VIEW_TYPE_LOAD_MORE = 1;

    private List<Bucket> bucketList;
    private LoadMoreListener loadMoreListener;
    private boolean showLoading;
    private boolean isEditMode;

    public BucketListAdapter (List<Bucket> bucketList,
                              LoadMoreListener loadMoreListener,
                              boolean isEditMode) {
        this.bucketList = bucketList;
        this.loadMoreListener = loadMoreListener;
        this.showLoading = true;
        this.isEditMode = isEditMode;
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
    public void onBindViewHolder (@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_BUCKET_LIST) {
            Bucket bucket = bucketList.get(position);
            BucketListViewHolder bucketListViewHolder = (BucketListViewHolder) holder;
            bucketListViewHolder.bucketTitleTextView.setText(bucket.title);

            if (bucket.total_photos <= 1) {
                bucketListViewHolder.shotNumberTextView.setText(bucket.total_photos + " shot");
            } else {
                bucketListViewHolder.shotNumberTextView.setText(bucket.total_photos + " shots");
            }

            if (isEditMode) {
                bucketListViewHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                bucketListViewHolder.checkBox.setVisibility(View.GONE);
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

    public interface LoadMoreListener {
        void onLoadMore();
    }
}
