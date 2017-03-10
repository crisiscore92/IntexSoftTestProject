package com.crisiscore.www.intexsofttestproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class FacebookHelperClass extends GetProfileInfoClass {

    private Context context;

    private ImageView ivUserAvatar;

    private TextView tvUserName, tvUserEmail, tvUserBirthday;

    private SharedPreferences preferences;

    FacebookHelperClass(Context context) {
        this.context = context;
    }

    private void setIvUserAvatar(ImageView ivUserAvatar) {
        this.ivUserAvatar = ivUserAvatar;
    }

    private void setTvUserName(TextView tvUserName) {
        this.tvUserName = tvUserName;
    }

    private void setTvUserEmail(TextView tvUserEmail) {
        this.tvUserEmail = tvUserEmail;
    }

    private void setTvUserBirthday(TextView tvUserBirthday) {
        this.tvUserBirthday = tvUserBirthday;
    }

    private ImageView getIvUserAvatar() {
        return ivUserAvatar;
    }

    private TextView getTvUserName() {
        return tvUserName;
    }

    private TextView getTvUserEmail() {
        return tvUserEmail;
    }

    private TextView getTvUserBirthday() {
        return tvUserBirthday;
    }

    @Override
    public void userAvatar(ImageView ivUserAvatar) {

        setIvUserAvatar(ivUserAvatar);

        Bundle params = new Bundle();
        params.putString("fields", "picture.type(large)");

        GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),
                "me",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {

                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                        if (graphResponse != null) {

                            try {

                                JSONObject data = graphResponse.getJSONObject();

                                if (data.has("picture")) {

                                    Glide
                                            .with(context)
                                            .load(
                                                    data
                                                    .getJSONObject("picture")
                                                    .getJSONObject("data")
                                                    .getString("url")
                                            )
                                            .into(getIvUserAvatar());

                                }

                            } catch (Exception e) {

                                e.printStackTrace();

                            }

                        }

                    }

                });

        graphRequest.executeAsync();

    }

    @Override
    public void userName(TextView tvUserName) {

        setTvUserName(tvUserName);

        Bundle params = new Bundle();
        params.putString("fields", "name");

        GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),
                "me",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {

                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                        if (graphResponse != null) {

                            try {

                                JSONObject data = graphResponse.getJSONObject();

                                String profileName = data.getString("name");

                                getTvUserName().setText(profileName);

                                preferences = context
                                        .getSharedPreferences("config", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("username", getTvUserName().getText().toString());
                                editor.putString("isAuthorized", "true");
                                editor.apply();

                            } catch (Exception e) {

                                e.printStackTrace();

                            }

                        }

                    }

                });

        graphRequest.executeAsync();

    }

    @Override
    public void userEmail(TextView tvUserEmail) {

        setTvUserEmail(tvUserEmail);

        Bundle params = new Bundle();
        params.putString("fields", "email");

        GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),
                "me",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {

                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                        if (graphResponse != null) {

                            try {

                                JSONObject data = graphResponse.getJSONObject();

                                String profileEmail = data.getString("email");

                                getTvUserEmail().append("\n" + profileEmail);

                                preferences = context
                                        .getSharedPreferences("config", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("email", profileEmail);
                                editor.apply();

                            } catch (Exception e) {

                                e.printStackTrace();

                            }

                        }

                    }

                });

        graphRequest.executeAsync();

    }

    @Override
    public void userBirthday(TextView tvUserBirthday) {

        setTvUserBirthday(tvUserBirthday);

        Bundle params = new Bundle();
        params.putString("fields", "birthday");

        GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),
                "me",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {

                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                        if (graphResponse != null) {

                            try {

                                JSONObject data = graphResponse.getJSONObject();

                                String profileBirthday= data.getString("birthday");

                                SimpleDateFormat sdf = new SimpleDateFormat(
                                        "MM/dd/yyyy",
                                        Locale.ENGLISH
                                );

                                Date date = sdf.parse(profileBirthday);

                                SimpleDateFormat newsdf = new SimpleDateFormat(
                                        "dd MMM yyyy",
                                        Locale.ENGLISH
                                );

                                String formattedProfileBirthday = newsdf.format(date);

                                getTvUserBirthday().append("\n" + formattedProfileBirthday);

                                preferences = context
                                        .getSharedPreferences("config", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("birthday", formattedProfileBirthday);
                                editor.apply();

                            } catch (Exception e) {

                                e.printStackTrace();

                            }

                        }

                    }

                });

        graphRequest.executeAsync();

    }

}
