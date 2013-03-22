package com.hurray.landlord.sdk;

import java.util.Vector;

import com.hurray.landlord.game.online.OnlineMessageAgent;
import com.hurray.lordserver.protocol.message.pay.PayOrangeHandleReq;
import com.hurray.lordserver.protocol.message.pay.PayOrangeHandleResp;
import com.juzi.main.AppConnect;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;

public class SdkManagerJuZi {
	public final static boolean power = true;
	private static SdkManagerJuZi instance = null;
	private final int sleepTime = 60 * 1000;
	private final int minMoney = 125;
	// 广告墙监听
	private View.OnClickListener offersOnClickListener = null;
	private Context context = null;
	private OnlineMessageAgent sendActivity = null;
	private Thread thread = null;
	private boolean isRun = false;
	private SdkManagerJuZi(){}
	
	public static SdkManagerJuZi getInstance(){
		if(instance == null){
			instance = new SdkManagerJuZi();
		}
		return instance;
	}
	
	public void setContext(Context context){
		this.context = context;
		AppConnect.getInstance(context);
	}
	
	public void setSendListener(OnlineMessageAgent sendActivity){
		this.sendActivity = sendActivity;
	}
	
	public View.OnClickListener getOffersOnClickListener(){
		if(this.offersOnClickListener == null && this.context != null){
			this.offersOnClickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 广告墙
					AppConnect.getInstance(context).showOffers(context);
				}
			};
		}
		return this.offersOnClickListener;
	}
	
	public void startThread(){
		if(this.thread == null){
			this.thread = new Thread(){
				public void run(){
					isRun = true;
					while(isRun){
	    				checkJuZiStart(context);
	    				try {
							Thread.sleep(sleepTime);
						} catch (Exception e) {
							e.printStackTrace();
						}
	    			}
				}
			};
			thread.start();
		}
	}
	
	private void checkJuZiStart(Context context){
		try {
			String points = AppConnect.getInstance(context).getPoints();
			if(!points.split(":")[0].equalsIgnoreCase("Error")){
    			int money = Integer.valueOf(points);
    			if(money > minMoney){
    				addMoneyToServ(money);
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
			onDestroy();
			isRun = false;
			return;
		}
    }
	/**
	 * 告诉服务器添加金币
	 */
	private void addMoneyToServ(int orangeMoney){
		if(this.sendActivity != null){
			PayOrangeHandleReq req = new PayOrangeHandleReq();
			req.setOrangeMoney(orangeMoney);
			this.sendActivity.doSend(req);
		}
	}
	
	public void callBack(PayOrangeHandleResp resp){
		if(resp.isSucceeded()){
			if(resp.getType() == 0 && resp.getOrangeMoney() > 0){
				// 消费橘子币
				AppConnect.getInstance(context).spendPoints(resp.getOrangeMoney());
			}
		}
	}
	
	public void onDestroy(){
		isRun = false;
		thread = null;
		try {
			AppConnect.getInstance(context).finalize();
		} catch (Exception e) {}
		offersOnClickListener = null;
		context = null;
		instance = null;
	}
	
	public SpannableString getSpannable(String str){
		SpannableString msp = null;
		StrBody[] bodys = getStr(str);
		if(bodys != null){
			StringBuffer sbf = new StringBuffer();
			for (int i = 0; i < bodys.length; ++i) {
				sbf.append(bodys[i].str);
			}
			msp = new SpannableString(sbf);
			
			int length = 0;
			for (int i = 0; i < bodys.length; ++i) {
				if (bodys[i].type == 1) {
					msp.setSpan(
							new ForegroundColorSpan(Color.rgb(0xde,
									0x5a, 0xad)), length, length
									+ bodys[i].str.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色
					// msp.setSpan(new
					// StyleSpan(android.graphics.Typeface.BOLD),
					// length, length + bodys[i].str.length(),
					// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 粗体
					msp.setSpan(new RelativeSizeSpan(1.2f), length,
							length + bodys[i].str.length(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				length += bodys[i].str.length();
			}
		}
		return msp;
	}
	
	public StrBody[] getStr(String str) {
		str = str.replaceAll("［", "[");
		str = str.replaceAll("【", "[");
		str = str.replaceAll("］", "]");
		str = str.replaceAll("】", "]");
		if (str.contains("[") && str.contains("]")) {
			Vector<StrBody> bodys = new Vector<StrBody>();
			String[] list = str.split("\\[");
			String[] temp = null;
			bodys.addElement(new StrBody(list[0], (byte) 0));
			for (int i = 1; i < list.length; ++i) {
				if (list[i].contains("]")) {
					temp = list[i].split("\\]");
					if (temp != null && temp.length <= 1) {
						bodys.addElement(new StrBody(temp[0], (byte) 1));
					} else {
						bodys.addElement(new StrBody(temp[0], (byte) 1));
						for (int j = 1; j < temp.length; ++j) {
							bodys.addElement(new StrBody(temp[j], (byte) 0));
						}
					}
				} else {
					bodys.addElement(new StrBody(list[i], (byte) 0));
				}
			}
			if (bodys.size() > 0) {
				StrBody[] strBodys = new StrBody[bodys.size()];
				for (int i = 0; i < strBodys.length; ++i) {
					strBodys[i] = bodys.elementAt(i);
				}
				bodys.removeAllElements();
				bodys = null;
				return strBodys;
			}
		}
		return null;
	}

	public class StrBody {
		public String str;
		public byte type;

		public StrBody(String str, byte type) {
			this.str = str;
			this.type = type;
		}
	}
}
