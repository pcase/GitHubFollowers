package com.azurehorsecreations.githubfollowers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class FollowerPageActivity extends AppCompatActivity implements GridAdapter.ItemClickListener, CallbackReceiver {
    public static final String SERVER_URL = "https://api.github.com/users/";
    public static final String USER = "USER";
    GridAdapter adapter;
    User user;
    List<User> followers = new ArrayList<>();
    RecyclerView recyclerView;
    private TextView emptyView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_page);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        user = getIntent().getParcelableExtra(USER);
        recyclerView = (RecyclerView) findViewById(R.id.github_recycler_view);
        emptyView = (TextView) findViewById(R.id.empty_view);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        if (user == null) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            UserFollowerFetcher fetcher = new UserFollowerFetcher(user);
            fetcher.execute();
        }
    }

    public void handleResultData(Object object) {
        final List<User> followers = (List<User>) object;
        adapter = new GridAdapter(FollowerPageActivity.this, followers);
        adapter.setClickListener(FollowerPageActivity.this);
        recyclerView.setAdapter(adapter);
        runOnUiThread(new Runnable() {
            public void run() {
                if ((followers == null) || (followers != null && followers.size() == 0)) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getUserFollowers() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (followers != null && followers.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
                adapter = new GridAdapter(FollowerPageActivity.this, followers);
                adapter.setClickListener(FollowerPageActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(USER, user);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        user = savedInstanceState.getParcelable(USER);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra(USER, adapter.getItem(position));
        startActivity(intent);
    }

    private String getUserFollowersUrl(User user) {
        return SERVER_URL + user.getLogin() + "/followers";
    }

    private class UserFollowerFetcher extends AsyncTask<Void, Void, String> {
        private static final String TAG = "UserFollowerFetcher";
        private User mUser;

        public UserFollowerFetcher(User user) {
           mUser = user;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(getUserFollowersUrl(mUser));
                HttpResponse response = httpClient.execute(request);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    try {
                        Reader reader = new InputStreamReader(content);
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        followers = Arrays.asList(gson.fromJson(reader, User[].class));
                        content.close();
                        getUserFollowers();
                    } catch (Exception ex) {
                        Log.e(TAG, "Failed to parse JSON due to: " + ex);
                    }
                } else {
                    Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
                }
            } catch(Exception ex) {
                Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
