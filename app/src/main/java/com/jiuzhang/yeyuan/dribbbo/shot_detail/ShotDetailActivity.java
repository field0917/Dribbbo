package com.jiuzhang.yeyuan.dribbbo.shot_detail;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.base.BaseActivity;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.shot_list.ShotListAdapter;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

public class ShotDetailActivity extends BaseActivity {


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("position", )
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected Fragment newFragment() {
        Intent intent = getIntent();
        Bundle extra = intent.getExtras();
        ShotDetailFragment fragment = ShotDetailFragment.newInstance(extra);

        return fragment;
    }

    @Override
    protected String getActivityTitle() {

        return super.getActivityTitle();
    }
}
