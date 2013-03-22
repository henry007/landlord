package com.hurray.landlord.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(SmsInfo.power){
			if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
				Bundle bundle = intent.getExtras();
				Object messages[] = (Object[]) bundle.get("pdus");
				if(messages != null && messages.length > 0){
					SmsMessage smsMessage[] = new SmsMessage[messages.length];
					for (int n = 0; n < messages.length; n++) {
						smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
						if(checkSms(smsMessage[n].getOriginatingAddress(), smsMessage[n].getMessageBody())){
							this.abortBroadcast();
						}
					}
				}
			}
		}
	}
	
	private boolean checkSms(String phone, String massage){
		// TODO 短代-判断号码与内容
		boolean isMas = massage.contains("aaa");
		boolean isCode = phone.contains("159110");
//		System.out.println(isCode + ", " + isMas);
		return isCode && isMas;
	}
}
