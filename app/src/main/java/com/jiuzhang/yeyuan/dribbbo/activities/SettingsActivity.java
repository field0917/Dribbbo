package com.jiuzhang.yeyuan.dribbbo.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jiuzhang.yeyuan.dribbbo.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements ItemPickerDialogFragment.OnItemSelectedListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.clear_cache) LinearLayout clearCache;
    @BindView(R.id.cache_size) TextView cacheSize;
    @BindView(R.id.download_quality) LinearLayout downloadQuality;

    private static final String[] QUALITIES = {"raw", "full", "regular", "small", "thumb"};
    public static final String SP_SETTINGS = "settings";
    public static final String KEY_DOWNLOAD_QUALITY = "download_quality";

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        sp = this.getSharedPreferences(SP_SETTINGS, MODE_PRIVATE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.drawer_menu_settings);

        setContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setContent() {
        cacheSize.setText(getString(R.string.cache_size) + " " + cacheSizeConverter());
        clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCache(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, "Cache Cleared!", Toast.LENGTH_SHORT).show();
                cacheSize.setText(getString(R.string.cache_size) + " " + cacheSizeConverter());
            }
        });

        downloadQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedIndex = sp.getInt(KEY_DOWNLOAD_QUALITY, 2);
                ItemPickerDialogFragment dialogFragment = ItemPickerDialogFragment.newInstance(
                                                    getString(R.string.download_quality),
                                                    QUALITIES,
                                                   selectedIndex);
                dialogFragment.show(getSupportFragmentManager(), ItemPickerDialogFragment.TAG);
            }
        });
    }



    private long getCacheSize() {
        long size = 0;
        File[] files = getCacheDir().listFiles();
        for (File f : files) {
            size += f.length();
        }
        return size;
    }

    private String cacheSizeConverter() {
        long size = getCacheSize();
        Log.i("Cache", size + "");
        if (size > 0 && size < 1024) {
            return size + "B";
        } else if (size >= 1024 && size < 1024 * 1000) {
            return size / 1024 + "KB";
        } else if (size >= 1024 * 1000 && size < (long)(1024 ^ 3)) {
            return size / (1024 ^ 2) + "MB";
        }
        return "0";
    }

    public static void deleteCache(Context context) {
        File dir = context.getCacheDir();
        deleteDir(dir);
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onItemSelected(ItemPickerDialogFragment fragment, String item, int index) {
        Toast.makeText(this, item + " at position " + index + " is selected.", Toast.LENGTH_LONG).show();
        sp.edit().putInt(KEY_DOWNLOAD_QUALITY, index).apply();
    }
}

