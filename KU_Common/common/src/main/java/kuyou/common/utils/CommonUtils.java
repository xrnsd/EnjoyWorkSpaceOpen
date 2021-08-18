package kuyou.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommonUtils {

    //protected final String TAG = this.getClass().getSimpleName();
    private static final String TAG = "CommonUtils";
    public final static boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);


    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.d("123456 Launcher.getStatusBarHeight>", " statusBarHeight =" + height);
        return height;
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null)
            return null;
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        if (bitmap == null)
            return null;

        return new BitmapDrawable(bitmap);
    }

    public static int str2Color(String color) {
        if (color == null)
            return -1;
        return Color.parseColor(color);
    }

    public static Drawable color2Drawable(String color) {
        if (color == null)
            return null;
        return color2Drawable(Color.parseColor(color));
    }

    public static Drawable color2Drawable(int color) {
        if (color <= 1)
            return null;
        return new ColorDrawable(color).getCurrent();
    }

    public static int dip2px(Context context, int dipValue) {
        return dip2px(context, (float) dipValue);
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int[] getScreenInfo(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return new int[]{(int) dm.widthPixels, (int) dm.heightPixels};
    }

    /**
     * action: 时间戳转UTC时间 <br/>
     * System.currentTimeMillis()获取的为UTC时间戳
     */
    public static String formatUTCTimeByMilSecond(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * action: 时间戳转设备本地时间 <br/>
     * System.currentTimeMillis()获取的为UTC时间戳
     */
    public static String formatLocalTimeByMilSecond(long timeInMillis, String pattern) {
        if (0 < timeInMillis)
            Calendar.getInstance().setTimeInMillis(timeInMillis);
        return new SimpleDateFormat(pattern).format(Calendar.getInstance().getTime());
    }

    /**
     * action: 将时间转换为时间戳 <br/>
     * System.currentTimeMillis()获取的为UTC时间戳
     */
    public static long formatDate2Stamp(String s, String pattern) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = simpleDateFormat.parse(s);
        return date.getTime();
    }

    public static boolean isContextExisted(Context context) {
        if (null == context) {
            return false;
        }
        if (context instanceof Activity) {
            return !((Activity) context).isFinishing();
        } else if (context instanceof Service) {
            return isServiceExisted(context, context.getClass().getName());
        }
        return context instanceof Application;
    }

    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;

            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

}
