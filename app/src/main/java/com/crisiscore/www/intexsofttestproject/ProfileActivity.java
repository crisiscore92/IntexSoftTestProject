package com.crisiscore.www.intexsofttestproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;

class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfileImage;

    private TextView tvProfileName, tvProfileEmail, tvProfileBirthday;

    private FacebookHelperClass facebookHelperClass = new FacebookHelperClass(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle(getResources().getString(R.string.profile));

        initialize();

        networkStateActions();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.profile_activity_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case R.id.log_out:

                if (Utils.isNetworkAvailable(this)){

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.logout_dialog_title));
                    builder.setMessage(getResources().getString(R.string.logout_dialog_message));
                    builder.setPositiveButton(getResources().getString(R.string.logout_dialog_positive)
                            , new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            new FirebaseAuthenticationManager(ProfileActivity.this).logOut();
                            LoginManager.getInstance().logOut();
                            finish();
                            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));

                        }

                    });
                    builder.setNegativeButton(getResources().getString(R.string.logout_dialog_negative)
                            , new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setIcon(R.drawable.ic_alert);
                    builder.create();
                    builder.show();

                } else {

                    Utils.makeToast(this, getResources().getString(R.string.logout_error));

                }

                break;

            default:

                break;

        }

        return true;

    }


    private void initialize(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnViewImages);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, PhotosActivity.class);
                startActivity(intent);

            }

        });

        ivProfileImage = (ImageView) findViewById(R.id.ivProfilePhoto);

        tvProfileName = (TextView) findViewById(R.id.tvProfileName);

        tvProfileEmail = (TextView) findViewById(R.id.tvProfileEmail);

        tvProfileBirthday = (TextView) findViewById(R.id.tvProfileBirthday);

    }

    private void getUserInfo(){

        facebookHelperClass.userAvatar(ivProfileImage);

        facebookHelperClass.userName(tvProfileName);

        facebookHelperClass.userEmail(tvProfileEmail);

        facebookHelperClass.userBirthday(tvProfileBirthday);

    }

    private void networkStateActions(){

        if (Utils.isNetworkAvailable(this)){

            getUserInfo();

        } else {

            SharedPreferences preferences = getSharedPreferences("config", MODE_PRIVATE);
            tvProfileName.setTextColor(Color.BLACK);
            tvProfileName.setText(preferences.getString("username", ""));
            tvProfileBirthday.append("\n" + preferences.getString("birthday", ""));
            tvProfileEmail.append("\n" + preferences.getString("email", ""));

        }

    }

}
