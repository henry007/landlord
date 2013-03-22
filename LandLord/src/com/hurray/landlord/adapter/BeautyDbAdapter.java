package com.hurray.landlord.adapter;

import com.hurray.landlord.entity.BeautyStatus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BeautyDbAdapter {

	public static final String KEY_NAME = "name";
	public static final String KEY_STATUS = "status";
	public static final String KEY_ROWID = "_id";
	
	private static final String TAG = "BeautyDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static final String DATABASE_CREATE = 
			"CREATE TABLE beauty (_id INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ "name TEXT NOT NULL UNIQUE, status TEXT NOT NULL);";
	
	private static final String DATABASE_NAME = "landlord_beauty";
	private static final String DATABASE_TABLE = "beauty";
	private static final int DATABASE_VERSION = 2;
	
	private final Context mCtx;
	
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
            db.execSQL("DROP TABLE IF EXISTS beauty");
            onCreate(db);
			
		}
		
	}
	
	public BeautyDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}
	
	public BeautyDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	public long createBeauty(String name, BeautyStatus status) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_STATUS, status.toString());
		
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public Cursor fetchAllBeauty() {
		return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, 
				KEY_STATUS}, null, null, null, null, null, null);
	}
	
	public Cursor fetchBeauty(String name) throws SQLException {
		Cursor mCursor =
				mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
						KEY_NAME, KEY_STATUS}, KEY_NAME + "=?" , new String[] {name}, 
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public boolean updateBeauty(String name, BeautyStatus status) {
		ContentValues args = new ContentValues();
		args.put(KEY_STATUS, status.toString());

		return mDb.update(DATABASE_TABLE, args, KEY_NAME + "=?", new String[] {name}) > 0;
	}
	
}
