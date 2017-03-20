package com.azurehorsecreations.githubfollowers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class UserDetailActivity extends AppCompatActivity implements CallbackReceiver {
    public static final String SERVER_URL = "https://api.github.com/users/";
    public static final String USER = "USER";
    User user;
    TextView userName;
    TextView name;
    TextView repoCount;
    TextView followerCount;
    TextView followingCount;
    TextView location;
    TextView email;
    ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_user_detail);
        user = getIntent().getParcelableExtra(USER);
        userName = (TextView) findViewById(R.id.username);
        name = (TextView) findViewById(R.id.name);
        repoCount = (TextView) findViewById(R.id.repo_count);
        followerCount = (TextView) findViewById(R.id.follower_count);
        followingCount = (TextView) findViewById(R.id.following_count);
        location = (TextView) findViewById(R.id.location);
        email = (TextView) findViewById(R.id.email);
        avatar = (ImageView) findViewById(R.id.avatar);
        UserInfoFetcher fetcher = new UserInfoFetcher(this, getUserUrl(), true);
        fetcher.execute();
    }

    public void handleResultData(Object object) {
        final User user = (User) object;
        runOnUiThread(new Runnable() {
            public void run() {
                userName.setText(user.getLogin());
                name.setText(user.getName());
                repoCount.setText(String.valueOf(user.getPublic_repos()));
                followerCount.setText(String.valueOf(user.getFollowers()));
                followingCount.setText(String.valueOf(user.getFollowing()));
                location.setText(user.getLocation());
                email.setText(user.getEmail());
                avatar.setImageBitmap(user.getAvatar());
            }
        });
    }

    private String getUserUrl() {
        return SERVER_URL + user.getLogin();
    }
}
