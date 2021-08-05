package kuyou.common.ipc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 远程模块信息的的公共数据库
 */
public class RemoteModuleInfoDbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "remote_module_provider.db";
    public static final String MODULE_TABLE_NAME = "modulePackageName";

    private static final int DB_VERSION = 1;

    private String CREATE_BOOK_TABLE = "CREATE TABLE IF NOT EXISTS "
            + MODULE_TABLE_NAME + "(_id INTEGER PRIMARY KEY, name TEXT)";

    public RemoteModuleInfoDbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
