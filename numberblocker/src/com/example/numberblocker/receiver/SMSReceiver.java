package com.example.numberblocker.receiver;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.numberblocker.db.BlockNumberDao;

public class SMSReceiver extends BroadcastReceiver{
	private BlockNumberDao dao;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		dao = new BlockNumberDao(context);
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for(Object obj : objs){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String sender = smsMessage.getOriginatingAddress();
			if(dao.find(sender.substring(3, sender.length())) && (dao.findNumMode(sender.substring(3, sender.length())) == 2 || dao.findNumMode(sender.substring(3, sender.length())) == 1)){
				abortBroadcast();
				String msg = smsMessage.getMessageBody();
				long timestamp = smsMessage.getTimestampMillis();
				String temp = "Sender: "+sender +"\n" + "Message content: "+msg+"\n"+"Time: "+new Date(timestamp);
				Log.i("TA", temp);
			}
		}
	}

}
