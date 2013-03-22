
package com.hurray.landlord.net.http;

import com.hurray.landlord.AsyncTask2;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.NetworkUtil;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.ProgressDialog;
import android.content.Context;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author sunzhibin
 */
public class HttpAsyncTask extends AsyncTask2<Void, Integer, HttpResponse> {

    private static final String TAG = "HttpAsyncTask";

    private static final int HTTP_PROGRESS_CONNECTING = 1;

    private static final int HTTP_PROGRESS_READING = 2;

    private static final int HTTP_PROGRESS_FINISHED = 3;

    // private static final long WAIT_TIME = Long.MAX_VALUE;

    private static final int MAX_CONCURRENT_THREAD_NUM = 1;

    private static final int MAX_REPEAT_REQUEST_TIMES = 3;

    /** The global http client for Http request **/
    private static ThreadSafeHttpClient sHttpClient = new ThreadSafeHttpClient();

    /****/
    private static HashMap<Integer, HttpAsyncTask> sHttpTaskMap = new HashMap<Integer, HttpAsyncTask>();

    /** The callback for the http task response */
    private HttpCallback mCallback;

    private Context mContext;

    private ProgressDialog mProgressDialog;

    private HttpRequest mReq;

    private HttpResponse mResp;

    /** flag if progress is shown */
    private boolean mShowProgress;

    private OnDownloadListener mDownloadListener = null;

    {
        HttpParams params = sHttpClient.getParams();
        params.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5000);
        params.setIntParameter(HttpConnectionParams.SO_TIMEOUT, 30000);
        sHttpClient.setParams(params);
    }

    @SuppressWarnings("unused")
    private HttpAsyncTask() {

    }

    /**
     * @param req The request to server, NOT null
     * @param resp The response from server, NOT null
     * @param callback The function to be called after got response from server,
     *            could be null
     * @param showProgress If a progress bar should be displayed, could be null
     * @param context the Context to be used by progress bar, could be null
     * @author sunzhibin
     */
    public HttpAsyncTask(HttpRequest req, HttpResponse resp, HttpCallback callback) {
        mReq = req;
        mResp = resp;
        mCallback = callback;
    }

    public void executeSafe() {
        // Only a connected network will execute the request
        if (NetworkUtil.isConnected()) {
            String key = mReq.toString();
            LogUtil.d(TAG, "executeSafe() executed: " + key);
            int hashCode = key.hashCode();
            synchronized (sHttpTaskMap) {
                if (!sHttpTaskMap.containsKey(hashCode)) {
                    if (sHttpTaskMap.size() < MAX_CONCURRENT_THREAD_NUM) {
                        LogUtil.d(TAG, "A task is going to be executed: " + key);
                        // set to null to indicate this task is running
                        sHttpTaskMap.put(hashCode, null);
                        this.execute();
                    } else {
                        sHttpTaskMap.put(hashCode, this);
                    }
                    LogUtil.d(TAG, "executeSafe's sHttpTaskArray.size(): " + sHttpTaskMap.size());
                }
            }
        } else {
            LogUtil.w(TAG, "Network is unconnected");
            if (mCallback != null) {
                LogUtil.w(TAG, "result_code: " + HttpResponse.RESULT_CODE_UNKNOWN_HOST + " 无法识别主机");
                mResp.setResultCode(HttpResponse.RESULT_CODE_UNKNOWN_HOST);
                mResp.setErrorMessage("无法识别主机");
                mCallback.onResponse(mReq, mResp);
            }
        }
    }

    @Override
    protected HttpResponse doInBackground(Void... params) {
        LogUtil.d(TAG, "doInBackground mReq.getReqURL(): " + mReq.getReqURL());

        BufferedInputStream bis = null;
        ByteArrayOutputStream bos = null;
        try {
            HttpUriRequest httpReq;
            if (mReq.getReqMethod().compareTo(HttpRequest.GET) == 0) {
                HttpGet get = new HttpGet(mReq.getReqURL());
                LogUtil.d(TAG, "getReqURL: " + mReq.getReqURL());
                httpReq = get;
            } else { // default post
                // Init the post request
                HttpPost post = new HttpPost(mReq.getReqURL());
                post.setEntity(mReq.pack());
                Header header = mReq.getHeader();
                if (header != null) {
                    post.setHeader(header);
                }
                httpReq = post;
                LogUtil.d(TAG, "The request content: " + mReq.toString());
            }

            // Execute the connection
            NetworkUtil.cmWapCheck(sHttpClient);
            org.apache.http.HttpResponse resp = sHttpClient.execute(httpReq);
            int result = resp.getStatusLine().getStatusCode();
            LogUtil.d(TAG, "Http response code: " + result);

            if (result == 200) {
                if (mShowProgress) {
                    publishProgress(HTTP_PROGRESS_READING);
                }
                HttpEntity respEntity = resp.getEntity();
                long contentLength = respEntity.getContentLength();
                LogUtil.d(TAG, "respEntity.getContentLength()=" + contentLength);
                bis = new BufferedInputStream(respEntity.getContent());

                byte[] buf = new byte[1024];
                int len = 0;
                if (mDownloadListener != null) {
                    if (contentLength > 0) {
                        mDownloadListener.onContentLength(contentLength);
                        long downloadedSize = 0;
                        while ((len = bis.read(buf)) > 0) {
                            mDownloadListener.onDataReceived(downloadedSize, buf, len);
                            downloadedSize += len;
                        }
                    } else {
                        mDownloadListener.onContentLengthError();
                    }
                } else {
                    bos = new ByteArrayOutputStream();
                    while ((len = bis.read(buf)) > 0) {
                        bos.write(buf, 0, len);
                    }

                    bis.close();
                    bis = null;

                    byte[] respContent = bos.toByteArray();
                    bos.close();
                    bos = null;

                    String respString = mResp.unPack(respContent);
                    LogUtil.d(TAG, "The response content: " + respString);
                }
                buf = null;
                mResp.setResultCode(HttpResponse.RESULT_CODE_OK);
            } else {
                LogUtil.w(TAG, "result_code: " + HttpResponse.RESULT_CODE_HTTP_ERROR
                            + " Http请求错误");
                mResp.setResultCode(HttpResponse.RESULT_CODE_HTTP_ERROR);
                mResp.setErrorMessage("Http请求错误");
            }
        } catch (UnknownHostException e) {
            LogUtil.w(TAG, "result_code: " + HttpResponse.RESULT_CODE_UNKNOWN_HOST + " 无法识别主机");
            mResp.setResultCode(HttpResponse.RESULT_CODE_UNKNOWN_HOST);
            mResp.setErrorMessage("无法识别主机");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            LogUtil.w(TAG, "result_code: " + HttpResponse.RESULT_CODE_PROTOCOL_ERROR
                        + " Http协议错误");
            mResp.setResultCode(HttpResponse.RESULT_CODE_PROTOCOL_ERROR);
            mResp.setErrorMessage("Http协议错误");
            e.printStackTrace();
        } catch (IOException e) {
            LogUtil.w(TAG, "result_code: " + HttpResponse.RESULT_CODE_UNKNOWN + " IO错误");
            mResp.setResultCode(HttpResponse.RESULT_CODE_UNKNOWN);
            mResp.setErrorMessage("IO错误");
            e.printStackTrace();
        } catch (Exception e) {
            LogUtil.w(TAG, "result_code: " + HttpResponse.RESULT_CODE_UNKNOWN + " 未知错误");
            mResp.setResultCode(HttpResponse.RESULT_CODE_UNKNOWN);
            mResp.setErrorMessage("未知错误");
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bis = null;
            }

            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bos = null;

            }
        }

        return mResp;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(HttpResponse result) {
        LogUtil.d(TAG, "onPostExecute");
        if (mShowProgress)
            publishProgress(HTTP_PROGRESS_FINISHED);
        mContext = null;

        /** Run the next task if there are any **/
        String key = mReq.toString();
        synchronized (sHttpTaskMap) {
            sHttpTaskMap.remove(key.hashCode());
            if (sHttpTaskMap.size() > 0) {
                Iterator<Entry<Integer, HttpAsyncTask>> iterator = sHttpTaskMap.entrySet()
                        .iterator();

                while (iterator.hasNext()) {
                    Entry<Integer, HttpAsyncTask> entry = iterator.next();
                    HttpAsyncTask task = entry.getValue();
                    if (null != task) {
                        // This task is not running
                        LogUtil.d(TAG, "A task is coming back to alive: " + key);
                        entry.setValue(null);
                        task.execute();
                        break;
                    } else {
                        iterator.remove();
                    }
                }
            }
            LogUtil.d(TAG, "onPostExecute's sHttpTaskArray.size(): " + sHttpTaskMap.size());
        }

        int times = mReq.getRepeatTimes();
        if (times < MAX_REPEAT_REQUEST_TIMES) {
            if ((HttpResponse.RESULT_CODE_UNKNOWN == result.getResultCode())) {

                // Re-check the network state when unknown situation.
                // Sometimes, network can be in "cmwap" while the settings show
                // "cmnet". Or, vice versa.
                if (HttpResponse.RESULT_CODE_UNKNOWN == result.getResultCode()) {
                    NetworkUtil.checkNetworkState();
                }

                mReq.setRepeatTimes(++times);
                // re-send the request
                HttpAsyncTask reSendTask = new HttpAsyncTask(mReq, mResp, mCallback);
                reSendTask.executeSafe();
            } else if (mCallback != null) {
                mCallback.onResponse(mReq, result);
            }
        } else {
            // Rebuild too many times, pop up a toast to the front-end
            // App.rebuildError();
            if (mCallback != null) {
                mCallback.onResponse(mReq, result);
            }
        }
        mCallback = null;
    }

    public void setOnDownloadListener(OnDownloadListener listener) {
        mDownloadListener = listener;
    }

    public interface OnDownloadListener {

        public void onContentLengthError();

        public void onContentLength(long contentLength);

        public void onDataReceived(long downSize, byte[] data, int dataSize) throws IOException;
    }
}
