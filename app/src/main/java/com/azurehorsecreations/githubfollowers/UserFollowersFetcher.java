package com.azurehorsecreations.githubfollowers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class UserFollowersFetcher extends AsyncTask<Void, Void, List<User>>  {
        private static final String TAG = "UserFollowersFetcher";
        private Context mContext;
        private ProgressBar progressBar;
        private String mUrl;

        public UserFollowersFetcher(Context context, String url) {
            mContext = context;
            mUrl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) ((Activity) mContext).findViewById(R.id.progressbar);
            progressBar.setVisibility(View.VISIBLE);
        }

    @Override
    protected List<User> doInBackground(Void... params) {
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
                    content.close();
                    return Arrays.asList(gson.fromJson(reader, User[].class));
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
        protected void onPostExecute(List<User> result) {
            progressBar.setVisibility(View.GONE);
            ((CallbackReceiver)mContext).handleResultData(result);
        }
}
