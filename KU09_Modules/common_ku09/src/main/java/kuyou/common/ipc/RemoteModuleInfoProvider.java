package kuyou.common.ipc;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 书籍提供者
 * <p/>
 * Created by wangchenlong on 16/6/14.
 */
public class RemoteModuleInfoProvider extends ContentProvider {

    private static final String TAG = "kuyou.common.ipc > RemoteModuleInfoProvider";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String AUTHORITY = "kuyou.common.ipc.remote.module.provider"; // 与AndroidManifest保持一致
    public static final Uri MODULE_PACKAGE_NAME_CONTENT_URI = Uri.parse(
            new StringBuilder("content://").append(AUTHORITY).append("/modulePackageName").toString());

    public static final int MODULE_PACKAGE_NAME_URI_CODE = 0;

    // 关联Uri和Uri_Code
    static {
        sUriMatcher.addURI(AUTHORITY, "modulePackageName", MODULE_PACKAGE_NAME_URI_CODE);
    }

    private Context mContext;
    private SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "当前线程:" + Thread.currentThread().getName());
        mContext = getContext();

        initProviderData(); // 初始化Provider数据

        return false;
    }

    private void initProviderData() {
        mDb = new RemoteModuleInfoDbOpenHelper(mContext).getWritableDatabase();
        //mDb.execSQL("delete from " + HelmetModuleDbOpenHelper.MODULE_TABLE_NAME);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query > 当前线程: " + Thread.currentThread().getName());
        String tableName = getTableName(uri);
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return mDb.query(tableName, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert");
        String table = getTableName(uri);
        if (TextUtils.isEmpty(table)) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        mDb.insert(table, null, values);

        // 插入数据后通知改变
        mContext.getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete");

        String table = getTableName(uri);
        if (TextUtils.isEmpty(table)) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int count = mDb.delete(table, selection, selectionArgs);
        if (count > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }

        return count; // 返回删除的函数
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update");

        String table = getTableName(uri);
        if (TextUtils.isEmpty(table)) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int row = mDb.update(table, values, selection, selectionArgs);
        if (row > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }

        return row; // 返回更新的行数
    }

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case MODULE_PACKAGE_NAME_URI_CODE:
                tableName = RemoteModuleInfoDbOpenHelper.MODULE_TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;
    }

    public static void addModulePackageName(Context context) {
        List<String> list = getAllModulePackageName(context, true);
        if (null == list) {
            Log.w(TAG, "addModulePackageName > process fail : getAllModulePackageName list is null");
            return;
        }
        if (list.contains(context.getPackageName())) {
            Log.w(TAG, "addModulePackageName > process fail : module package name already exists");
            return;
        }
        ContentValues values = new ContentValues();
        values.put("name", context.getPackageName());
        context.getContentResolver().insert(MODULE_PACKAGE_NAME_CONTENT_URI, values);
    }

    public static List<String> getAllModulePackageName(Context context, boolean isIncludeYourself) {
        Cursor cursor = context.getContentResolver().query(MODULE_PACKAGE_NAME_CONTENT_URI,
                new String[]{"_id", "name"}, null, null, null);
        if (null == cursor) {
            Log.e(TAG, "getAllModulePackageName > process fail : cursor is null");
            return null;
        }
        String packageName = null;
        List<String> packageNameList = new ArrayList<>();
        while (cursor.moveToNext()) {
            packageName = cursor.getString(1);
            Log.d(TAG, "find packageName: " + packageName);
            if (context.getPackageName().equals(packageName) && !isIncludeYourself) {
                continue;
            }
            if (packageNameList.contains(packageName)) {
                continue;
            }
            packageNameList.add(packageName);
        }
        cursor.close();
        return packageNameList;
    }
}
