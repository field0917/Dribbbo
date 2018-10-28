package com.jiuzhang.yeyuan.dribbbo.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.shot_list.ShotListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    String query = "";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.empty_view) TextView emptyView;
    @BindView(R.id.fragment_container) FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        handleIntent(intent);
    }

    private void handleIntent (Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            if (!query.equals("")) {
                emptyView.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFragment())
                        .commit();
            } else {
                emptyView.setVisibility(View.VISIBLE);
                container.setVisibility(View.GONE);
            }
        }
    }

    private Fragment newFragment() {
        return ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_SEARCH_PHOTOS, -1, "", query);
    }
}
