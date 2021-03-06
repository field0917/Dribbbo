package com.jiuzhang.yeyuan.dribbbo.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment;
import com.jiuzhang.yeyuan.dribbbo.shot_list.ShotListFragment;
import com.jiuzhang.yeyuan.dribbbo.user_list.UserListFragment;
import com.jiuzhang.yeyuan.dribbbo.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    String query = "";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tab) TabLayout tabLayout;

    SearchPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        return true;
    }

    @Override
    protected void onNewIntent (Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(Intent.ACTION_SEARCH) && Utils.isConnectedToInternet(this)) {
            query = intent.getStringExtra(SearchManager.QUERY);
            viewPagerAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SearchPagerAdapter extends FragmentStatePagerAdapter {

        private static final int POSITION_ONE = 0;
        private static final int POSITION_TWO = 1;
        private static final int POSITION_THREE = 2;

        public SearchPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POSITION_ONE:
                    return ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_SEARCH_PHOTOS, -1, "", query);
                case POSITION_TWO:
                    return BucketListFragment.newInstance(BucketListFragment.LIST_TYPE_SEARCH_BUCKET,false, null, "", query);
                case POSITION_THREE:
                    return UserListFragment.newInstance(query);
                default:
                    return null;
            }
        }

        // This method is called when notifyDataSetChanged(), which will refresh fragments
        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case POSITION_ONE:
                    return "Photos";
                case POSITION_TWO:
                    return "Collections";
                case POSITION_THREE:
                    return "Users";
                default:
                    return null;
            }
        }
    }
}
