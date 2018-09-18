package com.jiuzhang.yeyuan.dribbbo.bucket_list;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jiuzhang.yeyuan.dribbbo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BucketListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.bucket_title_text_view) TextView bucketTitleTextView;
    @BindView(R.id.bucket_shot_number_text_view) TextView shotNumberTextView;
    @BindView(R.id.bucket_check_box) CheckBox checkBox;
    @BindView(R.id.bucket_card_view) CardView cardView;

    public BucketListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
