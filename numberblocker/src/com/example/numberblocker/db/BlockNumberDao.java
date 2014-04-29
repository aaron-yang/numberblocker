package com.example.numberblocker.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.numberblocker.entity.BlockNumber;

public class BlockNumberDao {
	
	private BlockNumberDBOpenHelper helper;
	
	public BlockNumberDao(Context context){
		helper = new BlockNumberDBOpenHelper(context);
	}
	
	public boolean find(String number){
		boolean result = false;
		SQLiteDatabase db = helper.getWritableDatabase();
		if(db.isOpen()){
			Cursor cursor = db.rawQuery("select * from blocknumber where number =?", new String[]{number});
			if(cursor.moveToFirst()){
				result = true;
			}
			cursor.close();
			db.close();
		}
		return result;
	} 
	
	public int findNumMode(String number){
		// 拦截模式有3中 0
		//0拦截短信  1 拦截电话   2 拦截两个    -1没有标记拦截
		int mode = -1;
		SQLiteDatabase db = helper.getWritableDatabase();
		if(db.isOpen()){
			Cursor cursor = db.rawQuery("select mode from blocknumber where number =?", new String[]{number});
			if(cursor.moveToNext()){
				mode = cursor.getInt(0);
			}
			cursor.close();
			db.close();
		}
		return mode;
	}
	
	public boolean add(String number,String mode){
		if(find(number)){
			return false;
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("insert into blocknumber (number,mode) values(?,?)", new Object[]{number,mode});
			db.close();
		}
		return find(number);
	}
	
	public void delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("delete blocknumber where number= ?", new String[]{number});
			db.close();
		}
	}
	
	public void update(String oldnumber,String newnumber,String mode){
		SQLiteDatabase db = helper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("update blocknumber set number=?,mode=? where number= ?", new Object[]{newnumber,mode,oldnumber});
			db.close();
		}
	}
	
	public List<BlockNumber> findAll(){
		List<BlockNumber> numbers = new ArrayList<BlockNumber>();
		SQLiteDatabase db = helper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cursor = db.rawQuery("select number,mode from blocknumber",null);
			while(cursor.moveToNext()){
				BlockNumber number = new BlockNumber();
				number.setBlocknumber(cursor.getString(0));
				number.setMode(cursor.getInt(1));
				numbers.add(number);
			}
			cursor.close();
			db.close();
		}
		return numbers;
	}

}
