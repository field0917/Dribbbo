package com.jiuzhang.yeyuan.dribbbo.shot_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiuzhang.yeyuan.dribbbo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotInfoViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.shot_author_image) ImageView shotAuthorImage;
    //@BindView(R.id.shot_title) TextView shotTitle;
    @BindView(R.id.shot_author) TextView shotAuthor;
    //@BindView(R.id.shot_info) TextView shotInfo;

    public ShotInfoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
