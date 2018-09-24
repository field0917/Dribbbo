package com.jiuzhang.yeyuan.dribbbo.shot_detail;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment;
import com.jiuzhang.yeyuan.dribbbo.dribbble.Dribbble;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class ShotDetailFragment extends Fragment {

    public static final String KEY_SHOT = "shot";
    public static final int REQ_CHOSEN_BUCKET = 107 ;

    private ShotDetailAdapter adapter;
    private Shot shot;

    @BindView(R.id.shot_detail_recycler_view) RecyclerView recyclerView;

    public ShotDetailFragment() {
        // Required empty public constructor
    }


    public static ShotDetailFragment newInstance(Bundle args) {
        ShotDetailFragment fragment = new ShotDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shot_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shot = ModelUtils.toObject(getArguments().getString(KEY_SHOT), new TypeToken<Shot>(){});
        adapter = new ShotDetailAdapter(this, shot);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CHOSEN_BUCKET && resultCode == RESULT_OK) {

            List<Integer> currentChosenBucketIds = data
                    .getIntegerArrayListExtra(BucketListFragment.KEY_CHOSEN_BUCKET_ID_LIST);
            List<Integer> addedBucketIds = new ArrayList<>();
            List<Integer> removedBucketIds = new ArrayList<>();
            List<Integer> collectedBucketIds = adapter.getReadOnlyCollectedBucketIds();

            for (int currentChosenBucketId : currentChosenBucketIds) {
                if (!collectedBucketIds.contains(currentChosenBucketId)) {
                    addedBucketIds.add(currentChosenBucketId);
                }
            }

            for (int collectedBucketId : collectedBucketIds) {
                if (!currentChosenBucketIds.contains(collectedBucketId)) {
                    removedBucketIds.add(collectedBucketId);
                }
            }

            UpdateBucketTask task = new UpdateBucketTask();
            task.execute(addedBucketIds, removedBucketIds);
        }
    }

    private class UpdateBucketTask extends AsyncTask<List<Integer>, Void, Void> {

        private List<Integer> addedBucketIds;
        private List<Integer> removedBucketIds;
        
        @Override
        protected Void doInBackground(List<Integer>... lists) {
            addedBucketIds = lists[0];
            removedBucketIds = lists[1];

            addShotToBucket(addedBucketIds);
            removeShotFromBucket(removedBucketIds);

            return null;
        }

        protected void addShotToBucket (List<Integer> addedBucketIds) {
            for (int bucketID : addedBucketIds) {
                try {
                    Dribbble.addShotToBucket(shot.id, bucketID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        protected void removeShotFromBucket (List<Integer> removedBucketIds) {
            for (int bucketID : removedBucketIds) {
                try {
                    Dribbble.removeShotFromBucket(shot.id, bucketID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.updateCollectedBucketIDs(addedBucketIds, removedBucketIds);
        }
    }
}
