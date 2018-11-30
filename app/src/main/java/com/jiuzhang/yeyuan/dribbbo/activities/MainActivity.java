package com.jiuzhang.yeyuan.dribbbo.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.base.WendoTask;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailFragment;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;
import com.jiuzhang.yeyuan.dribbbo.wendo.Wendo;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment;
import com.jiuzhang.yeyuan.dribbbo.shot_list.ShotListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jiuzhang.yeyuan.dribbbo.wendo.Wendo.USER_TYPE;

public class MainActivity extends AppCompatActivity {

    private final static String NEW = "New";
    private final static String FEATURED = "Featured";
    private final static String COLLECTIONS = "Collections";
    public static final String KEY_CURRENT_USER = "current_user";
    public static final int REQ_UPDATE_PROFILE = 100;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.tab) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle(getString(R.string.app_name));
        toolbar.setElevation(0);
        setupHamburgerIcon();
        setupDrawerContent();

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR, -1, "", ""));
        fragments.add(ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_FEATURED, -1, "", ""));
        fragments.add(BucketListFragment.newInstance(BucketListFragment.LIST_TYPE_ALL_BUCKET,false, null, null, ""));

        List<String> titles = new ArrayList<>();
        titles.add(NEW);
        titles.add(FEATURED);
        titles.add(COLLECTIONS);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Menu menu = navigationView.getMenu();
                switch (position) {
                    case 0:
                        menu.findItem(R.id.drawer_item_new).setChecked(true);
                        break;
                    case 1:
                        menu.findItem(R.id.drawer_item_featured).setChecked(true);
                        break;
                    case 2:
                        menu.findItem(R.id.drawer_item_buckets).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                Menu menu = navigationView.getMenu();
                switch (position) {
                    case 0:
                        menu.findItem(R.id.drawer_item_new).setChecked(true);
                        break;
                    case 1:
                        menu.findItem(R.id.drawer_item_featured).setChecked(true);
                        break;
                    case 2:
                        menu.findItem(R.id.drawer_item_buckets).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    private void setupHamburgerIcon() {
        //Enable the app bar's "home" button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Then change it to the menu icon
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        // set the hamburger icon to animate when open and close navigation drawer, include method onPostCreate and onConfigurationChanged
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.action_upload:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/submit"));
                startActivity(browserIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void setupDrawerContent() {
        View headerLayout = navigationView.getHeaderView(0);
        ImageView userImage = headerLayout.findViewById(R.id.nav_header_user_img);
        TextView userName = headerLayout.findViewById(R.id.nav_header_user_name);
        TextView logout = headerLayout.findViewById(R.id.nav_header_log_out);

        Glide.with(this)
                .load(Wendo.getCurrentUser().getProfileImageURL())
                .apply(new RequestOptions().placeholder(R.drawable.user_picture_placeholder))
                .apply(RequestOptions.circleCropTransform())
                .thumbnail(0.1f)
                .into(userImage);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadCurrentUserTask().execute();
            }
        });

        userName.setText(Wendo.getCurrentUser().name);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Wendo.logout(MainActivity.this);

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                if (item.isChecked()) {
//                    drawerLayout.closeDrawers();
//                    return true;
//                }
                // set item as selected to persist highlight
                item.setChecked(true);
//                setTitle(item.getTitle());
                selectDrawerItem(item);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_item_new:
                viewPager.setCurrentItem(0);
                break;
            case R.id.drawer_item_featured:
                viewPager.setCurrentItem(1);
                break;
            case R.id.drawer_item_buckets:
                viewPager.setCurrentItem(2);
                break;
            case R.id.drawer_item_profile:
                Intent intent = new Intent(this, EditProfileActivity.class);
                intent.putExtra(KEY_CURRENT_USER, ModelUtils.toString(Wendo.getCurrentUser(), USER_TYPE));
                startActivityForResult(intent, REQ_UPDATE_PROFILE);
                break;
            case R.id.drawer_item_setting:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // After going back to main activity, reset the check status of navigation items
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.drawer_item_profile).setChecked(false);
        int position = viewPager.getCurrentItem();
        switch (position) {
            case 0:
                menu.findItem(R.id.drawer_item_new).setChecked(true);
                break;
            case 1:
                menu.findItem(R.id.drawer_item_featured).setChecked(true);
                break;
            case 2:
                menu.findItem(R.id.drawer_item_buckets).setChecked(true);
                break;
        }

        if (resultCode == RESULT_OK && requestCode == REQ_UPDATE_PROFILE) {
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
            setupDrawerContent();
        }
    }

    private class LoadCurrentUserTask extends WendoTask<Void, Void, User> {

        @Override
        public User doOnNewThread(Void... voids) throws Exception {
            return Wendo.getUser();
        }

        @Override
        public void onSuccess(User user) {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            intent.putExtra(ShotDetailFragment.KEY_USER,
                    ModelUtils.toString(user, new TypeToken<User>(){}));
            startActivity(intent);
        }

        @Override
        public void onFailed(Exception e) {
            Snackbar.make(findViewById(android.R.id.content), "Load current user failed!",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments;
        private List<String> titles;

        public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}