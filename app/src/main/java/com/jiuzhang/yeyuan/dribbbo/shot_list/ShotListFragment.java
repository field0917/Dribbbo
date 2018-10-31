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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.base.EmptyRecyclerView;
import com.jiuzhang.yeyuan.dribbbo.base.EndlessRecyclerViewScrollListener;
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
    public static final String KEY_QUERY = "query";

    public static final int LIST_TYPE_POPULAR = 0;
    public static final int LIST_TYPE_FEATURED = 1;
    public static final int LIST_TYPE_BUCKETED = 2;
    public static final int LIST_TYPE_USER_PHOTOS = 3;
    public static final int LIST_TYPE_USER_LIKES = 4;
    public static final int LIST_TYPE_SEARCH_PHOTOS = 5;

    private List<Shot> shotList = new ArrayList<>();
    private ShotListAdapter adapter;
    private int currentPage = 1;
    private boolean isLoading = false;

    public int listType;

    @BindView(R.id.shot_list_recycler_view) EmptyRecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.empty_view) TextView emptyView;

    public ShotListFragment() {
        // Required empty public constructor
    }

    public static ShotListFragment newInstance(int listType, int bucketId, String username, String query) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);
        args.putInt(KEY_BUCKET_ID, bucketId);
        args.putString(KEY_USER_NAME, username);
        args.putString(KEY_QUERY, query);
        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listType = getArguments().getInt(KEY_LIST_TYPE);
        adapter = new ShotListAdapter(this, shotList);
        if (!isLoading) {
            new LoadShotsTask(false, currentPage).execute();
        }
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

        swipeRefreshLayout.setEnabled(false); // During the first loading, disable swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                swipeRefreshLayout.setRefreshing(true); // Enable the refresh icon
                LoadShotsTask task = new LoadShotsTask(true, currentPage);
                task.execute();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_SPACE_HEIGHT));

        recyclerView.setAdapter(adapter);

        setEmptyViewText();

        recyclerView.setEmptyView(emptyView);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener() {
            @Override
            public void onLoadMore() {
                if (!isLoading) {
                    new LoadShotsTask(false, currentPage).execute();
                }
            }
        });

    }

    private void setEmptyViewText() {
        switch (listType) {
            case LIST_TYPE_BUCKETED:
                emptyView.setText(getString(R.string.bucket_no_photo));
                break;
            case LIST_TYPE_USER_PHOTOS:
                emptyView.setText(getString(R.string.user_no_photo));
                break;
            case LIST_TYPE_USER_LIKES:
                emptyView.setText(getString(R.string.user_no_like));
                break;
            case LIST_TYPE_SEARCH_PHOTOS:
                emptyView.setText(getString(R.string.no_search_result));
                break;
        }
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
        int page;

        public LoadShotsTask (boolean refresh, int page) {
            this.refresh = refresh;
            this.page = page;
        }

        @Override
        protected void onPreExecute() {
            isLoading = !isLoading;
            adapter.setShowLoading(isLoading);
        }

        @Override
        public List<Shot> doOnNewThread(Void... voids) throws Exception {

            String username;
            String query;
            switch (listType) {
                case LIST_TYPE_POPULAR:
                    return Wendo.getShots(page);

                case LIST_TYPE_FEATURED:
                    return Wendo.getCuratedShots(page);

                case LIST_TYPE_BUCKETED:
                    int bucketId = getArguments().getInt(KEY_BUCKET_ID);
                    return Wendo.getBucketShots(page, bucketId);

                case LIST_TYPE_USER_PHOTOS:
                    username = getArguments().getString(KEY_USER_NAME);
                    return Wendo.getUserShots(username, page);

                case LIST_TYPE_USER_LIKES:
                    username = getArguments().getString(KEY_USER_NAME);
                    return Wendo.getUserLikes(username, page);
                case LIST_TYPE_SEARCH_PHOTOS:
                    query = getArguments().getString(KEY_QUERY);
                    return Wendo.getSearchedShots(query, page);
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
                    Toast.makeText(getContext(), "Photos refreshed!", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.append(shots);
                }
                swipeRefreshLayout.setEnabled(true);
                currentPage += 1;
                isLoading = !isLoading;
            }
//            else {
//                Snackbar.make(getView(), "Error!", Snackbar.LENGTH_LONG).show();
//            }
            adapter.setShowLoading(isLoading);
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
            adapter.setShowLoading(false);
        }
    }
}
