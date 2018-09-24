package com.jiuzhang.yeyuan.dribbbo.bucket_list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.jiuzhang.yeyuan.dribbbo.base.BaseActivity;
import com.jiuzhang.yeyuan.dribbbo.dribbble.Dribbble;
import com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailAdapter;

import java.util.ArrayList;

public class EditBucketActivity extends BaseActivity{

    @Override
    protected Fragment newFragment() {
        ArrayList<Integer> chosenBucketIds = getIntent().getIntegerArrayListExtra(BucketListFragment.KEY_CHOSEN_BUCKET_ID_LIST);
        return BucketListFragment.newInstance(true, chosenBucketIds);
    }

    @Override
    protected String getActivityTitle() {
        return "Choose Bucket";
    }
}
