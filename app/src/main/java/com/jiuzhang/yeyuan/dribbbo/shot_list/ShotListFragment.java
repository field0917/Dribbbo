package com.jiuzhang.yeyuan.dribbbo.shot_list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.dribbble.Dribbble;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;
import com.jiuzhang.yeyuan.dribbbo.utils.VerticalSpaceItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShotListFragment extends Fragment {

    private static final int VERTICAL_SPACE_HEIGHT = 20;

//    private static final String MODEL_KEY_SHOT = "model key shot";
//    private static final String MODEL_KEY_LIKED_SHOT = "model key liked shot";

    private List<Shot> shotList = new ArrayList<>();
    private ShotListAdapter adapter;

    @BindView(R.id.shot_list_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    public ShotListFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        ShotListFragment fragment = new ShotListFragment();
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

        adapter = new ShotListAdapter(shotList, new ShotListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                LoadShotsTask task = new LoadShotsTask(adapter.getDataSetCount() / Dribbble.COUNT_PER_PAGE + 1);
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
    }

    private class LoadShotsTask extends AsyncTask<Void, Void, List<Shot>> {

        int page;
        boolean refresh;

        public LoadShotsTask (int page) {
            this.page = page;
        }

        public LoadShotsTask (boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Shot> doInBackground(Void... voids) {
            try {
                return refresh ? Dribbble.getShots(1) : Dribbble.getShots(page);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            //return fakeData(1);
        }

        @Override
        protected void onPostExecute(List<Shot> shots) {
            super.onPostExecute(shots);
            if (shots != null) {
                if (refresh) {
                    adapter.setData(shots);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    adapter.append(shots);
                }
                adapter.setShowLoading(shots.size() == Dribbble.COUNT_PER_PAGE);
                //adapter.setIsRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
            }
        }
    }

}
