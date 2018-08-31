package com.jiuzhang.yeyuan.dribbbo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    public static final int REQ_CODE = 100;
    public static final String KEY_URL = "auth URL";

    @BindView(R.id.login_button) Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                String url = "https://dribbble.com/oauth/authorize?client_id=401963981b138f18f81c37d543f3f9f716d247c6fba156e063e6756dd2d20cbe&scope=public+write";
                intent.putExtra(KEY_URL, url);
                startActivityForResult(intent, REQ_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
            String code = data.getStringExtra(AuthActivity.KEY_CODE);
            Toast.makeText(this, code, Toast.LENGTH_LONG).show();
        }

    }
}

// client_id=401963981b138f18f81c37d543f3f9f716d247c6fba156e063e6756dd2d20cbe
// client secret: 20583200f57f721db73aa082fccb2b56befe1162b7e18225727f049f6b228266
// code=0f9dd9975c3a93522566b394347ff92269ec932ea0a18e6badd84bcce09a6efd

//https://dribbble.com/oauth/authorize?client_id=401963981b138f18f81c37d543f3f9f716d247c6fba156e063e6756dd2d20cbe&scope=public+write