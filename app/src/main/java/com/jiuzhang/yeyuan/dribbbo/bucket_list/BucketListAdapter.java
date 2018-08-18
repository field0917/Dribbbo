package com.jiuzhang.yeyuan.dribbbo.bucket_list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;

import java.util.List;

public class BucketListAdapter extends RecyclerView.Adapter<BucketListViewHolder> {

    List<Bucket> bucketList;

    public BucketListAdapter (List<Bucket> bucketList) {
        this.bucketList = bucketList;
    }

    @NonNull
    @Override
    public BucketListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bucket_item, parent, false);
        return new BucketListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BucketListViewHolder holder, int position) {
        Bucket bucket = bucketList.get(position);
        holder.bucketNumberTextView.setText("Bucket " + bucket.bucketNumber);

        if (bucket.storeShotNumber <= 1) {
            holder.shotNumberTextView.setText(bucket.storeShotNumber + " shot");
        } else {
            holder.shotNumberTextView.setText(bucket.storeShotNumber + " shots");
        }
        holder.checkBox.setChecked(bucket.isChecked);
    }

    @Override
    public int getItemCount() {
        return bucketList.size();
    }
}
