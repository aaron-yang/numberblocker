package com.example.numberblocker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlockNumberDBOpenHelper extends SQLiteOpenHelper {

	public BlockNumberDBOpenHelper(Context context) {
		super(context, "numberblocker.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		arg0.execSQL("create table blocknumber (_id  integer primary key autoincrement,number varchar(20), mode integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
