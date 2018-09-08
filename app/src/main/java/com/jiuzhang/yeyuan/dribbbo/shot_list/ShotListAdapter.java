package com.jiuzhang.yeyuan.dribbbo.shot_list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailActivity;
import com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailFragment;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShotListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_SHOT = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public static final String KEY_SHOT_TITLE = "shot title";

    private boolean showLoading;
//    private boolean isRefreshing;

    private List<Shot> shotList;
    private LoadMoreListener loadMoreListener;

    public ShotListAdapter (List<Shot> shotList, LoadMoreListener loadMoreListener) {
        this.shotList = shotList;
        this.loadMoreListener = loadMoreListener;
        this.showLoading = true;
//        this.isRefreshing = false;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_SHOT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shot_item, parent, false);
                return new ShotListViewHolder(view);
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.loading_item_activity, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_SHOT) {

            final Shot shot = shotList.get(position);
            final Context context = holder.itemView.getContext();

            ShotListViewHolder shotListViewHolder = (ShotListViewHolder) holder;

            shotListViewHolder.shotCountTextView.setText(String.valueOf(shot.viewCount));
            shotListViewHolder.shotLikeTextView.setText(String.valueOf(shot.likeCount));
            shotListViewHolder.shotSaveTextView.setText(String.valueOf(shot.saveCount));

//            Picasso.with(context)
//                    .load(Uri.parse(shot.coverImageURL))
//                    .into(shotListViewHolder.shotImageView);

            Glide.with(context)
                    .load(shot.coverImageURL)
                    .placeholder(R.drawable.shot_image_placeholder)
                    .thumbnail(0.1f)//Load a thumbnail at 1/10th the size of your view and then load the full image on top
                                    //This will reduce the time your user has to see image loading spinners without sacrificing quality
                    .into(shotListViewHolder.shotImageView);

            shotListViewHolder.shotCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, ShotDetailActivity.class);
                    String s = ModelUtils.toString(shot, new TypeToken<Shot>(){});
                    intent.putExtra(ShotDetailFragment.KEY_SHOT, s);
                    intent.putExtra(KEY_SHOT_TITLE, shot.shotTitle);
                    context.startActivity(intent);

                }
            });
        } else if (viewType == VIEW_TYPE_LOADING) {
            loadMoreListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount () {
        return showLoading ? shotList.size() + 1 : shotList.size();
    }

    public int getDataSetCount () {
        return shotList.size();
    }

    @Override
    public int getItemViewType (int position) {
        return position < shotList.size() ? VIEW_TYPE_SHOT : VIEW_TYPE_LOADING;
    }

    public void append(List<Shot> moreShots) {
        shotList.addAll(moreShots);
        notifyDataSetChanged();
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

//    public void setRefreshing(boolean isRefreshing) {
//        this.isRefreshing = isRefreshing;
//        notifyDataSetChanged();
//    }

    public interface LoadMoreListener {
        void onLoadMore();
    }
}
