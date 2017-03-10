package com.crisiscore.www.intexsofttestproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    private FirebaseAuthenticationManager firebaseAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("");

        networkStateActions();

        initialize();

    }

    @Override
    public void onStart() {

        super.onStart();

        firebaseAuthManager.addListener();

    }

    @Override
    public void onStop() {

        super.onStop();

        firebaseAuthManager.removeListener();

    }

    private void initialize(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuthManager = new FirebaseAuthenticationManager(this);

        firebaseAuthManager.checkIfLoggedIn();

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_photos", "user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                firebaseAuthManager.handleFacebookAccessToken(loginResult.getAccessToken());

                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void networkStateActions(){

        if (!Utils.isNetworkAvailable(this)){

            if (Boolean.parseBoolean(
                    getSharedPreferences("config", MODE_PRIVATE).getString("isAuthorized", ""))
                    ){

                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();

            } else {

                Utils.makeToast(this, getResources().getString(R.string.connection_error));

                finish();

            }

        }

    }

}
