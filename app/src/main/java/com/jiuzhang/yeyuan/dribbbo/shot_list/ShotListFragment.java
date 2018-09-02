package com.jiuzhang.yeyuan.dribbbo.shot_list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;
import com.jiuzhang.yeyuan.dribbbo.utils.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShotListFragment extends Fragment {

    private static final int VERTICAL_SPACE_HEIGHT = 20;
    private static final int COUNT_PER_PAGE = 12;

//    private static final String MODEL_KEY_SHOT = "model key shot";
//    private static final String MODEL_KEY_LIKED_SHOT = "model key liked shot";

    private List<Shot> shotList;
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
        shotList = fakeData(1);
        adapter = new ShotListAdapter(shotList, new ShotListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    List<Shot> moreShots = fakeData(adapter.getDataSetCount() / COUNT_PER_PAGE);
                                    adapter.append(moreShots);
                                    adapter.setShowLoading(moreShots.size() >= COUNT_PER_PAGE);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
    //TODO: 第七章小视频 2.Pull to refresh
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Toast.makeText(getContext(), "Loading 12 new shots", Toast.LENGTH_SHORT).show();
//                swipeRefreshLayout.setRefreshing(true);
//            }
//        });

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_SPACE_HEIGHT));

        recyclerView.setAdapter(adapter);
    }

//    private class LoadShotsTask extends AsyncTask<Void, Void, List<Shot>> {
//
//        @Override
//        protected List<Shot> doInBackground(Void... voids) {
//            List<Shot> moreShots = fakeData(adapter.getDataSetCount() / COUNT_PER_PAGE);
//            adapter.append(moreShots);
//            adapter.setShowLoading(moreShots.size() >= COUNT_PER_PAGE);
//        }
//    }

    private List<Shot> fakeData(int page) {
        shotList = new ArrayList<>();
        Random random = new Random();

        int count = page < 2 ? COUNT_PER_PAGE : 10;

        for (int i = 0; i < count; i++) {
            Shot shot = new Shot();
            User user = new User();
            shot.user = user;
            shot.likeCount = random.nextInt(400);
            shot.viewCount = random.nextInt(5000);
            shot.saveCount = random.nextInt(300);

            shot.shotTitle = "shot " + i;
            shot.shotInfo = makeDescription();
            shot.user.name = "author " + i;

            shotList.add(shot);
        }
        return shotList;
    }

    private static final String[] words = {
            "bottle", "bowl", "brick", "building", "bunny", "cake", "car", "cat", "cup",
            "desk", "dog", "duck", "elephant", "engineer", "fork", "glass", "griffon", "hat", "key",
            "knife", "lawyer", "llama", "manual", "meat", "monitor", "mouse", "tangerine", "paper",
            "pear", "pen", "pencil", "phone", "physicist", "planet", "potato", "road", "salad",
            "shoe", "slipper", "soup", "spoon", "star", "steak", "table", "terminal", "treehouse",
            "truck", "watermelon", "window"
    };

    private static String makeDescription() {
        return TextUtils.join(" ", words);
    }
}
