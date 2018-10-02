package com.jiuzhang.yeyuan.dribbbo.bucket_list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.base.BaseActivity;
import com.jiuzhang.yeyuan.dribbbo.dribbble.Dribbble;
import com.jiuzhang.yeyuan.dribbbo.model.Bucket;
import com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailAdapter;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.util.ArrayList;
import java.util.List;

public class EditBucketActivity extends BaseActivity{

    @Override
    protected Fragment newFragment() {
        boolean isEditMode = getIntent()
                .getBooleanExtra(BucketListFragment.KEY_EDIT_MODE, false);
        List<Bucket> chosenBuckets = ModelUtils.toObject(
                getIntent().getStringExtra(BucketListFragment.KEY_COLLECTED_BUCKETS),
                new TypeToken<List<Bucket>>(){});
        return BucketListFragment.newInstance(isEditMode, chosenBuckets);
    }

    @Override
    protected String getActivityTitle() {
        return "Choose Bucket";
    }
}
