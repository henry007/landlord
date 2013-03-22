package com.hurray.landlord.sdk;

import com.hurray.landlord.activities.OnlineHomeActivity;
import com.hurray.lordserver.protocol.message.announcement.AnnouncementReq;

public class NoticeSend {
	private static NoticeSend instance = null;
	public boolean isSendNotice;
	
	private NoticeSend(){
		isSendNotice = false;
	}
	
	public static NoticeSend getInstance(){
		if(instance == null){
			instance = new NoticeSend();
		}
		return instance;
	}
	
	public void reset(){
		isSendNotice = false;
	}
	
	public void sendNotice(final OnlineHomeActivity onlineHome){
		if (!isSendNotice) {
			new Thread(){
				public void run(){
					try {
						Thread.sleep(500);
					} catch (Exception e) {
						e.printStackTrace();
					}
					onlineHome.doSend(new AnnouncementReq());
					isSendNotice = true;
				}
			}.start();
		}
	}
}
