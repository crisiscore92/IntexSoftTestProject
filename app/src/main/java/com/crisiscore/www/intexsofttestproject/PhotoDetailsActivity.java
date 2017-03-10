package com.crisiscore.www.intexsofttestproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.squareup.picasso.Picasso;

class PhotoDetailsActivity extends AppCompatActivity {

    private ImageView ivPhotoDetails;

    private long id;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details);

        setTitle(getResources().getString(R.string.photo_details));

        initialize();

        setUpPhoto();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.photo_details_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.delete:

                if (Utils.isNetworkAvailable(this)){

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.delete_dialog_title));
                    builder.setMessage(getResources().getString(R.string.delete_dialog_message));
                    builder.setPositiveButton(getResources().getString(R.string.delete_dialog_positive)
                            , new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            PhotosDB db = new PhotosDB(PhotoDetailsActivity.this);
                            db.open();
                            db.delRec(PhotoDetailsActivity.this.id);
                            db.close();
                            new FirebaseStorageManager().deleteImage(uri);
                            Utils.makeToast(PhotoDetailsActivity.this, "Photo was deleted!");
                            finish();

                        }

                    });
                    builder.setNegativeButton(getResources().getString(R.string.delete_dialog_negative)
                            , new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setIcon(R.drawable.ic_alert);
                    builder.create();
                    builder.show();

                } else {

                    Utils.makeToast(this, getResources().getString(R.string.delete_error));

                }

                break;
        }

        return true;

    }

    private void setUpPhoto(){

        Intent intent = getIntent();

        if (intent != null){

            uri = Uri.parse(intent.getStringExtra("photoUri"));

            id = intent.getLongExtra("id", 0);

            if (Utils.isNetworkAvailable(this)){

                Glide
                        .with(ivPhotoDetails.getContext())
                        .using(new FirebaseImageLoader())
                        .load(new FirebaseStorageManager().downloadImage(uri))
                        .into(ivPhotoDetails);

            } else {

                Picasso.with(ivPhotoDetails.getContext()).load(uri).into(ivPhotoDetails);

            }

        }

    }

    private void initialize(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                finish();

            }

        });

        ivPhotoDetails = (ImageView) findViewById(R.id.ivPhotoDetails);

    }

}
