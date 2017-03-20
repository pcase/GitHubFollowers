package com.azurehorsecreations.githubfollowers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserNameActivity extends AppCompatActivity implements CallbackReceiver {
    public static final String SERVER_URL = "https://api.github.com/users/";
    public static final String USER = "USER";
    private User user;
    private EditText userNameText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_user_name);
        userNameText = (EditText) findViewById(R.id.username);
        final Button goButton = (Button) findViewById(R.id.go_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UserInfoFetcher fetcher = new UserInfoFetcher(UserNameActivity.this, getUserUrl(), false);
                fetcher.execute();
            }
        });
    }
    public void handleResultData(Object object) {
        final User user = (User) object;
        runOnUiThread(new Runnable() {
            public void run() {
                startFollowerPageActivity(user);
            }
        });
    }

    private String getUserUrl() {
        return SERVER_URL + userNameText.getText().toString().trim();
    }

    private void startFollowerPageActivity(User user) {
        Intent intent = new Intent(this, FollowerPageActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
    }
}
