package com.oldfeel.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.widget.TextView;

import com.oldfeel.conf.BaseConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络接口,post请求string或者get请求json,里面只包含一个线程,只能同时发送一个网络请求
 *
 * @author oldfeel
 */
public class NetUtil extends Handler {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final int REQUEST_METHOD_POST = 1;
    public static final int REQUEST_METHOD_JSON = 2;
    public static final int REQUEST_METHOD_GET = 3;
    public static final int REQUEST_METHOD_PUT = 4;
    public static final int REQUEST_METHOD_DELETE = 5;
    private static final long TIME_OUT = 30;

    public Activity activity;
    public JSONObject params = new JSONObject();
    public ProgressDialog pd;
    public Thread requestThread;
    public String api;
    public OnJsonResultListener listener;
    public String rootUrl;
    public int requestMethod = 1;
    /**
     * true 为记录 cookie
     */
    public boolean isCookie = false;
    /**
     * true 为显示 log 信息
     */
    private boolean isShowLog = true;
    /**
     * true 为 log 返回的 字符串
     */
    protected boolean isLogResult = false;
    /**
     * true 为 toast 错信息
     */
    protected boolean isToastFail = false;
    private MyCookieStore myCookieStore;

    public void setCookie(boolean cookie) {
        isCookie = cookie;
        if (isCookie) {
            myCookieStore = new MyCookieStore(activity);
        }
    }

    public void setLogResult(boolean logResult) {
        isLogResult = logResult;
    }

    public void setToastFail(boolean toastFail) {
        isToastFail = toastFail;
    }

    public NetUtil(String api) {
        this.api = api;
    }

    public void setRequestMethod(int requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * 构造一个netapi对象
     *
     * @param activity
     * @param api      这次请求需要调用的api
     */
    public NetUtil(Activity activity, String api) {
        this.activity = activity;
        if (api.startsWith("http")) {
            setRootUrl(api);
        } else {
            this.api = api;
        }
    }

    /**
     * 添加参数
     */
    public void setParams(String key, Object object) {
        String value = null;
        if (object instanceof TextView) {
            value = ((TextView) object).getText().toString();
        } else if (object != null) {
            value = object.toString();
        }
        if (!StringUtil.isEmpty(key)) {
            if (StringUtil.isEmpty(value)) {
                value = "";
            }
            try {
                params.put(key.trim(), value.trim());// *.trim(),取消首尾空格
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取参数
     */
    public String getParam(String key) {
        try {
            return params.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 显示进度条提示
     *
     * @param message
     */
    public void showPd(String message) {
        if (activity == null) {
            return;
        }
        if (message == null || message.length() == 0) {
            return;
        }
        if (pd != null) {
            pd.cancel();
            pd = null;
        }
        pd = new ProgressDialog(activity);
        pd.setMessage(message);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (requestThread != null) {
                    requestThread.interrupt();
                }
            }
        });
        pd.setCancelable(true);
        pd.show();
    }

    public void sendGet(String text, OnJsonResultListener onJsonResultListener) {
        requestMethod = REQUEST_METHOD_GET;
        sendPost(text, onJsonResultListener);
    }

    public void sendDelete(String text, OnJsonResultListener onJsonResultListener) {
        requestMethod = REQUEST_METHOD_DELETE;
        sendPost(text, onJsonResultListener);
    }

    public void sendPut(String text, OnJsonResultListener onJsonResultListener) {
        requestMethod = REQUEST_METHOD_PUT;
        sendPost(text, onJsonResultListener);
    }

    public void sendJson(String text, OnJsonResultListener onJsonResultListener) {
        requestMethod = REQUEST_METHOD_JSON;
        sendPost(text, onJsonResultListener);
    }

    /**
     * 发送post请求,获取字符串结果
     *
     * @throws JSONException
     * @throws Exception
     */
    public String postStringResult() {
        if (rootUrl == null) {
            rootUrl = BaseConstant.getInstance().getRootUrl();
        }
        String requestUrl = rootUrl + api;

        if (isDebug()) {
            logRequestUrl(requestUrl);
        }

        try {
            OkHttpClient client = getClient();

            Request request;
            RequestBody body;

            switch (requestMethod) {
                case REQUEST_METHOD_DELETE:
                    body = RequestBody.create(JSON, params.toString());
                    request = new Request.Builder()
                            .url(requestUrl)
                            .delete(body)
                            .addHeader("Authorization", getAuthorization())
                            .build();
                    break;
                case REQUEST_METHOD_GET:
                    request = new Request.Builder()
                            .url(getRequestGetUrl(requestUrl))
                            .build();
                    break;
                case REQUEST_METHOD_JSON:
                    body = RequestBody.create(JSON, params.toString());
                    request = new Request.Builder()
                            .url(requestUrl)
                            .post(body)
                            .build();
                    break;
                case REQUEST_METHOD_PUT:
                    body = RequestBody.create(JSON, params.toString());
                    request = new Request.Builder()
                            .url(requestUrl)
                            .put(body)
                            .addHeader("Authorization", getAuthorization())
                            .build();
                    break;
                default: // REQUEST_METHOD_POST
                    FormBody.Builder fb = new FormBody.Builder();

                    Iterator<?> iterator = params.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        try {
                            fb.add(key, params.get(key).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    body = fb.build();
                    request = new Request.Builder()
                            .url(requestUrl)
                            .post(body)
                            .build();
                    break;
            }
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getRequestGetUrl(String requestUrl) {
        Iterator<?> iterator = params.keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();

            // 打印出get方法请求的url
            if (requestUrl.contains("?")) {
                requestUrl = requestUrl + "&";
            } else {
                requestUrl = requestUrl + "?";
            }
            try {
                requestUrl += key + "=" + params.get(key).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return requestUrl;
    }

    private void logRequestUrl(String requestUrl) {
        showLog("request url is " + getRequestGetUrl(requestUrl));
    }

    private boolean isDebug() {
        return BaseConstant.getInstance().isDebug();
    }

    private String getAuthorization() {
        return "";
    }

    protected File file;
    protected String fileKey;

    public void setFile(String fileKey, File file) {
        this.fileKey = fileKey;
        this.file = file;
    }

    /**
     * 发送带文件的post请求
     *
     * @return
     */
    protected String postFileResult() {
        if (rootUrl == null) {
            rootUrl = BaseConstant.getInstance().getRootUrl();
        }
        String requestUrl = rootUrl + api;

        try {
            OkHttpClient client = getClient();

            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(fileKey, file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

            Iterator<?> iterator = params.keys();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                try {
                    builder.addFormDataPart(key, params.get(key).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Request request = new Request.Builder()
                    .url(requestUrl)
                    .post(builder.build())
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private OkHttpClient getClient() {
        OkHttpClient client;
        if (isCookie) {
            if (myCookieStore == null) {
                showLog("请先通过 setCookie(true) 初始化 myCookieStore");
                return null;
            }
            client = new OkHttpClient.Builder()
                    .cookieJar(new CookieJar() {
                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            myCookieStore.put(url, cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = myCookieStore.get(url.toString());
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    })
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .build();
        } else {
            client = new OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public void setShowLog(boolean isShowLog) {
        this.isShowLog = isShowLog;
    }

    public void showLog(Object log) {
        if (isShowLog)
            LogUtil.showLog(log);
    }

    public interface OnUploadListener {
        public void onUploadProcess(int counter);
    }

    private OnUploadListener onUploadListener;

    public void setOnUploadListener(OnUploadListener onUploadListener) {
        this.onUploadListener = onUploadListener;
    }

    public class Complete implements Runnable {
        @Override
        public void run() {
            if (pd != null) {
                pd.cancel();
            }
            requestThread.interrupt();
        }
    }

    /**
     * 判断网络连接
     */
    public boolean isNetworkConnect() {
        ConnectivityManager cm = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.getActiveNetworkInfo();
        if (cm.getActiveNetworkInfo() != null) {
            return cm.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    /**
     * 打开网络对话框
     */
    public void whetherOpenNet() {
        if (activity == null) {
            return;
        }
        DialogUtil.getInstance().showOpenNetDialog(activity, cancelListener);
    }

    /**
     * 获取当前NetApi绑定的activity
     *
     * @return
     */
    public Activity getActivity() {
        return this.activity;
    }

    public interface OnCancelListener {
        public void cancel();
    }

    private OnCancelListener cancelListener;

    /**
     * 取消网络请求监听。。。
     *
     * @param cancelListener
     */
    public void setCancelListener(OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }


    /**
     * 返回结果的处理
     */
    public interface OnJsonResultListener {
        public void onSuccess(String data);

        public void onFail(int code, String data);
    }

    /**
     * 发送post请求
     *
     * @param text
     * @param listener
     */
    public void sendPost(final String text, final OnJsonResultListener listener) {
        if (activity != null && !isNetworkConnect()) {
            whetherOpenNet();
            return;
        }
        this.listener = listener;
        showPd(text);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                final String result = file == null ? postStringResult() : postFileResult();
                if (isLogResult) {
                    showLog(result);
                }
                if (requestThread.isInterrupted()) {
                    showLog("is interrupted");
                    return;
                }
                post(new Complete() {

                    @Override
                    public void run() {
                        if (listener != null && (activity != null && !activity.isFinishing())) {
                            if (result != null && isSuccess(result)) {
                                listener.onSuccess(getData(result));
                            } else {
                                String data = result == null ? "网络连接错误,没有返回任何数据" : getErrorData(result);
                                if (isToastFail) {
                                    DialogUtil.getInstance().showToast(activity, data);
                                }
                                listener.onFail(getCode(result), data);
                            }
                        }
                        super.run();
                    }
                });

            }
        };
        requestThread = new Thread(task);
        requestThread.start();
    }

    /**
     * 错误时的状态说明
     *
     * @param result
     * @return
     */
    public String getErrorData(String result) {
        return getData(result);
    }

    /**
     * 结果是否正确
     *
     * @param result
     * @return
     */
    public boolean isSuccess(String result) {
        return getCode(result) == 0;
    }

    /**
     * 返回的数据
     *
     * @param result
     * @return
     */
    public String getData(String result) {
        if (StringUtil.isEmpty(result)) {
            return result;
        }
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("data")) {
                return json.getString("data");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getCode(String result) {
        if (StringUtil.isEmpty(result)) {
            return -1;
        }
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("code")) {
                return json.getInt("code");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void sendPost(OnJsonResultListener listener) {
        this.sendPost(null, listener);
    }

    public void sendPost() {
        this.sendPost(null);
    }
}