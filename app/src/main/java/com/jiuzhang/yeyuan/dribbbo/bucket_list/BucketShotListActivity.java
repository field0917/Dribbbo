package com.jiuzhang.yeyuan.dribbbo.bucket_list;

import android.support.v4.app.Fragment;

import com.jiuzhang.yeyuan.dribbbo.base.BaseActivity;
import com.jiuzhang.yeyuan.dribbbo.shot_list.ShotListFragment;

import static com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment.KEY_BUCKET_ID;

public class BucketShotListActivity extends BaseActivity {

    @Override
    protected Fragment newFragment() {
        int bucketId = getIntent().getIntExtra(KEY_BUCKET_ID, -1);
        ShotListFragment fragment = ShotListFragment.newInstance(
                ShotListFragment.LIST_TYPE_BUCKETED, bucketId);
        return fragment;
    }

    @Override
    protected String getActivityTitle() {
        String title = getIntent().getStringExtra(BucketListFragment.KEY_BUCKET_TITLE);
        return title;
    }
}
