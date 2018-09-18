package com.jiuzhang.yeyuan.dribbbo.bucket_list;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.dribbble.Dribbble;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;
import com.jiuzhang.yeyuan.dribbbo.utils.VerticalSpaceItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BucketListFragment extends Fragment {

    private static final int VERTICAL_SPACE_HEIGHT = 20;
    private static final int COUNT_PER_PAGE = 7;

    @BindView(R.id.bucket_list_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.bucket_fab) FloatingActionButton fab;

    private BucketListAdapter adapter;
    private List<Bucket> bucketList = new ArrayList<>();

    public BucketListFragment() {
        // Required empty public constructor
    }

    public static BucketListFragment newInstance() {
        BucketListFragment fragment = new BucketListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //bucketList = fakeData(1);

        //final Handler handler = new Handler();
        adapter = new BucketListAdapter(bucketList, new BucketListAdapter.LoadMoreListener() {

            @Override
            public void onLoadMore() {
                LoadBucketTask task = new LoadBucketTask();
                task.execute();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(1000);
//                            final List<Bucket> moreData = fakeData(adapter.getDataCount() / COUNT_PER_PAGE);
//
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    adapter.append(moreData);
//                                    adapter.setShowLoading(moreData.size() >= COUNT_PER_PAGE);
//                                }
//                            });
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bucket_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_SPACE_HEIGHT));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Fab clicked!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class LoadBucketTask extends AsyncTask<Void, Void, List<Bucket>> {

        @Override
        protected List<Bucket> doInBackground(Void... voids) {
            try {
                return Dribbble.getBuckets();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Bucket> buckets) {
            if (buckets != null) {
                adapter.append(buckets);
                adapter.setShowLoading(buckets.size() >= COUNT_PER_PAGE);
            }

        }
    }

//    private List<Bucket> fakeData(int page) {
//
//        List<Bucket> buckets = new ArrayList<>();
//
//        Random random = new Random();
//
//        int count = page < 2 ? COUNT_PER_PAGE : 3;
//
//
//        for (int i = 0; i < count; i++) {
//            Bucket bucket = new Bucket();
//            bucket.bucketNumber = i;
//            bucket.storeShotNumber = random.nextInt(30);
//            buckets.add(bucket);
//        }
//
//        return buckets;
//    }
}
