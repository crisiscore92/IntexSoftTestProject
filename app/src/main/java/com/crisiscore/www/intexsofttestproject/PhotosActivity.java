package com.crisiscore.www.intexsofttestproject;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

class PhotosActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PICK_PHOTO_REQUEST = 101;

    private PhotosDB db;

    private SimpleCursorAdapter scAdapter;

    private ListView lvPhotos;

    private TextView tvNoImages;

    private ImageView ivPhotoDetails;

    private boolean firstOpen;

    private Uri photoUri;

    private Uri tabletUri;

    private long id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        setTitle(getResources().getString(R.string.photos));

        initialize();

        setUpListView();

        firstOpen = true;

        db = new PhotosDB(this);
        db.open();

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!firstOpen){

            getLoaderManager().restartLoader(0, null, this);

            if (getResources().getBoolean(R.bool.isTablet)
                    && tabletUri != null
                    && tabletUri.toString().length() > 0){

                setPhotoDetailsFragment();

            }

        }

    }

    @Override
    protected void onPause() {

        super.onPause();

        firstOpen = false;

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        db.close();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_PHOTO_REQUEST){

            if (resultCode == RESULT_OK){

                photoUri = data.getData();

                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle(getResources().getString(R.string.upload_dialog_title));
                dialog.setMessage(getResources().getString(R.string.upload_dialog_message));
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.create();
                dialog.show();

                FirebaseStorageManager firebaseStorageManager = new FirebaseStorageManager();
                firebaseStorageManager.uploadImage(photoUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "dd MMM yyyy, HH:mm",
                                Locale.ENGLISH
                        );
                        String formattedDate = sdf.format(calendar.getTime());

                        if (!haveDuplicate(photoUri)){

                            db.addRec(photoUri.toString(), formattedDate);

                            getLoaderManager().getLoader(0).forceLoad();

                            dialog.dismiss();

                            Utils.makeToast(
                                    PhotosActivity.this,
                                    getResources().getString(R.string.upload_success)
                            );

                        } else {

                            dialog.dismiss();

                        }

                    }

                });

            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (getResources().getBoolean(R.bool.isTablet)){

            MenuInflater inflater = getMenuInflater();

            inflater.inflate(R.menu.photo_details_menu, menu);

            return true;

        }

        return false;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (getResources().getBoolean(R.bool.isTablet) && id >= 0){

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

                                PhotosDB db = new PhotosDB(PhotosActivity.this);
                                db.open();
                                db.delRec(PhotosActivity.this.id);
                                db.close();
                                new FirebaseStorageManager().deleteImage(tabletUri);
                                Utils.makeToast(
                                        PhotosActivity.this,
                                        getResources().getString(R.string.delete_success));

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

        return false;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new MyCursorLoader(this, db);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        scAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    boolean haveDuplicate(Uri uri){

        Cursor cursor = db.getAllPhotos();

        boolean haveDuplicate = false;

        if (cursor.moveToFirst()){

            do {

                String uriStr = cursor.getString(cursor.getColumnIndex("uri"));

                if (uri.toString().equals(uriStr)){

                    haveDuplicate = true;

                    Utils.makeToast(
                            this,
                            getResources().getString(R.string.photo_exists)
                    );

                    break;

                }

            } while (cursor.moveToNext());

        }

        cursor.close();

        return haveDuplicate;

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

        lvPhotos = (ListView) findViewById(R.id.lvPhotos);

        tvNoImages = (TextView) findViewById(R.id.tvNoImages);

        scAdapter = new PhotosAdapter(this).createAdapter();

        ivPhotoDetails = (ImageView) findViewById(R.id.ivPhotoDetails) ;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnNewImage);

        if (Utils.isNetworkAvailable(this)){

            fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, PICK_PHOTO_REQUEST);

                }

            });

        } else {

            fab.setVisibility(View.INVISIBLE);

        }

    }

    private void setUpListView(){

        lvPhotos.setAdapter(scAdapter);
        lvPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Cursor cursor = db.getPhoto(l);

                if (cursor.moveToFirst()){

                    if (getResources().getBoolean(R.bool.isTablet)){

                        tabletUri = Uri.parse(cursor.getString(cursor.getColumnIndex("uri")));

                        id = Long.parseLong(cursor.getString(cursor.getColumnIndex("_id")));

                        setPhotoDetailsFragment();
                        
                    } else {

                        Intent intent = new Intent(PhotosActivity.this, PhotoDetailsActivity.class);
                        intent.putExtra("photoUri", cursor.getString(cursor.getColumnIndex("uri")));
                        intent.putExtra("id", Long.parseLong(cursor.getString(cursor.getColumnIndex("_id"))));
                        startActivity(intent);

                    }

                }

            }

        });

        lvPhotos.setEmptyView(tvNoImages);

    }

    private void setPhotoDetailsFragment(){

        if (Utils.isNetworkAvailable(PhotosActivity.this)){

            Glide
                    .with(ivPhotoDetails.getContext())
                    .using(new FirebaseImageLoader())
                    .load(new FirebaseStorageManager().downloadImage(tabletUri))
                    .into(ivPhotoDetails);

        } else {

            Picasso
                    .with(ivPhotoDetails.getContext())
                    .load(tabletUri)
                    .into(ivPhotoDetails);

        }

    }

    private static class MyCursorLoader extends CursorLoader {

        PhotosDB db;

        MyCursorLoader(Context context, PhotosDB db) {

            super(context);

            this.db = db;

        }

        @Override
        public Cursor loadInBackground() {

            return db.getAllPhotos();

        }

    }

}
