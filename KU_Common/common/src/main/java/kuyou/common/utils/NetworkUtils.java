package kuyou.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-22 <br/>
 * </p>
 */
public class NetworkUtils {
    private static final String TAG = "kuyou.common.utils > NetworkUtils";

    private static ConnectivityManager sConnectivityManager;

    /**
     * action: 判断网络是否可用
     *
     * */
    public static boolean isNetworkAvailable(Context context) {
        if (null == sConnectivityManager)
            sConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (sConnectivityManager == null) {
            Log.e(TAG, " isNetworkAvailable > ConnectivityManager is null");
            return false;
        }
        NetworkInfo info = sConnectivityManager.getActiveNetworkInfo();
        boolean isConnected = info != null && info.isConnected()  // 当前网络是连接的
                && info.getState() == NetworkInfo.State.CONNECTED;      // 当前所连接的网络可用
        Log.d(TAG, " isNetworkAvailable > ConnectivityManager.isConnected = " + isConnected);
        return isConnected;
    }

    /**
     * 把IP拆分位int数组
     *
     * @param ip
     * @return
     *
     */
    public int[] getIntIPValue(String ip) throws Exception {
        String[] sip = ip.split("[.]");
        // if (sip.length != 4) {
        // throw new Exception("error IPAddress");
        // }
        int[] intIP = {Integer.parseInt(sip[0]), Integer.parseInt(sip[1]), Integer.parseInt(sip[2]),
                Integer.parseInt(sip[3])};
        return intIP;
    }

    /**
     * 把byte类型IP地址转化位字符串
     *
     * @param address
     * @return
     *
     */
    public String getStringIPValue(byte[] address) throws Exception {
        int first = ByteUtils.byte2Int(address[0]);
        int second = ByteUtils.byte2Int(address[1]);
        int third = ByteUtils.byte2Int(address[2]);
        int fourth = ByteUtils.byte2Int(address[3]);

        return first + "." + second + "." + third + "." + fourth;
    }
}
