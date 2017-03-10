package com.crisiscore.www.intexsofttestproject;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.squareup.picasso.Picasso;

class PhotosAdapter {

    private Activity context;

    PhotosAdapter(Activity context) {

        this.context = context;
    }

    SimpleCursorAdapter createAdapter(){

        String[] from = new String[] {
                PhotosDB.COLUMN_PHOTO_URI,
                PhotosDB.COLUMN_DATE};

        int[] to = new int[] {
                R.id.ivPhotoListItem,
                R.id.tvPublishDate};

        SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(
                context,
                R.layout.photos_list_item,
                null,
                from,
                to,
                0);

        scAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(final View view, final Cursor cursor, int i) {

                if (view.getId() == R.id.tvPublishDate){

                    String dateStr =
                            cursor.getString(cursor.getColumnIndex("date")).split(",")[0].trim()
                                    + "\n"
                                    + cursor.getString(cursor.getColumnIndex("date")).split(",")[1].trim();

                    TextView date = (TextView) view;

                    date.setText(dateStr);

                    return true;

                }

                if (view.getId() == R.id.ivPhotoListItem){

                    Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex("uri")));

                    ImageView imageView = (ImageView) view;

                    if (Utils.isNetworkAvailable(context)){

                        Glide
                                .with(imageView.getContext())
                                .using(new FirebaseImageLoader())
                                .load(new FirebaseStorageManager().downloadImage(uri))
                                .into(imageView);

                    } else {

                        Picasso.with(imageView.getContext()).load(uri).into(imageView);

                    }

                    return true;

                }

                return false;

            }

        });

        return scAdapter;

    }

}
