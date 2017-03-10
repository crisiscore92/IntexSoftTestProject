package com.crisiscore.www.intexsofttestproject;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class FirebaseAuthenticationManager {

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private Activity activity;

    FirebaseAuthenticationManager(Activity activity) {

        this.activity = activity;

        initialize();

    }

    private void initialize(){

        mAuth = FirebaseAuth.getInstance();

    }

    void checkIfLoggedIn(){

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    Intent i = new Intent(
                            FirebaseAuthenticationManager.this.activity,
                            ProfileActivity.class
                    );

                    FirebaseAuthenticationManager.this.activity.startActivity(i);

                    FirebaseAuthenticationManager.this.activity.finish();

                }

            }

        };

    }

    void addListener(){

        mAuth.addAuthStateListener(mAuthListener);

    }

    void removeListener(){

        if (mAuthListener != null) {

            mAuth.removeAuthStateListener(mAuthListener);

        }

    }

    void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth
                .signInWithCredential(credential)
                .addOnCompleteListener(FirebaseAuthenticationManager.this.activity,
                        new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            Utils.makeToast(
                                    FirebaseAuthenticationManager.this.activity,
                                    activity.getResources().getString(R.string.auth_failed)
                            );

                        }

                    }

                });

    }

    void logOut(){

        mAuth.signOut();

    }

}
