package com.jiuzhang.yeyuan.dribbbo.shot_detail;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.EditBucketActivity;
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
    private List<Bucket> collectedBuckets;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void setResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_SHOT, ModelUtils.toString(shot, new TypeToken<Shot>(){}));
        getActivity().setResult(RESULT_OK, resultIntent);
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
        if (collectedBuckets == null || collectedBuckets.isEmpty()) {
            collectedBuckets = shot.current_user_collections;// The *current user's* collections that this photo belongs to.
        }
        adapter = new ShotDetailAdapter(this, shot);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CHOSEN_BUCKET && resultCode == RESULT_OK) {

            List<Bucket> currentChosenBuckets = ModelUtils.toObject(
                    data.getStringExtra(BucketListFragment.KEY_CHOSEN_BUCKETS),
                    new TypeToken<List<Bucket>>(){});
            List<Bucket> addedBuckets = new ArrayList<>();
            List<Bucket> removedBuckets = new ArrayList<>();

            for (Bucket currentChosenBucket : currentChosenBuckets) {
                if (!collectedBuckets.contains(currentChosenBucket)) {
                    addedBuckets.add(currentChosenBucket);
                }
            }

            for (Bucket collectedBucket : collectedBuckets) {
                if (!currentChosenBuckets.contains(collectedBucket)) {
                    removedBuckets.add(collectedBucket);
                }
            }

            UpdateCollectedBucketTask task = new UpdateCollectedBucketTask();
            task.execute(addedBuckets, removedBuckets);
        }
    }

    public void bucket (Context context) {
        if (collectedBuckets == null) {
            Snackbar.make(getView(), R.string.shot_detail_loading_buckets, Snackbar.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(context, EditBucketActivity.class);
            intent.putExtra(BucketListFragment.KEY_EDIT_MODE, true);
            intent.putExtra(BucketListFragment.KEY_COLLECTED_BUCKETS,
                    ModelUtils.toString(collectedBuckets, new TypeToken<List<Bucket>>(){}));//give the selectedBuckets to EditBucketActivity
            startActivityForResult(intent, ShotDetailFragment.REQ_CHOSEN_BUCKET);
        }

    }

    public void share (Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, shot.getHtmlUrl());
        intent.setType("text/plain");
        context.startActivity(Intent.createChooser(intent, context.getResources().getText(R.string.send_to)));
    }

    private class UpdateCollectedBucketTask extends AsyncTask<List<Bucket>, Void, Shot> {

        private List<Bucket> addedBuckets;
        private List<Bucket> removedBuckets;

        @Override
        protected Shot doInBackground(List<Bucket>... lists) {
            addedBuckets = lists[0];
            removedBuckets = lists[1];

            addShotToBucket(addedBuckets);

            removeShotFromBucket(removedBuckets);

            return null;
        }

        protected Shot addShotToBucket (List<Bucket> addedBuckets) {
            Shot newShot = null;
            for (Bucket bucket : addedBuckets) {
                try {
                    newShot = Dribbble.addShotToBucket(shot.id, bucket.id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return newShot;
        }

        protected Shot removeShotFromBucket (List<Bucket> removedBuckets) {
            Shot newShot = null;
            for (Bucket bucket : removedBuckets) {
                try {
                    newShot = Dribbble.removeShotFromBucket(shot.id, bucket.id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return newShot;
        }

        @Override
        protected void onPostExecute(Shot updatedShot) {
            if (collectedBuckets == null) {
                collectedBuckets = new ArrayList<>();
            }

            collectedBuckets.addAll(addedBuckets);
            collectedBuckets.removeAll(removedBuckets);

            shot.bucketed = !collectedBuckets.isEmpty();
            shot.current_user_collections.addAll(addedBuckets);
            shot.current_user_collections.removeAll(removedBuckets);
            adapter.notifyDataSetChanged();

            setResult();
        }

    }
}
