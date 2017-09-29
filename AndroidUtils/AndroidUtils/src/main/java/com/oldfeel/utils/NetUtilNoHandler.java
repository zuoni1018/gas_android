package com.oldfeel.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.widget.TextView;

import com.oldfeel.conf.BaseConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 不是继承自 hanler 的 netutil
 * Created by oldfeel on 15/8/1.
 */
public class NetUtilNoHandler {
    /**
     * 超时时间限制
     */
    public static final int TIME_OUT = 30 * 1000;
    public static final int BUFFER_SIZE = 1024;
    public JSONObject params = new JSONObject();
    public ProgressDialog pd;
    public Thread requestThread;
    public AlertDialog dialog;
    public String api;

    public NetUtilNoHandler(String api) {
        this.api = api;
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
        if (!StringUtil.isEmpty(key) && !StringUtil.isEmpty(value)) {
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
    public String getParam(String key) throws JSONException {
        return params.getString(key);
    }

    /**
     * 发送post上传文件,获取字符串结果
     *
     * @throws JSONException
     * @throws Exception
     */
    public String postStringResult() throws SocketTimeoutException,
            JSONException {
        String requestUrl = BaseConstant.getInstance().getRootUrl() + api;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);

            String COOKIES_HEADER = "Set-Cookie";
            CookieManager msCookieManager = CookieHandler.getDefault() == null ?
                    new CookieManager() : (CookieManager) CookieHandler.getDefault();
            msCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(msCookieManager);

            if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                LogUtil.showLog("add cookie " + msCookieManager.getCookieStore().getCookies());
                conn.setRequestProperty("Cookie",
                        TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
            } else {
                LogUtil.showLog("no cookie");
            }

            conn.connect();

            DataOutputStream outStream = new DataOutputStream(
                    conn.getOutputStream());
            Iterator<?> iterator = params.keys();
            StringBuilder sb = new StringBuilder();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                try {
                    outStream.writeBytes("&"
                            + URLEncoder.encode(key, "utf-8")
                            + "="
                            + URLEncoder.encode(params.get(key).toString(),
                            "utf-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 打印出get方法请求的url
                if (sb.length() == 0) {
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(URLEncoder.encode(key, "utf-8")
                        + "="
                        + URLEncoder
                        .encode(params.get(key).toString(), "utf-8"));
            }

            // save cookie
            Map<String, List<String>> headerFields = conn.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }

            requestUrl = requestUrl + sb.toString();
            LogUtil.showLog("request url is " + requestUrl);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return inputStreamTOString(conn.getInputStream());
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.showLog("connect failed: " + requestUrl);
        return null;
    }

    public String inputStreamTOString(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);
        return new String(outStream.toByteArray(), "UTF-8");
    }

    /**
     * 发送get上传文件,获取字符串结果
     *
     * @throws JSONException
     * @throws Exception
     */
    public String getStringResult() throws SocketTimeoutException,
            JSONException {
        try {
            String getPath = prepareGetPath();
            LogUtil.showLog("request url is " + getPath);
            URL url = new URL(getPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return inputStreamTOString(conn.getInputStream());
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.showLog("connect failed: " + BaseConstant.getInstance().getRootUrl() + api);
        return null;
    }

    public String prepareGetPath() {
        Iterator<?> iterator = params.keys();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            // 打印出get方法请求的url
            if (sb.length() == 0) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            try {
                sb.append(URLEncoder.encode(key, "utf-8")
                        + "="
                        + URLEncoder
                        .encode(params.get(key).toString(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return BaseConstant.getInstance().getRootUrl() + api + sb.toString();
    }

    /**
     * 发送post请求
     *
     * @param listener
     */
    public void sendPost(final NetUtil.OnJsonResultListener listener) {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    final String result = postStringResult();
                    if (requestThread.isInterrupted()) {
                        LogUtil.showLog("is interrupted");
                        return;
                    }
                    if (listener != null) {
                        if (isSuccess(result)) {
                            listener.onSuccess(getData(result));
                        } else {
                            listener.onFail(getCode(result), getErrorData(result));
                        }
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("data")) {
                return json.getString("data");
            } else {
                return result;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getCode(String result) {
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

    /**
     * 发送get请求
     *
     * @param listener
     */
    public void sendGet(final NetUtil.OnJsonResultListener listener) {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    final String result = getStringResult();
                    if (requestThread.isInterrupted()) {
                        LogUtil.showLog("is interrupted");
                        return;
                    }
                    getComplete(listener, result);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        requestThread = new Thread(task);
        requestThread.start();
    }

    public void getComplete(final NetUtil.OnJsonResultListener listener, final String result) {
        if (listener != null) {
            if (isSuccess(result)) {
                listener.onSuccess(getData(result));
            } else {
                listener.onFail(getCode(result), getData(result));
            }
        }
    }

}
