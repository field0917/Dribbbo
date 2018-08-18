package com.jiuzhang.yeyuan.dribbbo.shot_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.jiuzhang.yeyuan.dribbbo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotImageViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.shot_detail_image_view) ImageView imageView;

    public ShotImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
