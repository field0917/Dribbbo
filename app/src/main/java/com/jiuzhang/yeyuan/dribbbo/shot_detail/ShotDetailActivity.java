package com.jiuzhang.yeyuan.dribbbo.shot_detail;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.base.BaseActivity;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.shot_list.ShotListAdapter;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotDetailActivity extends BaseActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shot_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download:
                Toast.makeText(this, "download!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.info:
                Toast.makeText(this, "info!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.url:
                Toast.makeText(this, "URL!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

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
