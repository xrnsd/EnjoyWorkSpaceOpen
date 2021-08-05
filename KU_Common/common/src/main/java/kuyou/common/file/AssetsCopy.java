package kuyou.common.file;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-6 <br/>
 * <p>
 */
public class AssetsCopy {
    private static final String TAG = "AssetsCopy";

    public static void copy(Context context, String assetFileName, String dirPathStorage) {
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(assetFileName);

            String sdPath = Environment.getExternalStorageDirectory().getPath();
            FileOutputStream fos = new FileOutputStream(new StringBuilder()
                    .append(dirPathStorage)
                    .append("/")
                    .append(assetFileName).toString());


            byte[] buff = new byte[512];
            int count = is.read(buff);
            while (count != -1) {
                fos.write(buff);
                count = is.read(buff);
            }
            is.close();
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
