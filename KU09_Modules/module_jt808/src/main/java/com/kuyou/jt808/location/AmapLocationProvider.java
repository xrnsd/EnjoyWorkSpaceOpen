package com.kuyou.jt808.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.kuyou.jt808.R;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-6 <br/>
 * </p>
 */
public class AmapLocationProvider extends HMLocationProvider implements AMapLocationListener {

    private static final String NOTIFICATION_CHANNEL_NAME = "状态";
    private boolean isCreateChannel = false;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private NotificationManager notificationManager = null;

    private static AmapLocationProvider sMain;

    private AmapLocationProvider(Context context) {
        super(context);
    }

    public static AmapLocationProvider getInstance(Context context) {
        if (null == sMain) {
            sMain = new AmapLocationProvider(context);
        }
        return sMain;
    }

    @Override
    protected void init() {
        //initGaoDeLocationSDK(mContext);
    }

    @Override
    protected int getLocationFreq() {
        return 5000;
    }

    /**
     * 初始化定位
     * <p>
     * since: 2.8.0 <br/>
     * author wuguoxian <br/>
     * belong 高德地图SDK <br/>
     */
    private void initGaoDeLocationSDK(Context context) {
        if (null != locationClient)
            return;
        //初始化client
        locationClient = new AMapLocationClient(context.getApplicationContext());
        locationOption = getDefaultOption();
        if (null == locationClient) {
            Log.e(TAG, "initGaoDeLocationSDK init fail > locationClient is null");
            return;
        }
        if (null == locationOption) {
            Log.e(TAG, "initGaoDeLocationSDK init fail > locationOption is null");
            return;
        }

        //设置定位参数
        locationClient.setLocationOption(locationOption);
        //设置定位监听
        locationClient.setLocationListener(AmapLocationProvider.this);
    }

    /**
     * 默认的定位参数
     * <p>
     * since: 2.8.0 <br/>
     * author wuguoxian <br/>
     * belong 高德地图SDK <br/>
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(getLocationFreq());//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(true);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        printfAMapLocationInfo(aMapLocation);
        dispatchEventLocationChange(aMapLocation);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != locationClient) {
            Log.d(TAG, "onResume > disableBackgroundLocation");
            locationClient.disableBackgroundLocation(true);
        }
        startLocation(mContext);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != locationClient) {
            Log.d(TAG, "onStop > enableBackgroundLocation");
            locationClient.enableBackgroundLocation(2001, buildNotification(mContext));
        }
    }

    /**
     * 开始定位
     * <p>
     * since: 2.8.0 <br/>
     * author wuguoxian <br/>
     * belong 高德地图SDK <br/>
     */
    @Override
    public void startLocation(Context context) {
        Log.i(TAG, "startLocation > ");
        if (null == locationClient) {
            locationClient = new AMapLocationClient(context);
        }
        if (null == locationOption) {
            locationOption = getDefaultOption();
        }
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    private void printfAMapLocationInfo(AMapLocation location) {
        StringBuffer sb = new StringBuffer("AMapLocationListener > onLocationChanged > printfAMapLocationInfo :");

        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.getErrorCode() == 0) {
            sb.append("定位成功" + "\n");
            sb.append("定位类型: " + location.getLocationType() + "\n");
            sb.append("提供者    : " + location.getProvider() + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\n");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
            sb.append("高    度    : " + location.getAltitude() + "米" + "\n");
            sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
            sb.append("角    度    : " + location.getBearing() + "\n");
            sb.append("星    数    : " + location.getSatellites() + "\n");
            sb.append("国    家    : " + location.getCountry() + "\n");
            sb.append("省            : " + location.getProvince() + "\n");
            sb.append("市            : " + location.getCity() + "\n");
            sb.append("城市编码 : " + location.getCityCode() + "\n");
            sb.append("区            : " + location.getDistrict() + "\n");
            sb.append("区域 码   : " + location.getAdCode() + "\n");
            sb.append("地    址    : " + location.getAddress() + "\n");
            sb.append("地    址    : " + location.getDescription() + "\n");
            sb.append("兴趣点    : " + location.getPoiName() + "\n");
            //定位完成的时间
            // sb.append("定位时间: " + Utils.formatLocalTimeByMilSecond(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");

        } else { //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
        }
        sb.append("***定位质量报告***").append("\n");
        sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
        sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
        sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
        sb.append("****************").append("\n");
        // sb.append("回调时间: " + Utils.formatLocalTimeByMilSecond(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

        Log.i(TAG, sb.toString());
    }

    /**
     * 获取GPS状态的字符串
     * <p>
     * since: 2.8.0 <br/>
     * author wuguoxian <br/>
     * belong 高德地图SDK <br/>
     *
     * @param statusCode GPS状态码
     */
    private String getGPSStatusString(int statusCode) {
        switch (statusCode) {
            case AMapLocationQualityReport.GPS_STATUS_OK:
                return "GPS状态正常";
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                return "手机中没有GPS Provider，无法进行GPS定位";
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                return "GPS关闭，建议开启GPS，提高定位质量";
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                return "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                return "没有GPS定位权限，建议开启gps定位权限";
            default:
                return "";
        }
    }


    /**
     * action:开始后台定位<br/>
     * <p>
     * since: 2.8.0 <br/>
     * author wuguoxian <br/>
     * belong 高德地图SDK <br/>
     */
    @SuppressLint("NewApi")
    private Notification buildNotification(Context context) {

        Notification.Builder builder = null;
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = context.getPackageName();
            if (!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(context, channelId);
        } else {
            builder = new Notification.Builder(context);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(PendingIntent.getActivity(
                        context, 0, context.getPackageManager().getLaunchIntentForPackage(
                                context.getPackageName()), PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.run_background))
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }

    @Override
    public void startLocation() {
        startLocation(mContext);
    }
}
