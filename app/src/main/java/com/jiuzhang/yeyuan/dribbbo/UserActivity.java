package com.jiuzhang.yeyuan.dribbbo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.shot_detail.ShotDetailFragment;
import com.jiuzhang.yeyuan.dribbbo.shot_list.ShotListFragment;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tab) TabLayout tab;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.user_name) TextView userName;
    @BindView(R.id.user_location) TextView userLocation;
    @BindView(R.id.user_image) ImageView userImage;
    @BindView(R.id.user_photos) TextView userPhotos;
    @BindView(R.id.user_likes) TextView userLikes;
    @BindView(R.id.user_collections) TextView userCollections;
    @BindView(R.id.portfolio_url) TextView porfolioUrl;
    @BindView(R.id.instagram) TextView instagram;
    @BindView(R.id.bio) TextView bio;


    private static final int POSITION_ONE = 0;
    private static final int POSITION_TWO = 1;
    private static final int POSITION_THREE = 2;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setActivityContent();
    }

    private void setActivityContent() {
        Intent intent = getIntent();
        String userString = intent.getStringExtra(ShotDetailFragment.KEY_USER);
        user = ModelUtils.toObject(userString, new TypeToken<User>(){});

        userName.setText(user.name);

        if (user.location == null) {
            userLocation.setText("not known");
        } else {
            userLocation.setText(user.location);
        }

        Glide.with(this)
                .load(user.getProfileImageURL())
                .apply(new RequestOptions().placeholder(R.drawable.user_picture_placeholder))
                .apply(RequestOptions.circleCropTransform())
                .into(userImage);
        userPhotos.setText(String.valueOf(user.total_photos));
        userLikes.setText(String.valueOf(user.total_likes));
        userCollections.setText(String.valueOf(user.total_collections));
        if (user.portfolio_url != null) {
            porfolioUrl.setText(user.portfolio_url);
        } else {
            porfolioUrl.setVisibility(View.GONE);
        }
        if (user.instagram_username != null) {
            instagram.setText("Instagram: " + user.instagram_username);
        } else {
            instagram.setVisibility(View.GONE);
        }
        if (user.bio != null) {
            bio.setText(user.bio.trim());
        } else {
            bio.setText("Download free, beautiful high-quality photos curated by " + user.name);
        }

        // Setup viewpager's adapter
        UserPagerAdapter adapter = new UserPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Setup Tablayout with viewpager
        tab.setupWithViewPager(viewPager);
        tab.setBackgroundResource(R.color.colorPrimary);
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

    private class UserPagerAdapter extends FragmentStatePagerAdapter {

        public UserPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POSITION_ONE:
                    return ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_USER_PHOTOS, -1, user.username);
                case POSITION_TWO:
                    return ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_USER_LIKES, -1, user.username);
                case POSITION_THREE:
                    return BucketListFragment.newInstance(false,false, null, user.username);
                default:
                    return null;
            }
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
                    return "Likes";
                case POSITION_THREE:
                    return "Collections";
                default:
                    return null;
            }
        }
    }
}
