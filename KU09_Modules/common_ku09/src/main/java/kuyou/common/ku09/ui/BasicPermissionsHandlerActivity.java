package kuyou.common.ku09.ui;


import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import kuyou.common.ku09.R;
import kuyou.common.permission.PermissionUtil;

/**
 * action :需要进行运行时权限检测的Activity可以继承这个类
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-25 <br/>
 * <p>
 */
public abstract class BasePermissionsActivity extends BaseActivity {

    //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
    private boolean needCheckBackLocation = false;
    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    private static String BACKGROUND_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 当前需要动态申请的的权限列表
     */
    protected String[] getPermissions() {
        return null;
    }

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23
                && getApplicationInfo().targetSdkVersion >= 23) {
            if (isNeedCheck) {
                String[] needPermissions = getPermissions();
                if (null != needPermissions)
                    requestPermission(needPermissions);
            }
        }
    }

    /**
     * @param permissions
     * @since 2.5.0
     */
    protected void requestPermission(String... permissions) {
        Log.d(TAG, "requestPermission: ");
        try {
            if (Build.VERSION.SDK_INT >= 23
                    && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                    Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class,
                            int.class});
                    method.invoke(this, array, PERMISSON_REQUESTCODE);
                }
            }
        } catch (Throwable e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * 检测权限，如果返回true,有权限 false 无权限
     *
     * @param permission 权限
     * @return 是否有权限
     */
    public boolean checkSelfPermission(String... permission) {
        if (null == permission || permission.length <= 0) {
            return false;
        }
        return (findDeniedPermissions(permission)).isEmpty();
    }

    /**
     * 检测权限，如果返回true,有权限 false 无权限
     *
     * @return 是否有权限
     */
    public boolean checkSelfPermission() {
        if (null == getPermissions() || getPermissions().length <= 0) {
            return false;
        }
        for (String permission : getPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkSelfPermission: invalid permission = " + permission);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        Log.d(TAG, "findDeniedPermissions: ");
        List<String> needRequestPermissonList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23
                && getApplicationInfo().targetSdkVersion >= 23) {
            try {
                for (String perm : permissions) {
                    Method checkSelfMethod = getClass().getMethod("checkSelfPermission", String.class);
                    Method shouldShowRequestPermissionRationaleMethod = getClass().getMethod("shouldShowRequestPermissionRationale",
                            String.class);
                    if ((Integer) checkSelfMethod.invoke(this, perm) != PackageManager.PERMISSION_GRANTED
                            || (Boolean) shouldShowRequestPermissionRationaleMethod.invoke(this, perm)) {
                        if (!needCheckBackLocation
                                && BACKGROUND_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissonList.add(perm);
                    }
                }
            } catch (Throwable e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    protected boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
//        if (requestCode == PERMISSON_REQUESTCODE) {
//            if (!verifyPermissions(paramArrayOfInt)) {
//                showMissingPermissionDialog();
//                isNeedCheck = false;
//            }
//        }
        Log.d(TAG, " onRequestPermissionsResult ");
        if (PERMISSON_REQUESTCODE == requestCode) {
            isNeedCheck = false;
            if (verifyPermissions(paramArrayOfInt)) {
                onRequestPermissionsResultSuccess();
                return;
            }
            onRequestPermissionsResultFail();
        } else {
            Log.w(TAG, " onRequestPermissionsResult >requestCode is not REQUEST_PERMISSIONS_CODE");
        }
    }

    protected void onRequestPermissionsResultSuccess() {
        Log.d(TAG, " onRequestPermissionsResultSuccess > request Permissions success");

    }

    protected void onRequestPermissionsResultFail() {
        Log.e(TAG, " onRequestPermissionsResultFail > request Permissions fail");

    }

    protected void showMissingPermissionDialog() {
        Log.d(TAG, "showMissingPermissionDialog: ");
        PermissionUtil.showSureDialog(BasePermissionsActivity.this, getString(R.string.miss_permissions_config_title),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                        finish();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
    }

    protected void startAppSettings() {
        Log.d(TAG, "startAppSettings: ");
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}