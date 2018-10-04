package com.jiuzhang.yeyuan.dribbbo;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jiuzhang.yeyuan.dribbbo.dribbble.Dribbble;
import com.jiuzhang.yeyuan.dribbbo.bucket_list.BucketListFragment;
import com.jiuzhang.yeyuan.dribbbo.shot_list.ShotListFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
//    @BindView(R.id.nav_header_user_name) TextView userName;
//    @BindView(R.id.nav_header_log_out) TextView logout;
//    @BindView(R.id.nav_header_user_img) ImageView userImage;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupHamburgerIcon();
        setupDrawerContent();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR, -1))
                .commit();

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
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent() {
        View headerLayout = navigationView.getHeaderView(0);
        ImageView userImage = headerLayout.findViewById(R.id.nav_header_user_img);
        TextView userName = headerLayout.findViewById(R.id.nav_header_user_name);
        TextView logout = headerLayout.findViewById(R.id.nav_header_log_out);

//        Picasso.with(this)
//                .load(Dribbble.getCurrentUser().getProfileImageURL())
//                .placeholder(R.drawable.user_picture_placeholder)
//                .into(userImage);

// User picture cannot load with Glide
        Glide.with(this)
                .load(Dribbble.getCurrentUser().getProfileImageURL())
                .apply(new RequestOptions().placeholder(R.drawable.user_picture_placeholder))
                .apply(RequestOptions.circleCropTransform())
                .thumbnail(0.1f)
                .into(userImage);

        userName.setText(Dribbble.getCurrentUser().name);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dribbble.logout(MainActivity.this);

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.isChecked()) {
                    drawerLayout.closeDrawers();
                    return true;
                }
                // set item as selected to persist highlight
                item.setChecked(true);
                // change the title in tool bar
                setTitle(item.getTitle());
                selectDrawerItem(item);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.drawer_item_home:
                fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR, -1);
                break;
            case R.id.drawer_item_likes:
                fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_LIKED, -1);
                break;
            case R.id.drawer_item_buckets:
                fragment = BucketListFragment.newInstance(false, null);
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }


    }
}