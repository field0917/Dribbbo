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
    @BindView(R.id.shot_author_image) ImageView shotAuthorImg;
    @BindView(R.id.shot_author_name) TextView shotAuthorName;
    @BindView(R.id.shot_like_text_view) TextView shotLikeTextView;

    public ShotListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
