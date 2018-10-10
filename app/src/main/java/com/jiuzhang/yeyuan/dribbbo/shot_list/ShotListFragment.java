package com.jiuzhang.yeyuan.dribbbo.shot_list;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.base.WendoTask;
import com.jiuzhang.yeyuan.dribbbo.wendo.Wendo;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;
import com.jiuzhang.yeyuan.dribbbo.utils.VerticalSpaceItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment.KEY_BUCKET_ID;
import static com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailFragment.KEY_SHOT;


public class ShotListFragment extends Fragment {

    private static final int VERTICAL_SPACE_HEIGHT = 20;
    public static final int REQ_SHOT_UPDATE = 108;
    public static final String KEY_LIST_TYPE = "list_type";
    public static final String KEY_USER_NAME = "username";

    public static final int LIST_TYPE_POPULAR = 0;
    public static final int LIST_TYPE_LIKED = 1;
    public static final int LIST_TYPE_BUCKETED = 2;
    public static final int LIST_TYPE_USER_PHOTOS = 3;
    public static final int LIST_TYPE_USER_LIKES = 4;

    private List<Shot> shotList = new ArrayList<>();
    private ShotListAdapter adapter;

    public int listType;

    @BindView(R.id.shot_list_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    public ShotListFragment() {
        // Required empty public constructor
    }

    public static ShotListFragment newInstance(int listType, int bucketId, String username) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);
        args.putInt(KEY_BUCKET_ID, bucketId);
        args.putString(KEY_USER_NAME, username);
        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shot_list, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listType = getArguments().getInt(KEY_LIST_TYPE);

        adapter = new ShotListAdapter(this, shotList, new ShotListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                LoadShotsTask task = new LoadShotsTask(false);
                task.execute();
            }
        });

        swipeRefreshLayout.setEnabled(false); // During the first loading, disable swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadShotsTask task = new LoadShotsTask(true);
                task.execute();
                //adapter.setIsRefreshing(true);
                swipeRefreshLayout.setRefreshing(true); // Enable the refresh icon

            }
        });



        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_SPACE_HEIGHT));

        recyclerView.setAdapter(adapter);

//        if (adapter.getItemCount() == 0) {
//            noShotTextView.setVisibility(View.VISIBLE);
//        } else {
//            noShotTextView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SHOT_UPDATE && resultCode == RESULT_OK) {
            Shot updatedShot = ModelUtils.toObject(data.getStringExtra(KEY_SHOT),
                                                   new TypeToken<Shot>(){});
            for (Shot shot : adapter.getData()) {
                if (shot.id.equals(updatedShot.id)) {
                    shot.liked_by_user = updatedShot.liked_by_user;
                    shot.likes = updatedShot.likes;
                    shot.current_user_collections = updatedShot.current_user_collections;
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }

    }

    private class LoadShotsTask extends WendoTask<Void, Void, List<Shot>> {

        boolean refresh;

        public LoadShotsTask (boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        public List<Shot> doOnNewThread(Void... voids) throws Exception {
            int page = refresh ? 1 : adapter.getDataSetCount() / Wendo.COUNT_PER_PAGE + 1;
            String username;
            switch (listType) {
                case LIST_TYPE_POPULAR:
                    return Wendo.getShots(page);

                case LIST_TYPE_LIKED:
                    return Wendo.getLikedShots(page);

                case LIST_TYPE_BUCKETED:
                    int bucketId = getArguments().getInt(KEY_BUCKET_ID);
                    return Wendo.getBucketShots(page, bucketId);

                case LIST_TYPE_USER_PHOTOS:
                    username = getArguments().getString(KEY_USER_NAME);
                    return Wendo.getUserShots(username, page);

                case LIST_TYPE_USER_LIKES:
                    username = getArguments().getString(KEY_USER_NAME);
                    return Wendo.getUserLikes(username, page);
                default:
                    return null;
            }
        }

        @Override
        public void onSuccess (List<Shot> shots) {
            if (shots != null) {

                if (refresh) {
                    adapter.setData(shots);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    adapter.append(shots);
                }
                adapter.setShowLoading(shots.size() >= Wendo.COUNT_PER_PAGE);
                swipeRefreshLayout.setEnabled(true);
            }
        }

        @Override
        public void onFailed (Exception e) {
            Snackbar.make(getView(), "Something went wrong with the server", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Try again", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO:reload what did not load
                            Toast.makeText(getContext(), "reload!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
        }
    }
}
