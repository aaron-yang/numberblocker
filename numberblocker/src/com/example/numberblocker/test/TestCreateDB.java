package com.example.numberblocker.test;

import com.example.numberblocker.db.BlockNumberDBOpenHelper;

import android.test.AndroidTestCase;

public class TestCreateDB extends AndroidTestCase {
	
	public void testDBCreataion(){
		BlockNumberDBOpenHelper helper = new BlockNumberDBOpenHelper(getContext());
		assertTrue(helper.getWritableDatabase() != null);
	}

}
