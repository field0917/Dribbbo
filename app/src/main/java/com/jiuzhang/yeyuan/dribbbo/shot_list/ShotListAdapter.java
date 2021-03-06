package com.jiuzhang.yeyuan.dribbbo.shot_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.activities.UserActivity;
import com.jiuzhang.yeyuan.dribbbo.base.WendoTask;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.utils.ImageUtils;
import com.jiuzhang.yeyuan.dribbbo.utils.Utils;
import com.jiuzhang.yeyuan.dribbbo.wendo.Wendo;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailActivity;
import com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailFragment;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.util.List;

import okhttp3.internal.Util;

import static com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailFragment.KEY_USER;

public class ShotListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_SHOT = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public static final String KEY_SHOT_TITLE = "shot title";

    private ShotListFragment shotListFragment;
    private List<Shot> shotList;
    private boolean showLoading = false;
    private ShotListViewHolder shotListViewHolder;

    public ShotListAdapter (ShotListFragment shotListFragment,
                            List<Shot> shotList) {
        this.shotListFragment = shotListFragment;
        this.shotList = shotList;
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

            shotListViewHolder = (ShotListViewHolder) holder;

            shotListViewHolder.shotAuthorName.setText(shot.user.name);

            ImageUtils.loadCircleUserImage(context,
                                           shot.user.getProfileImageURL(),
                                           shotListViewHolder.shotAuthorImg);
            ImageUtils.loadShotImage(context, shot.getImageUrl(), shotListViewHolder.shotImageView);

            shotListViewHolder.shotCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    disableCardViewClickability();
//                    if (Utils.isConnectedToInternet(shotListFragment.getContext())) {
                        LoadFullShotDetailTask task = new LoadFullShotDetailTask();
                        task.execute(shot.id);
//                    } else {
//                        Toast.makeText(shotListFragment.getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
//                    }
                }
            });

            shotListViewHolder.shotAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserActivity.class);
                    intent.putExtra(KEY_USER, ModelUtils.toString(shot.user, new TypeToken<User>(){}));
                    context.startActivity(intent);

                }
            });
        }
    }

//    public void disableCardViewClickability() {
//        shotListViewHolder.shotCardView.setEnabled(false);
//    }
//
//    public void enableCardViewClickability() {
//        shotListViewHolder.shotCardView.setEnabled(true);
//    }

    @Override
    public int getItemCount () {
        return showLoading ? shotList.size() + 1 : shotList.size();
//        return shotList.size();
    }

    public void setShowLoading (boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType (int position) {
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

    public class LoadFullShotDetailTask extends WendoTask<String, Void, Shot> {

        @Override
        public void checkInternetConnection() {
            if (!Utils.isConnectedToInternet(shotListFragment.getContext())) {
                Toast.makeText(shotListFragment.getContext(), "No Internet Test", Toast.LENGTH_SHORT).show();
            }
        }

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
