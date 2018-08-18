package com.jiuzhang.yeyuan.dribbbo.shot_list;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiuzhang.yeyuan.dribbbo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.shot_card_view) CardView shotCardView;
    @BindView(R.id.shot_image_view) ImageView shotImageView;
    @BindView(R.id.shot_count_text_view) TextView shotCountTextView;
    @BindView(R.id.shot_like_text_view) TextView shotLikeTextView;
    @BindView(R.id.shot_save_text_view) TextView shotSaveTextView;

    public ShotListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
