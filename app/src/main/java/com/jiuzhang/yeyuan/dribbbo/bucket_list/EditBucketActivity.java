package com.jiuzhang.yeyuan.dribbbo.bucket_list;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.jiuzhang.yeyuan.dribbbo.base.BaseActivity;

import java.util.ArrayList;

public class EditBucketActivity extends BaseActivity{

    @Override
    protected Fragment newFragment() {
        return BucketListFragment.newInstance(true, new ArrayList<Integer>());
    }

    @Override
    protected String getActivityTitle() {
        return "Choose Bucket";
    }
}
