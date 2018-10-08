package com.jiuzhang.yeyuan.dribbbo.shot_list;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.base.WendoException;
import com.jiuzhang.yeyuan.dribbbo.base.WendoTask;
import com.jiuzhang.yeyuan.dribbbo.wendo.Wendo;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailActivity;
import com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailFragment;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.io.IOException;
import java.util.List;

public class ShotListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_SHOT = 0;
    private final int VIEW_TYPE_LOADING = 1;
//    private final int VIEW_TYPE_NO_SHOT = 2;

    public static final String KEY_SHOT_TITLE = "shot title";

    private boolean showLoading;

    private ShotListFragment shotListFragment;
    private List<Shot> shotList;
    private LoadMoreListener loadMoreListener;

    public ShotListAdapter (ShotListFragment shotListFragment, List<Shot> shotList, LoadMoreListener loadMoreListener) {
        this.shotListFragment = shotListFragment;
        this.shotList = shotList;
        this.loadMoreListener = loadMoreListener;
        this.showLoading = true;
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
//            case VIEW_TYPE_NO_SHOT:
//                view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.empty_message, parent, false);
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

            final ShotListViewHolder shotListViewHolder = (ShotListViewHolder) holder;

            shotListViewHolder.shotLikeTextView.setText(String.valueOf(shot.likes));
            shotListViewHolder.shotAuthorName.setText(shot.user.name);

            Glide.with(context)
                    .load(shot.user.getProfileImageURL())
                    .apply(new RequestOptions().placeholder(R.drawable.user_picture_placeholder))
                    .apply(RequestOptions.circleCropTransform()) // make the image circle
                    .thumbnail(0.1f)
                    .into(shotListViewHolder.shotAuthorImg);

            Glide.with(context)
                    .load(shot.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.shot_image_placeholder)
                            .error(R.drawable.error_image_not_found))
                    .thumbnail(0.1f)//Load a thumbnail at 1/10th the size of your view and then load the full image on top
                                    //This will reduce the time your user has to see image loading spinners without sacrificing quality
                    .into(shotListViewHolder.shotImageView);

            shotListViewHolder.shotCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Intent intent = new Intent(context, ShotDetailActivity.class);
//                    String s = ModelUtils.toString(shot, new TypeToken<Shot>(){});
//                    intent.putExtra(ShotDetailFragment.KEY_SHOT, s);
//                    intent.putExtra(KEY_SHOT_TITLE, shot.id);
//                    shotListFragment.startActivityForResult(intent, ShotListFragment.REQ_SHOT_UPDATE);
                    LoadFullShotDetailTask task = new LoadFullShotDetailTask();
                    task.execute(shot.id);
                }
            });
        } else if (viewType == VIEW_TYPE_LOADING) {
            loadMoreListener.onLoadMore();
        }
//        else if (viewType == VIEW_TYPE_NO_SHOT) {
//            EmptyMessageViewHolder viewHolder = (EmptyMessageViewHolder) holder;
//        }
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
//        if (shotList == null || shotList.size() == 0) {
//            return VIEW_TYPE_NO_SHOT;
//        }
        return position < shotList.size() ? VIEW_TYPE_SHOT : VIEW_TYPE_LOADING;
    }

    public void append(@NonNull List<Shot> moreShots) {
        shotList.addAll(moreShots);
        notifyDataSetChanged();
    }

    public void setData(@NonNull List<Shot> data) {
        shotList.clear();
        shotList.addAll(data);
        notifyDataSetChanged();
    }

    public List<Shot> getData() {
        return shotList;
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }

    public class LoadFullShotDetailTask extends WendoTask<String, Void, Shot> {

        @Override
        public Shot doOnNewThread(String... strings) throws Exception {
            String shotId = strings[0];
            return Wendo.getShot(shotId);
        }

        @Override
        public void onSuccess(Shot shot) {
            Intent intent = new Intent(shotListFragment.getContext(), ShotDetailActivity.class);
            String s = ModelUtils.toString(shot, new TypeToken<Shot>(){});
            intent.putExtra(ShotDetailFragment.KEY_SHOT, s);
            intent.putExtra(KEY_SHOT_TITLE, shot.id);
            shotListFragment.startActivityForResult(intent, ShotListFragment.REQ_SHOT_UPDATE);
        }

        @Override
        public void onFailed(Exception e) {
            Snackbar.make(shotListFragment.getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}
