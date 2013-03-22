package com.hurray.landlord.server;

import static com.hurray.landlord.server.ServerConstants.LOG_TAG;

import com.hurray.landlord.R;
import com.hurray.landlord.game.online.CommonMsgIntercepter;
import com.hurray.landlord.sdk.SdkManagerJuZi;
import com.hurray.landlord.server.stub.IMessageListener;
import com.hurray.landlord.server.stub.IMessageServer;
import com.hurray.landlord.server.stub.MessageHolder;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ManifestUtil;
import com.hurray.landlord.utils.StringUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.MessageList;
import com.hurray.lordserver.protocol.message.SysCommonPush;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.base.HeartBeatReq;
import com.hurray.lordserver.protocol.message.error.AuthErrorResp;
import com.hurray.lordserver.protocol.message.pay.PayOrangeHandleResp;
import com.hurray.lordserver.protocol.message.user.LogoutUserResp;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Yizhou He 4/25/12 20:05
 */
public class MessageServerService extends Service implements
		CommonMsgIntercepter {
	private String url;
	public long sleepTime = ServerConstants.DEFAULT_MSG_FREQUENCY;
	// lhx 7-17
	private boolean interceptAutherror = false;
	private String sessionId = null;
	private RemoteCallbackList<IMessageListener> listeners = new RemoteCallbackList<IMessageListener>();
	private String heartBeat = null;
	private LinkedBlockingQueue<MessageHolder> mSendQueue = new LinkedBlockingQueue<MessageHolder>();
	private final IMessageServer.Stub mBinder = new IMessageServer.Stub() {

		@Override
		public void sendMessage(MessageHolder msg) throws RemoteException {
			mSendQueue.offer(msg);
		}

		@Override
		public void addListener(IMessageListener cb) throws RemoteException {
			if (cb != null) {
				listeners.register(cb);
			}
		}

		@Override
		public void removeListener(IMessageListener cb) throws RemoteException {
			if (cb != null) {
				listeners.unregister(cb);
			}
		}

		@Override
		public void setFrequency(long time) throws RemoteException {
			if (time <= 0) {
				throw new RuntimeException("frequency must be larger than 0");
			}
			sleepTime = time;

		}

		@Override
		public void stop() throws RemoteException {
			stopped = true;
		}
	};
	private boolean stopped = false;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		url = ServerConstants.getServerUrl();
		HttpMessageChannel channel = new HttpMessageChannel();
		channel.start();
		LogUtil.i(LOG_TAG, "using: " + url);
		LogUtil.i(LOG_TAG, "start Message Channel thread");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(LOG_TAG, "shutdown JSON server");
		stopped = true;
	}

	private void notifyIncomingMessage(MessageHolder holder) {
		synchronized (mSendQueue) {

			try {
				int N = listeners.beginBroadcast();
				for (int i = 0; i < N; i++) {
					listeners.getBroadcastItem(i).onReceive(holder);
				}
				listeners.finishBroadcast();

			} catch (RemoteException e) {
				LogUtil.e(LOG_TAG, "notify callback failed", e);
			} catch (IllegalStateException e) {
				listeners.finishBroadcast();
			}

		}
	}

	private class HttpMessageChannel extends Thread {

		private DefaultHttpClient mHttpClient;

		private long lastActiveTime = 0;

		public HttpMessageChannel() {
			mHttpClient = new DefaultHttpClient();
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					ServerConstants.CONNECT_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParameters,
					ServerConstants.SO_TIMEOUT);
			mHttpClient.setParams(httpParameters);

			mHttpClient
					.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {
						@Override
						public long getKeepAliveDuration(HttpResponse response,
								HttpContext context) {
							long keepAlive = super.getKeepAliveDuration(
									response, context);
							// Keep connections alive 5 seconds if a keep-alive
							// value

							if (keepAlive == -1) {
								// has not be explicitly set by the server
								keepAlive = 5000;
							}

							return keepAlive;
						}
					});

		}

		@Override
		public void run() {
			synchronized (MessageServerService.this) {

				LogUtil.d(LOG_TAG, "Begin to run polling thread");
				while (!stopped) {
					long current = SystemClock.elapsedRealtime();

					try {
						MessageHolder holder = mSendQueue.poll(getSleepTime(),
								TimeUnit.MILLISECONDS);
						if (holder != null) {
							String content = holder.getJsonContent();
							if (!holder.getType().equals("base.HeartBeatReq")) {
								LogUtil.d(LOG_TAG, "SEND_json " + content);

							} else {

								LogUtil.d(LOG_TAG, "HearBeat111");

							}
							sendMessage(content);
						} else {
							if (heartBeat != null) {
								LogUtil.d(LOG_TAG, "heartBear-->" + heartBeat);
								sendMessage(heartBeat);
							} else {
								LogUtil.d(LOG_TAG,
										"MsgHolder ==null||HearBeat==null");
							}

						}

					} catch (InterruptedException e) {
						Log.w(LOG_TAG, "interrupt Thread");
						stopped = true;

					} catch (IOException e) {
						Log.w(LOG_TAG, "sendQueue error", e);
					}

					lastActiveTime = current;

				}
			}
		}

		private void sendMessage(String content) throws IOException {

			String digest = StringUtil.digestMD5(content + "hurray2012");

			HttpPost httppost = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("msg_list", content));
			nameValuePairs.add(new BasicNameValuePair("digest", digest));
			nameValuePairs.add(new BasicNameValuePair("sessionId", sessionId));

			httppost.setHeader("client_version", ManifestUtil.getVersionName());
			UrlEncodedFormEntity form = new UrlEncodedFormEntity(
					nameValuePairs, "UTF-8");

			httppost.setEntity(form);

			HttpResponse response = mHttpClient.execute(httppost);
			HttpEntity httpEntity = response.getEntity();
			if (httpEntity != null) {
				InputStream is = httpEntity.getContent();
				String result = IOUtils.toString(is);
				try {
					MessageHolder responseHolder = new MessageHolder();
					responseHolder.setJsonContent(result);
					MessageList list = responseHolder.getMessages();

					if (list.size() > 0) {
						sessionId = list.getFirst().getSessionId();
						HeartBeatReq req = new HeartBeatReq();
						req.setSessionId(sessionId);
						heartBeat = new MessageList(req).toJSONString();

						BaseMessage tempMsg = list.getFirst();

						if (!msgIntercepted(tempMsg)) {

							LogUtil.d(LOG_TAG, "RECV_json " + result);
							notifyIncomingMessage(responseHolder);

						} else {

							LogUtil.d(LOG_TAG, "RECV_json is intercepted --->"
									+ result);

						}

						BaseMessage msg = list.getFirst();
						// 接收消息，遇到LogoutUserResp，AuthErrorResp时停止心跳
						if (msg instanceof AuthErrorResp
								|| msg instanceof LogoutUserResp) {
							heartBeat = null;
						}
					}
				} catch (Exception e) {
					LogUtil.d(LOG_TAG, "RECV_json error ", e);
				}

			}
		}
	}

	private long getSleepTime() {
		return sleepTime;
	}

	@Override
	public boolean msgIntercepted(BaseMessage response) {
		if (response == null) {
			LogUtil.w(LOG_TAG, "onPreReceived null BaseMessage");
			return true;
		}

		if (response instanceof AuthErrorResp) {

			AuthErrorResp r = (AuthErrorResp) response;

			String desc = r.getResultDesc();
			if (desc != null && desc.length() > 0) {
				ToastUtil.longShow(desc);
			} else {
				ToastUtil.longShow(R.string.auth_error);
			}
			return false;
		}

		if (SdkManagerJuZi.power) {
			if (response instanceof PayOrangeHandleResp) {
				SdkManagerJuZi.getInstance().callBack(
						(PayOrangeHandleResp) response);
			}
		}

		if (response instanceof SysCommonPush) {

			SysCommonPush r = (SysCommonPush) response;
			String info = r.getInfo();

			if (r.getCode() == SysCommonPush.ONLINE_PRIZE__CODE
					|| r.getCode() == SysCommonPush.DRESSUP_ITEM__CODE) {
				return false;
			}

			if (info != null) {
				int len = info.length();
				if (len > 0 && len < 10) {
					ToastUtil.show(info);
				} else if (len >= 10) {
					ToastUtil.longShow(info);
				}
				// LogUtil.d("Demo", "SysCommonPush--->" + info);
				return true;
			}

			return true;
		}
		if (response instanceof AuthErrorResp) {
			if (interceptAutherror)
				return true;
		}

		return false;
	}
}
