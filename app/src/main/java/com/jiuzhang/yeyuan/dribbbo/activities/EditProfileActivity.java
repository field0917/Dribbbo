package com.jiuzhang.yeyuan.dribbbo.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.base.WendoTask;
import com.jiuzhang.yeyuan.dribbbo.custom_class.ClearableEditText;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.utils.ImageUtils;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;
import com.jiuzhang.yeyuan.dribbbo.wendo.Wendo;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jiuzhang.yeyuan.dribbbo.activities.MainActivity.KEY_CURRENT_USER;
import static com.jiuzhang.yeyuan.dribbbo.wendo.Wendo.USER_TYPE;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.user_image) ImageView userImage;
    @BindView(R.id.change_img) TextView changeImageButton;
    @BindView(R.id.first_name) ClearableEditText firstName;
    @BindView(R.id.last_name) ClearableEditText lastName;
    @BindView(R.id.username) ClearableEditText username;
    @BindView(R.id.email) ClearableEditText email;
    @BindView(R.id.portfolio_url) ClearableEditText portfolio_url;
    @BindView(R.id.location) ClearableEditText location;
    @BindView(R.id.bio) ClearableEditText bio;
    @BindView(R.id.instagram) ClearableEditText instagram;

    User user;
    String[] info = new String[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        setTitle(getString(R.string.edit_profile));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = ModelUtils.toObject(getIntent().getStringExtra(KEY_CURRENT_USER), USER_TYPE);

        setupContentView(user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_bucket_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
                save();
                return true;
            default:
                return false;
        }
    }

    public void setupContentView(User user) {
        ImageUtils.loadCircleUserImage(this, user.getProfileImageURL(), userImage);
        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditProfileActivity.this, "change photo", Toast.LENGTH_SHORT).show();
            }
        });
        firstName.setText(user.first_name);
        lastName.setText(user.last_name);
        username.setText(user.username);
        email.setText(user.email);
        portfolio_url.setText(user.portfolio_url);
        location.setText(user.location);
        bio.setText(user.bio);
        instagram.setText(user.instagram_username);

    }

    private void save() {
        info[0] = firstName.getText().toString();
        info[1] = lastName.getText().toString();
        info[2] = username.getText().toString();
        info[3] = email.getText().toString();
        info[4] = portfolio_url.getText().toString();
        info[5] = location.getText().toString();
        info[6] = bio.getText().toString();
        info[7] = instagram.getText().toString();

        UpdateCurrentUserTask task = new UpdateCurrentUserTask();
        task.execute();
    }

    private class UpdateCurrentUserTask extends WendoTask<Void, Void, User> {

        @Override
        public User doOnNewThread(Void... voids) throws Exception {
            user = Wendo.updateCurrentUserProfile(info);
            return user;
        }

        @Override
        public void onSuccess(User user) {
//            Wendo.storeUser();
            Intent intent = new Intent();
            intent.putExtra(KEY_CURRENT_USER, ModelUtils.toString(user, USER_TYPE));
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onFailed(Exception e) {
            Toast.makeText(EditProfileActivity.this, "Something went wrong when you update user profile", Toast.LENGTH_LONG).show();
        }
    }

}
