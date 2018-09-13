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

    private class LoadShotsTask extends AsyncTask<Void, Void, List<Shot>> {

        int page;

        public LoadShotsTask (int page) {
            this.page = page;
        }

        @Override
        protected List<Shot> doInBackground(Void... voids) {
            try {
                return Dribbble.getShots(page);

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
                adapter.append(shots);
                adapter.setShowLoading(shots.size() == Dribbble.COUNT_PER_PAGE);
            }
        }
    }

//    private List<Shot> fakeData(int page) {
//        shotList = new ArrayList<>();
//        Random random = new Random();
//
//        int count = page < 2 ? Dribbble.COUNT_PER_PAGE : 10;
//
//        for (int i = 0; i < count; i++) {
//            Shot shot = new Shot();
//            User user = new User();
//            shot.theUser = user;
//            shot.likes = random.nextInt(400);
//            shot.views = random.nextInt(5000);
//            shot.downloads = random.nextInt(300);
//
//            shot.title = "shot " + i;
//            shot.description = makeDescription();
//            shot.theUser.name = "author " + i;
//            shot.imageURL = imageUrls[random.nextInt(imageUrls.length)];
//
//            shotList.add(shot);
//        }
//        return shotList;
//    }
//
//    private static final String[] words = {
//            "bottle", "bowl", "brick", "building", "bunny", "cake", "car", "cat", "cup",
//            "desk", "dog", "duck", "elephant", "engineer", "fork", "glass", "griffon", "hat", "key",
//            "knife", "lawyer", "llama", "manual", "meat", "monitor", "mouse", "tangerine", "paper",
//            "pear", "pen", "pencil", "phone", "physicist", "planet", "potato", "road", "salad",
//            "shoe", "slipper", "soup", "spoon", "star", "steak", "table", "terminal", "treehouse",
//            "truck", "watermelon", "window"
//    };
//
//    private static final String[] imageUrls = {
//            "https://cdn.dribbble.com/users/59947/screenshots/5116436/skato_boardo_dribbble.jpg",
//            "https://cdn.dribbble.com/users/1405777/screenshots/5116188/ingredients-dribbble.gif",
//            "https://cdn.dribbble.com/users/14268/screenshots/5116569/whalecentre_4x.png",
//            "https://cdn.dribbble.com/users/788099/screenshots/5108468/rooster_and_chickens_kit8-net.png",
//            "https://cdn.dribbble.com/users/1094383/screenshots/5116850/complete_4x.png",
//            "https://cdn.dribbble.com/users/1853242/screenshots/5118317/mountains.png",
//            "https://cdn.dribbble.com/users/772985/screenshots/5101143/2.jpg",
//            "https://cdn.dribbble.com/users/241205/screenshots/4990821/drib_00_node-lambda-cloud.png",
//            "https://cdn.dribbble.com/users/2738/screenshots/5117731/w_w_haunted_full_tiny.jpg",
//            "https://cdn.dribbble.com/users/107759/screenshots/5122322/cards_static3__4x.png",
//            "https://cdn.dribbble.com/users/102849/screenshots/5115962/desktop_messenger_4x.png",
//            "https://cdn.dribbble.com/users/497743/screenshots/5064852/cc_dribbble_post.png",
//            "https://cdn.dribbble.com/users/1568450/screenshots/5114477/belmarhealth_dribbblui_gyciui_2-01_4x.png",
//            "https://cdn.dribbble.com/users/380831/screenshots/5114247/mikdik-fashion-store-online-shop-ui-ux-design-webdesign-dribbble-shot17.png",
//            "https://cdn.dribbble.com/users/1502458/screenshots/5114131/artboard_copy_11_4x.png",
//            "https://cdn.dribbble.com/users/1127192/screenshots/5090295/as_explore_2x_4x.png",
//            "https://cdn.dribbble.com/users/812028/screenshots/5114507/db.jpg",
//            "https://cdn.dribbble.com/users/2002619/screenshots/5114298/poster_2_-_faster_-_retoque_psd_4x.jpg",
//            "https://cdn.dribbble.com/users/29553/screenshots/5114001/factory_dribbble_th_4x.jpg",
//            "https://cdn.dribbble.com/users/1338391/screenshots/5114581/001_character_dribbble.jpg"
//    };
//
//    private static String makeDescription() {
//        return TextUtils.join(" ", words);
//    }
}
