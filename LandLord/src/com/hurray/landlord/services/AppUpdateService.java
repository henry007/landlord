
package com.hurray.landlord.services;

import com.hurray.landlord.R;
import com.hurray.landlord.net.http.HttpAsyncTask;
import com.hurray.landlord.net.http.HttpAsyncTask.OnDownloadListener;
import com.hurray.landlord.net.http.HttpCallback;
import com.hurray.landlord.net.http.HttpRequest;
import com.hurray.landlord.net.http.HttpResponse;
import com.hurray.landlord.net.http.req.GetApkRequest;
import com.hurray.landlord.net.http.resp.GetApkResponse;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.PathUtil;
import com.hurray.landlord.utils.SdcardUtil;
import com.hurray.landlord.utils.ToastUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;
import java.io.IOException;

public class AppUpdateService extends Service {

    public static final String APP_UPDATE_URL = "APP_UPDATE_URL";

    private static final String TAG = "AppUpdateService";

    private static final String FILE_SUFFIX_APK = ".apk";

    private static final int MAX_PROGRESS = 100;

    private static final int NFY_ID = 0;

    // url

    private String mUrl;

    private String mFileName;

    private String mFullName;

    private RemoteViews mRemote;

    private PendingIntent mPending;

    private NotificationManager mNfcMgr;

    private Notification mNfc;

    private int mProgress;

    private long mDownSize;

    private long mFileSize;

    private final IBinder mBinder = new AppUpdateBinder();

    public boolean mUpdating = false;

    private boolean mIsRunning = false;

    public boolean isRunning() {
        return mIsRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        initNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mIsRunning) {
            ToastUtil.show("升级已在运行");
            return START_REDELIVER_INTENT;
        }
        mIsRunning = true;

        Log.d(TAG, "onStartCommand" + " flags=" + flags + " startId=" + startId);

        mProgress = -1;
        mFileSize = 0;
        mDownSize = 0;
        mUrl = intent.getStringExtra(APP_UPDATE_URL);
        mFileName = "landlord_v" + System.currentTimeMillis() + FILE_SUFFIX_APK;

        // check parms
        long availableSize = SdcardUtil.getSdcardAvailableSize();
        if (availableSize < 15L * 1024L * 1024L) { // 空间不足
            ToastUtil.show("SD卡未就绪或空间不足");
            mIsRunning = false;
            return START_REDELIVER_INTENT;
        } else {
            String fullpath = PathUtil.getApkUpdatePath();
            if (fullpath != null && fullpath.length() > 0) {
                mFullName = fullpath + File.separator + mFileName;
                LogUtil.d(TAG, "mFileName=" + mFileName);
                LogUtil.d(TAG, "mFullName=" + mFullName);
                ToastUtil.show("开始下载APK安装包，请在通知栏检查进度");
                download();
            } else {
                ToastUtil.show("update_abort_cos_no_sdcard");
                mIsRunning = false;
                return START_REDELIVER_INTENT;
            }
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        mIsRunning = false;
    }

    public class AppUpdateBinder extends Binder {
        public AppUpdateService getService() {
            return AppUpdateService.this;
        }
    }

    private void initNotification() {
        LogUtil.d(TAG, "initNotification");

        mNfcMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mRemote = new RemoteViews(getPackageName(), R.layout.update_remote);

        Intent i = new Intent(this, Notification.class);
        mPending = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        mNfc = new Notification();
        mNfc.icon = R.drawable.ic_launcher;
        mNfc.defaults = Notification.FLAG_ONLY_ALERT_ONCE;
    }

    private void updateNotification() {
        LogUtil.d(TAG, "updateNotification: " + mProgress);

        mRemote.setProgressBar(R.id.progressbar_update, MAX_PROGRESS, mProgress, false);
        mRemote.setTextViewText(R.id.text_update, mProgress + "%");
        mNfc.contentView = mRemote;
        mNfc.contentIntent = mPending;
        mNfcMgr.notify(NFY_ID, mNfc);
    }

    private void cancelNotification() {
        if (mNfcMgr != null) {
            LogUtil.d(TAG, "cancelNotification");
            mNfcMgr.cancel(NFY_ID);
            LogUtil.d(TAG, "canceled notification manager");
        }
    }

    private void cancelAndStop() {
        cancelNotification();
        stopSelf();
    }

    private void download() {
        LogUtil.d(TAG, "++++download");
        HttpCallback callback = new HttpCallback() {
            @Override
            public void onResponse(HttpRequest req, HttpResponse resp) {

                if (resp.getResultCode() == HttpResponse.RESULT_CODE_OK
                            && mDownSize > 0
                            && mFileSize > 0
                            && mDownSize == mFileSize) {
                    LogUtil.d(TAG, "mDownloadedSize=" + mDownSize);
                    LogUtil.d(TAG, "mFileSize=" + mFileSize);

                    ToastUtil.longShow("APK安装包下载到\n" + mFullName);
                    installApk();
                } else {
                    ToastUtil.longShow("下载出错，升级失败");
                    clearApk();
                }

                cancelAndStop();
            }
        };
        GetApkRequest req = new GetApkRequest(mUrl);
        GetApkResponse resp = new GetApkResponse();
        HttpAsyncTask task = new HttpAsyncTask(req, resp, callback);
        task.setOnDownloadListener(new OnDownloadListener() {

            @Override
            public void onDataReceived(long downSize, byte[] data, int dataSize) throws IOException {
                String fullName = SdcardUtil.write2SDCard(PathUtil.getApkUpdatePath(), mFileName,
                        downSize, data);
                if (fullName != null) {
                    updateProgress(downSize + dataSize);
                    mDownSize = downSize + dataSize;
                } else { // 出错
                    ToastUtil.show("写入SD卡出错");
                    throw new IOException();
                }
            }

            @Override
            public void onContentLength(long contentLength) {
                mFileSize = contentLength;
            }

            @Override
            public void onContentLengthError() {
                mFileSize = 0;
                LogUtil.d(TAG, "onContentLengthError");
            }
        });
        task.executeSafe();
    }

    private void updateProgress(long downSize) {
        int progress = (Float.valueOf((downSize * 1.0f / mFileSize * MAX_PROGRESS))).intValue();
        if (progress > mProgress || progress == MAX_PROGRESS) {
            LogUtil.d(TAG, "mDownSize/mFileSize: " + downSize + "/" + mFileSize);
            LogUtil.d(TAG, "mProgress: " + mProgress);
            mProgress = progress;
            updateNotification();
        }
    }

    private void installApk() {
        cancelNotification();
        LogUtil.d(TAG, "mFullName=" + mFullName);

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + mFullName), "application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void clearApk() {
        File file = new File(mFullName);
        if (file.exists()) {
            if (file.delete()) {
                LogUtil.d(TAG, mFullName + " remove success");
            } else {
                LogUtil.d(TAG, mFullName + " remove failed");
            }
        } else {
            LogUtil.d(TAG, mFullName + " not exist");
        }
    }

}
