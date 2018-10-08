package com.jiuzhang.yeyuan.dribbbo.shot_list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jiuzhang.yeyuan.dribbbo.R;

public class EmptyMessageViewHolder extends RecyclerView.ViewHolder{

    public EmptyMessageViewHolder(View itemView) {
        super(itemView);
        TextView noShotTextView = itemView.findViewById(R.id.no_shot_text_view);
    }
}
