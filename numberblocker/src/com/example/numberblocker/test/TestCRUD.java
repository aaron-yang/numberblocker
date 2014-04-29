package com.example.numberblocker.test;

import android.test.AndroidTestCase;

import com.example.numberblocker.db.BlockNumberDao;

public class TestCRUD extends AndroidTestCase{
	
	public void testAdd(){
		BlockNumberDao dao = new BlockNumberDao(getContext());
		assertFalse(dao.find("13559494123"));
	    dao.add("13559494123", "0");
	    assertTrue(dao.find("13559494123"));
	}
	
}
