package com.jiuzhang.yeyuan.dribbbo.bucket_list;

import android.support.v4.app.Fragment;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.base.BaseActivity;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.util.List;

public class EditBucketActivity extends BaseActivity{

    @Override
    protected Fragment newFragment() {
        boolean isEditMode = getIntent()
                .getBooleanExtra(BucketListFragment.KEY_EDIT_MODE, false);
        List<Bucket> chosenBuckets = ModelUtils.toObject(
                getIntent().getStringExtra(BucketListFragment.KEY_COLLECTED_BUCKETS),
                new TypeToken<List<Bucket>>(){});
        return BucketListFragment.newInstance(isEditMode, chosenBuckets, null);
    }

    @Override
    protected String getActivityTitle() {
        return "Choose Bucket";
    }
}
