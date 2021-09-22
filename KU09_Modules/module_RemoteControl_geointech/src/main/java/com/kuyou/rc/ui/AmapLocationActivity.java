package com.kuyou.rc.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.kuyou.rc.R;
import com.kuyou.rc.basic.location.ILocationDispatcherCallback;

import kuyou.common.ku09.ui.BasicPermissionsHandlerActivity;

/**
 * action :位置接口实现,不负责具体业务
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-25 <br/>
 * <p>
 */
public class AmapLocationActivity extends BasicPermissionsHandlerActivity
        implements AMapLocationListener {

    protected final static String TAG = "com.kuyou.rc.handler.location.amap > AmapLocationActivity";

    private static final int GAO_DE_POSITION_FREQ = 2000;
    private static final String NOTIFICATION_CHANNEL_NAME = "状态";

    private boolean isCreateChannel = false;
    private AMapLocation mLocation = null;
    private Location mLocationFake = null;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private NotificationManager notificationManager = null;

    protected ILocationDispatcherCallback mLocationDispatcherCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        onBackPressed();
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        super.initViews();
        initGaoDeLocationSDK(getApplicationContext());
        startLocation(getApplicationContext());
    }

    @Override
    protected String[] getPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                && getApplicationInfo().targetSdkVersion > 28) {
            return new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.READ_PHONE_STATE
                    , Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    //, "com.mediatek.permission.CTA_ENABLE_BT"
            };
        }
        return new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.READ_PHONE_STATE
                //, "com.mediatek.permission.CTA_ENABLE_BT"
        };
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (null == location) {
            Log.w(TAG, " onLocationChanged > 定位失败 > use base location ");
            return;
        }
        if (location.getErrorCode() == 0) { //定位成功,进行处理
            dispatchLocation(location);
        }
        //printfAMapLocationInfo(location);
    }

    /**
     * action:停止后台定位<br/>
     * <p>
     * since: 2.8.0 <br/>
     * author wuguoxian <br/>
     * belong 高德地图SDK <br/>
     */
    @Override
    protected void onResume() {
        super.onResume();
//        if (null != locationClient) {
//            Log.d(TAG, "onResume > disableBackgroundLocation");
//            locationClient.disableBackgroundLocation(true);
//        }
        startLocation(getApplicationContext());
    }

    /**
     * action:开始后台定位<br/>
     * <p>
     * since: 2.8.0 <br/>
     * author wuguoxian <br/>
     * belong 高德地图SDK <br/>
     */
    @Override
    protected void onStop() {
        super.onStop();
//        if (null != locationClient) {
//            Log.d(TAG, "onStop > enableBackgroundLocation");
//            locationClient.enableBackgroundLocation(2001, buildNotification(getApplicationContext()));
//        }
    }

    public void dispatchLocation(Location location) {
        if (null == getLocationDispatcherCallback()) {
            Log.d(TAG, "dispatchEventLocationChange > process fail : getLocationDispatcherCallback() is null");
            return;
        }
        getLocationDispatcherCallback().dispatchLocation(location);
    }

    public ILocationDispatcherCallback getLocationDispatcherCallback() {
        return mLocationDispatcherCallback;
    }

    public void setLocationDispatcherCallback(ILocationDispatcherCallback callback) {
        mLocationDispatcherCallback = callback;
    }

    private void printfAMapLocationInfo(AMapLocation location) {
        StringBuffer sb = new StringBuffer("AMapLocationListener > onLocationChanged > printfAMapLocationInfo :");

        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.getErrorCode() == 0) {
            sb.append("\n定位成功");
            sb.append("\n定位类型: ").append(location.getLocationType());
            sb.append("\n提供者    : ").append(location.getProvider());
            sb.append("\n经    度  : ").append(location.getLongitude());
            sb.append("\n纬    度  : ").append(location.getLatitude());
            sb.append("\n精    度  : ").append(location.getAccuracy()).append("米");
            //sb.append("\n高    度  : ").append(location.getAltitude()).append("米");
            //sb.append("\n速    度  : ").append(location.getSpeed()).append("米/秒");
            //sb.append("\n角    度  : ").append(location.getBearing());
            //sb.append("\n星    数  : ").append(location.getSatellites());
            //sb.append("\n国    家  : ").append(location.getCountry());
            sb.append("\n省        : ").append(location.getProvince());
            sb.append("\n市        : ").append(location.getCity());
            //sb.append("\n城市编码   : ").append(location.getCityCode());
            //sb.append("\n区        : ").append(location.getDistrict());
            //sb.append("\n区域 码   : ").append(location.getAdCode());
            sb.append("\n地    址  : ").append(location.getAddress());
            //sb.append("\n地    址  : ").append(location.getDescription());
            //sb.append("\n兴趣点    : ").append(location.getPoiName());
            //sb.append("\n定位时间: " + CommonUtils.formatLocalTimeByMilSecond(location.getTime(), "yyyy-MM-dd HH:mm:ss"));

        } else { //定位失败
            sb.append("\n定位失败");
            sb.append("\n错误码:").append(location.getErrorCode());
            sb.append("\n错误信息:").append(location.getErrorInfo());
            sb.append("\n错误描述:").append(location.getLocationDetail());
        }
        sb.append("\n***定位质量报告***");
        sb.append("\n* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭");
        sb.append("\n* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus()));
        sb.append("\n* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites());
        sb.append("\n****************");

        Log.i("location", sb.toString());
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
        context = context.getApplicationContext();
        //初始化client
        locationClient = new AMapLocationClient(context);
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
        // 设置定位监听
        locationClient.setLocationListener(this);
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
        mOption.setInterval(GAO_DE_POSITION_FREQ);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(true);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
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
     * 开始定位
     * <p>
     * since: 2.8.0 <br/>
     * author wuguoxian <br/>
     * belong 高德地图SDK <br/>
     */
    public void startLocation(Context context) {
        Log.i(TAG, "startLocation > ");
        context = context.getApplicationContext();
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

    /**
     * 停止定位
     * <p>
     * since: 2.8.0 <br/>
     * author wuguoxian <br/>
     * belong 高德地图SDK <br/>
     */
    public void stopLocation() {
        if (isStartLocation())
            locationClient.stopLocation();
    }

    public boolean isStartLocation() {
        return locationClient.isStarted();
    }

    /**
     * action:注销位置获取
     * <p>
     * since: 2.8.0 <br/>
     * author wuguoxian <br/>
     * belong 高德地图SDK <br/>
     * date: 20200326 <br/>
     */
    public void destroyLocation() {
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
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
        if (Build.VERSION.SDK_INT >= 26) {
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

        if (Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }
}
