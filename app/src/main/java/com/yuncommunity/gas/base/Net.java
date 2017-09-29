package com.yuncommunity.gas.base;

import android.app.Activity;

import com.oldfeel.utils.LogUtil;
import com.oldfeel.utils.NetUtil;

import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by oldfeel on 17-4-12.
 */

public class Net extends NetUtil {
    public static final String ROOT_URL = "http://61.164.45.100:666";
    public static final String WEB_SERVER_URL = ROOT_URL + "/WebMain.asmx";

    // 命名空间
    private static final String NAMESPACE = "http://tempuri.org/";

    public Net(Activity activity, String api) {
        super(activity, api);
    }

    /**
     * 发送请求
     *
     * @param text
     * @param callback
     */
    public void sendRequest(final String text, final Callback callback) {
        LogUtil.showLog("sendRequest " + api);
        LogUtil.showLog("params " + params);
        if (activity != null && !isNetworkConnect()) {
            whetherOpenNet();
            return;
        }
        showPd(text);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                final SoapObject result = soapObjectResult();
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
                        if (callback != null && (activity != null && !activity.isFinishing())) {
                            if (result != null) {
                                if (result.getProperty(api + "Result") instanceof SoapObject) {
                                    callback.success((SoapObject) result.getProperty(api + "Result"));
                                } else {
                                    callback.otherSuccess(result.getProperty(api + "Result"));
                                }
                            } else {
                                callback.success(null);
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

    public SoapObject soapObjectResult() {
        // 创建HttpTransportSE对象，传递WebService服务器地址
        final HttpTransportSE httpTransportSE = new HttpTransportSE(WEB_SERVER_URL);
        // 创建SoapObject对象
        SoapObject soapObject = new SoapObject(NAMESPACE, api);

        // SoapObject添加参数
        Iterator<?> iterator = params.keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            try {
                soapObject.addProperty(key, params.get(key).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
        final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // 设置是否调用的是.Net开发的WebService
        soapEnvelope.setOutputSoapObject(soapObject);
        soapEnvelope.dotNet = true;
        httpTransportSE.debug = true;

        SoapObject result = null;
        try {
            httpTransportSE.call(NAMESPACE + api, soapEnvelope);
            if (soapEnvelope.getResponse() != null) {
                // 获取服务器响应返回的SoapObject
                LogUtil.showLog("api is " + api);
                LogUtil.showLog("bodyIn is " + soapEnvelope.bodyIn);
                result = (SoapObject) soapEnvelope.bodyIn;
                LogUtil.showLog("result is " + result);
            }
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public abstract static class Callback {
        public abstract void success(SoapObject soapObject);

        public void otherSuccess(Object object) {
        }
    }
}