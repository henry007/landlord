package com.hurray.landlord.sms;

import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;

import android.telephony.SmsManager;

public class SmsInfo {
	public final static boolean power = false;
	private static SmsInfo instance = null;
	// private static String[] businessName = {"极品在线", "星天下"};
	private static String[] businessCode = { "1", "294" };
	private static String[] businessNumber = { "1066578668", "106699776"};

	public static SmsInfo getInstance() {
		if (instance == null) {
			instance = new SmsInfo();
		}
		return instance;
	}

	public void sendSms(String destPhone, String message) {
		try {
			SmsManager sms = SmsManager.getDefault();
			AccountPreferrence apf = AccountPreferrence.getSingleton();
	        message = message + "|" + apf.getUserId((long)0) + "|" + apf.getNickName("");
	        System.out.println(message);
	        sms.sendTextMessage(destPhone, null, message, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void sendSms(int index) {
		if (index < businessNumber.length) {
			sendSms(businessNumber[index], businessCode[index]);
		}
	}

}
