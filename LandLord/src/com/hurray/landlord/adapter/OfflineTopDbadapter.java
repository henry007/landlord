
package com.hurray.landlord.adapter;

import com.hurray.landlord.entity.UserInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class OfflineTopDbadapter {

    private static final String KEY_NAME = "name";
    public static final String KEY_SCORE = "score";
    private static final String KEY_ROWID = "_id";

    private static final String TAG = "OfflineTopDbadapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
            "CREATE TABLE top (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL, score INTEGER NOT NULL);";

    private static final String DELETE_EXCESS_DATA =
            "DELETE FROM top WHERE '_id' NOT IN " +
                    "(SELECT '_id' FROM top ORDER BY score DESC LIMIT 0,10);";

    private static final String DATABASE_NAME = "landlord_top";
    private static final String DATABASE_TABLE = "top";
    private static final int DATABASE_VERSION = 2;

    private Context mAppCtx;

    public synchronized static int getMinScore(Context ctx) {
        OfflineTopDbadapter offlineTopDbadapter = new OfflineTopDbadapter(ctx);
        offlineTopDbadapter.openReadable();
        int minScore = offlineTopDbadapter.fetchMinScore();
        offlineTopDbadapter.close();
        return minScore;
    }

    public synchronized static ArrayList<UserInfo> getUserInfoList(Context ctx) {
        OfflineTopDbadapter offlineTopDbadapter = new OfflineTopDbadapter(ctx);
        offlineTopDbadapter.openReadable();
        ArrayList<UserInfo> list = offlineTopDbadapter.fetchUserInfoList();
        offlineTopDbadapter.close();
        return list;
    }

    public synchronized static boolean addRecord(Context ctx, String name, int score) {
        OfflineTopDbadapter offlineTopDbadapter = new OfflineTopDbadapter(ctx);
        offlineTopDbadapter.openWriteable();
        long lines = offlineTopDbadapter.createNote(name, score);
        offlineTopDbadapter.close();
        return (lines > 0);
    }

    private OfflineTopDbadapter(Context ctx) {
        this.mAppCtx = ctx.getApplicationContext();
    }

    private OfflineTopDbadapter openWriteable() throws SQLException {
        mDbHelper = new DatabaseHelper(mAppCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    private OfflineTopDbadapter openReadable() throws SQLException {
        mDbHelper = new DatabaseHelper(mAppCtx);
        mDb = mDbHelper.getReadableDatabase();
        return this;
    }

    private void close() {
        mDbHelper.close();
    }

    private long createNote(String name, Integer score) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_SCORE, score);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    private Cursor fetchAllNote() {
        return mDb.query(DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_NAME,
                KEY_SCORE
        }, null, null, null, null, KEY_SCORE + " DESC", "0,10");
    }

    private int fetchMinScore() {
        Cursor cursor = fetchAllNote();
        int minScore = 0;
        if (cursor != null) {
            if (cursor.getCount() < 10) {
                minScore = 0;
            } else {
                cursor.moveToLast();
                minScore = cursor.getInt(cursor.getColumnIndex(OfflineTopDbadapter.KEY_SCORE));
            }

            cursor.close();
        }

        return minScore;
    }

    private ArrayList<UserInfo> fetchUserInfoList() {
        ArrayList<UserInfo> list = new ArrayList<UserInfo>();

        Cursor cursor = fetchAllNote();
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OfflineTopDbadapter.KEY_NAME);
            int scoreIndex = cursor.getColumnIndex(OfflineTopDbadapter.KEY_SCORE);

            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(nameIndex);
                    int score = cursor.getInt(scoreIndex);
                    list.add(new UserInfo(name, score));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;

    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS top");
            onCreate(db);

        }

    }
}
