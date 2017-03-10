package com.crisiscore.www.intexsofttestproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class PhotosDB {

    private static final String DB_NAME = "mydb";

    private static final int DB_VERSION = 1;

    private static final String DB_TABLE = "mytab";

    private static final String COLUMN_ID = "_id";

    static final String COLUMN_PHOTO_URI = "uri";

    static final String COLUMN_DATE = "date";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_PHOTO_URI + " text, " +
                    COLUMN_DATE +
                    ");";

    private final Context mCtx;

    private DBHelper mDBHelper;

    private SQLiteDatabase mDB;

    PhotosDB(Context mCtx) {
        this.mCtx = mCtx;
    }

    void open() {

        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);

        mDB = mDBHelper.getWritableDatabase();

    }

    void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    Cursor getAllPhotos(){

        return mDB.query(
                DB_TABLE,
                null,
                null,
                null,
                null,
                null,
                null);

    }

    Cursor getPhoto(long id){

        return mDB.query(
                DB_TABLE,
                null,
                COLUMN_ID + " = " + id,
                null,
                null,
                null,
                null);

    }


    void addRec(String uri, String date) {

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PHOTO_URI, uri);
        cv.put(COLUMN_DATE, date);

        mDB.insert(DB_TABLE, null, cv);

    }

    void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                 int version) {

            super(context, name, factory, version);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

}
