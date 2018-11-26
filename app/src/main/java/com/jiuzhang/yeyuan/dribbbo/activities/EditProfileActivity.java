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
import com.jiuzhang.yeyuan.dribbbo.custom_class.ClearableEditText;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.utils.ImageUtils;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

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
//    @BindView(R.id.first_name) EditText firstName;
//    @BindView(R.id.delete_first_name) Button deleteFirstName;
//    @BindView(R.id.last_name) EditText lastName;
//    @BindView(R.id.delete_last_name) Button deleteLastName;
//    @BindView(R.id.username) EditText username;
//    @BindView(R.id.delete_username) Button deleteUsername;
//    @BindView(R.id.email) EditText email;
//    @BindView(R.id.delete_email) Button deleteEmail;
//    @BindView(R.id.portfolio_url) EditText portfolio_url;
//    @BindView(R.id.delete_portfolio) Button deletePortfolio;
//    @BindView(R.id.location) EditText location;
//    @BindView(R.id.delete_location) Button deleteLocation;
//    @BindView(R.id.bio) EditText bio;
//    @BindView(R.id.delete_bio) Button deleteBio;
//    @BindView(R.id.instagram) EditText instagram;
//    @BindView(R.id.delete_instagram) Button deleteInstagram;


    User user;
//    Map<EditText, Button> mMap = new HashMap<>();
//    EditText[] editList = {firstName, lastName, username, email, portfolio_url, location, bio, instagram};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        setTitle(getString(R.string.edit_profile));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = ModelUtils.toObject(getIntent().getStringExtra(KEY_CURRENT_USER), USER_TYPE);
//        mMap.put(firstName, deleteFirstName);
//        mMap.put(lastName, deleteLastName);
//        mMap.put(username, deleteUsername);
//        mMap.put(email, deleteEmail);
//        mMap.put(portfolio_url, deletePortfolio);
//        mMap.put(location, deleteLocation);
//        mMap.put(bio, deleteBio);
//        mMap.put(instagram, deleteInstagram);

        setupContentView(user);

//        for (final EditText editText : editList) {
////            final Button button = mMap.get(editText);
//            editText.setOnTouchListener(this);
////            editText.setOnTouchListener(new View.OnTouchListener() {
////                @Override
////                public boolean onTouch(View view, MotionEvent motionEvent) {
////                    button.setVisibility(View.VISIBLE);
////                    button.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View view) {
////                            editText.setText("");
////                        }
////                    });
////                    return true;
////                }
////            });
//        }
    }

//    @Override
//    public void onFocusChange(View view, boolean b) {
//
//    }
//
//    @Override
//    public boolean onTouch(final View view, MotionEvent motionEvent) {
//        if (view instanceof EditText) {
//            final EditText editTextView = (EditText)view;
//            editTextView.setOnFocusChangeListener(this);
//            Button button = mMap.get(editTextView);
//            button.setVisibility(View.VISIBLE);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    editTextView.setText("");
//                }
//            });
//        }
//        return false;
//    }

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
        Intent intent = new Intent();
        intent.putExtra(KEY_CURRENT_USER, ModelUtils.toString(user, USER_TYPE));
        setResult(RESULT_OK, intent);
        finish();
    }


}
