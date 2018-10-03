package com.jiuzhang.yeyuan.dribbbo.shot_detail;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.EditBucketActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotActionViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.shot_detail_view_count) TextView viewCount;
    @BindView(R.id.shot_detail_like_count) TextView likeCount;
    @BindView(R.id.img_like) ImageButton likeIcon;
    @BindView(R.id.action_collect) TextView collect;
    @BindView(R.id.action_share) TextView shareButton;

    public ShotActionViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }
}
