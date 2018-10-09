package com.jiuzhang.yeyuan.dribbbo.bucket_list;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.base.WendoException;
import com.jiuzhang.yeyuan.dribbbo.base.WendoTask;
import com.jiuzhang.yeyuan.dribbbo.wendo.Wendo;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;
import com.jiuzhang.yeyuan.dribbbo.utils.VerticalSpaceItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class BucketListFragment extends Fragment {

    private static final int VERTICAL_SPACE_HEIGHT = 20;
    private static final int COUNT_PER_PAGE = 7;

    public static final int REQ_NEW_BUCKET = 106;

    public static final String KEY_EDIT_MODE = "edit_mode";
    public static final String KEY_COLLECTED_BUCKETS = "collected_buckets";
    public static final String KEY_CHOSEN_BUCKETS = "chosen_buckets";
    public static final String KEY_BUCKET_TITLE = "bucket_title";
    public static final String KEY_BUCKET_ID = "bucket_id";
    public static final String KEY_USER_NAME = "username";

    @BindView(R.id.bucket_list_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.bucket_fab) FloatingActionButton fab;

    private BucketListAdapter adapter;
    private List<Bucket> bucketList = new ArrayList<>();

    public boolean isEditMode;
    public Set<Integer> chosenBucketIdsSet;
    public String username;

    public BucketListFragment() {
        // Required empty public constructor
    }

    public static BucketListFragment newInstance(boolean isEditMode, List<Bucket> chosenBuckets, String username) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_EDIT_MODE, isEditMode);
        args.putString(KEY_COLLECTED_BUCKETS,
                ModelUtils.toString(chosenBuckets, new TypeToken<List<Bucket>>(){}));
        args.putString(KEY_USER_NAME, username);

        BucketListFragment fragment = new BucketListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        isEditMode = args.getBoolean(KEY_EDIT_MODE);
        username = args.getString(KEY_USER_NAME);

        if (isEditMode) {
            List<Bucket> chosenBuckets = ModelUtils.toObject(args.getString(KEY_COLLECTED_BUCKETS),
                    new TypeToken<List<Bucket>>(){});
            List<Integer> chosenBucketIds = getBucketIds(chosenBuckets);
            chosenBucketIdsSet = chosenBucketIds == null ? new HashSet<Integer>() :
                                                                       new HashSet<>(chosenBucketIds);
        }

        adapter = new BucketListAdapter(bucketList, new BucketListAdapter.LoadMoreListener() {

            @Override
            public void onLoadMore() {
                LoadBucketTask task = new LoadBucketTask();
                task.execute();

            }
        }, isEditMode);

        if (isEditMode) {setHasOptionsMenu(true);}

    }

    private List<Integer> getBucketIds(List<Bucket> chosenBuckets) {
        List<Integer> bucketIds = new ArrayList<>();
        for (Bucket bucket : chosenBuckets) {
            bucketIds.add(bucket.id);
        }
        return bucketIds;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isEditMode) {
            inflater.inflate(R.menu.edit_bucket_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            List<Bucket> currentChosenBuckets = adapter.getCurrentSelectedBuckets();
            Intent result = new Intent();
            result.putExtra(KEY_CHOSEN_BUCKETS, ModelUtils.toString(currentChosenBuckets,
                    new TypeToken<List<Bucket>>(){}));
            getActivity().setResult(RESULT_OK, result);
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
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

        if (username != null) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NewBucketDialogFragment dialogFragment = NewBucketDialogFragment.newInstance();
                    dialogFragment.setTargetFragment(BucketListFragment.this, REQ_NEW_BUCKET);
                    dialogFragment.show(getFragmentManager(), NewBucketDialogFragment.TAG);
                }
            });
        }

//        if (adapter.getItemCount() == 0) {
//            noBucketTextView.setVisibility(View.VISIBLE);
//        } else {
//            noBucketTextView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_NEW_BUCKET && resultCode == RESULT_OK) {
            String bucketName = data.getStringExtra(NewBucketDialogFragment.KEY_NEW_BUCKET_NAME);
            String bucketDes = data.getStringExtra(NewBucketDialogFragment.KEY_NEW_BUCKET_DESCRIPTION);

            if (!TextUtils.isEmpty(bucketName)) {
                NewBucketsTask task = new NewBucketsTask(bucketName, bucketDes);
                task.execute();
            }

        }
    }

    private class LoadBucketTask extends WendoTask<Void, Void, List<Bucket>> {

        @Override
        public List<Bucket> doOnNewThread(Void... voids) throws Exception {
            if (username == null) {
                return Wendo.getBuckets();
            } else {
                return Wendo.getUserBuckets(username);
            }

        }

        @Override
        public void onSuccess(List<Bucket> buckets) {
            if (buckets != null) {

                if (isEditMode) {
                    // check if the shot has been stored in any bucket, if true, set the isChosen true
                    // then add buckets to adapter
                    for (Bucket bucket : buckets) {
                        if (chosenBucketIdsSet.contains(bucket.id)) {
                            bucket.isChosen = true;
                        }
                    }
                }
                adapter.append(buckets);
                adapter.setShowLoading(buckets.size() >= COUNT_PER_PAGE);

            } else {
                Snackbar.make(getView(), "Error!", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailed(Exception e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class NewBucketsTask extends WendoTask<Void, Void, Bucket> {

        String bucketName;
        String bucketDes;

        public NewBucketsTask (String bucketName, String bucketDes) {
            this.bucketName = bucketName;
            this.bucketDes = bucketDes;
        }

        @Override
        public Bucket doOnNewThread(Void... voids) throws IOException {
            return Wendo.createNewBucket(bucketName, bucketDes);
        }

        @Override
        public void onSuccess(Bucket bucket) {
            if (bucket != null) {
                adapter.prepend(bucket);
            } else {
                Snackbar.make(getView(), "Error!", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailed(Exception e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }


}
