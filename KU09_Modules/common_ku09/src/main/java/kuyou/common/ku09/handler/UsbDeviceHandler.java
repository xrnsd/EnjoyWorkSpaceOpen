package kuyou.common.ku09.handler;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * action :USB设备插拔监听
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-9 <br/>
 * </p>
 */
public class UsbDeviceHandler extends BroadcastReceiver {

    protected static final String TAG = "com.kuyou.rc.handler.hardware > UsbDeviceHandler";
    protected static final String ACTION_USB_DEVICE_PERMISSION = "com.android.example.USB_PERMISSION";

    private volatile static UsbDeviceHandler sInstance;

    private UsbDeviceHandler() {

    }

    public static UsbDeviceHandler getInstance() {
        if (sInstance == null) {
            synchronized (UsbDeviceHandler.class) {
                if (sInstance == null) {
                    sInstance = new UsbDeviceHandler();
                }
            }
        }
        return sInstance;
    }

    private UsbDevice mUsbDevice;
    private UsbManager mUsbManager;
    private IUsbDeviceListener mUsbDeviceListener;

    public static interface IUsbDeviceListener {
        public void onUsbDevice(UsbDevice device, boolean attached);
    }

    public UsbDeviceHandler setUsbDeviceListener(IUsbDeviceListener listener) {
        mUsbDeviceListener = listener;
        return UsbDeviceHandler.this;
    }

    protected void onUsbDevice(UsbDevice device, boolean attached) {
        Log.d(TAG, (attached ? "onUsbDevice > attached device = " : "onUsbDevice > detached device = ")
                + mUsbDevice.getProductName());
        if (null == mUsbDeviceListener) {
            Log.e(TAG, "dispatchEvent > process fail : mEventBusDispatchCallback is null");
            return;
        }
        mUsbDeviceListener.onUsbDevice(device, attached);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;
        String action = intent.getAction();
        if (TextUtils.isEmpty(action))
            return;
        if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
            mUsbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            onUsbDevice(mUsbDevice, true);
            //requestUserPermission(context,usbDevice);
        } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
            onUsbDevice(mUsbDevice, false);
            //设备拔下，资源释放
        } else if (action.equals(ACTION_USB_DEVICE_PERMISSION)) {
            //获取连接设备的权限
            boolean isGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
            if (isGranted) {
                //用户已授权
            } else {
                //用户未授权
            }
        }
    }

    private void requestUserPermission(Context context, UsbDevice usbDevice) {
        if (null == mUsbManager) {
            mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        }
        if (mUsbManager.hasPermission(usbDevice)) {
            return;
        }
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(ACTION_USB_DEVICE_PERMISSION), 0);
        mUsbManager.requestPermission(usbDevice, mPendingIntent);
    }

    public static UsbDeviceHandler register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_DEVICE_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.getApplicationContext().registerReceiver(getInstance(), filter);
        return getInstance();
    }

    public static void unregister(Context context) {
        context.getApplicationContext().unregisterReceiver(getInstance());
    }
}