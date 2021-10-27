package com.kuyou.rc.basic.wifi;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import kuyou.common.xml.BasicXmlParser;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-10-22 <br/>
 * </p>
 */
public class WifiConnectInfoParser extends BasicXmlParser {

    protected final String TAG = "com.kuyou.xmlparsedemo.wifi > WifiConnectInfoParser";

    public static final String TAG_CONNECT = "WifiConnectionList";
    public static final String TAG_ITEM = "WifiConnect";
    public static final String TAG_SSID = "ssid";
    public static final String TAG_PASSWORD = "password";

    private WifiConnectInfo mWifiConnectInfo = null;
    private IWifiConnectInfoListener mWifiConnectInfoListener;


    public IWifiConnectInfoListener getWifiConnectInfoListener() {
        return mWifiConnectInfoListener;
    }

    public WifiConnectInfoParser setWifiConnectInfoListener(IWifiConnectInfoListener wifiConnectInfoListener) {
        mWifiConnectInfoListener = wifiConnectInfoListener;
        return WifiConnectInfoParser.this;
    }

    protected void onReadWifiConnectInfo(WifiConnectInfo info) {
        if (null == getWifiConnectInfoListener()) {
            Log.w(TAG, "onReadWifiConnectInfo > process fail : getWifiConnectInfoListener is null");
            return;
        }
        getWifiConnectInfoListener().onReadWifiConnectInfo(info);
    }

    protected void onReadFinish() {
        if (null == getWifiConnectInfoListener()) {
            Log.w(TAG, "onReadFinish > process fail : getWifiConnectInfoListener is null");
            return;
        }
        getWifiConnectInfoListener().onReadFinish();
    }

    @Override
    protected void onParseItemEnd(String tagName) {
        if (null == tagName) {
            Log.w(TAG, "onParseItemEnd > process fail : tagName is null ");
            return;
        }
        if (TAG_ITEM.equals(tagName)) {
            if (null == mWifiConnectInfo) {
                return;
            }
            onReadWifiConnectInfo(mWifiConnectInfo);
            return;
        }
        if (TAG_CONNECT.equals(tagName)) {
            onReadFinish();
            return;
        }
        Log.w(TAG, "onParseItemEnd > process fail : invalid tagName = " + tagName);
    }

    /**
     * @hide
     */
    @Override
    final protected void parseItem(String tagName, XmlPullParser parser) throws Exception {
        switch (tagName) {
            case WifiConnectInfoParser.TAG_ITEM:
                mWifiConnectInfo = new WifiConnectInfo();
                break;
            case WifiConnectInfoParser.TAG_SSID:
                mWifiConnectInfo.setSsid(parser.nextText());
                break;
            case WifiConnectInfoParser.TAG_PASSWORD:
                mWifiConnectInfo.setPassword(parser.nextText());
                break;
            default:
                break;
        }
    }
}
