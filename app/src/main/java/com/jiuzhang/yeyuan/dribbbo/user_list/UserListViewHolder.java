package com.jiuzhang.yeyuan.dribbbo.user_list;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiuzhang.yeyuan.dribbbo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.user_card_view) CardView cardView;
    @BindView(R.id.user_list_image) ImageView userImage;
    @BindView(R.id.user_list_name) TextView name;
    @BindView(R.id.user_list_username) TextView username;

    public UserListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
