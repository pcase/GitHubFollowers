package com.azurehorsecreations.githubfollowers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

public class UserInfoFetcher extends AsyncTask<Void, Void, User>  {
        private static final String TAG = "UserInfoFetcher";
        private Context mContext;
        private ProgressBar progressBar;
        private User mUser;
        private String mUrl;
        private boolean mShowProgressBar;

        public UserInfoFetcher(Context context, String url, boolean showProgressBar) {
            mContext = context;
            mUrl = url;
            mShowProgressBar = showProgressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) ((Activity) mContext).findViewById(R.id.progressbar);
            if (mShowProgressBar) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected User doInBackground(Void... params) {
            try {
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(mUrl);
                HttpResponse response = httpClient.execute(request);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    try {
                        Reader reader = new InputStreamReader(content);
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        User user = gson.fromJson(reader, User.class);
                        content.close();

                        try {
                            InputStream in = new java.net.URL(user.getAvatar_url()).openStream();
                            user.setAvatar(BitmapFactory.decodeStream(in));
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        };

                        return user;
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
        protected void onPostExecute(User result) {
            progressBar.setVisibility(View.GONE);
            ((CallbackReceiver)mContext).handleResultData(result);
        }
}
