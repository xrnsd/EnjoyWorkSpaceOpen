package com.kuyou.voicecontrol.sdk;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class Utils {
    private static final String TAG = "Utils";
    private static final Pattern pattern = Pattern.compile(".*<s>\\s*(.*)\\s*</s>.*\\n\\s*(.*)\\s*");

    public static void copyFile(Context context, String assetsFile, String destination) {
        InputStream is = null;
        FileOutputStream fos = null;
        byte[] buf1 = new byte[512];
        try {
            File des = new File(destination, assetsFile);
            if (des.exists()) {
                return;
            }
            if (!new File(destination).exists()) {
                Log.d(TAG, "mkdirs  " + new File(destination).mkdirs());
            }
            Log.d(TAG, "copy to: " + des.getAbsolutePath());
            fos = new FileOutputStream(des);
            is = context.getAssets().open(assetsFile);
            int readCount;
            while ((readCount = is.read(buf1)) > 0) {
                fos.write(buf1, 0, readCount);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "copy: ", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static StringBuffer getAssetsFile(Context context, String fileName) {
        StringBuffer sb = new StringBuffer(1024 * 10);
        try {
            InputStream in = context.getAssets().open(fileName);
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bufferReader = new BufferedReader(reader);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            in.close();
            return sb;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }


}
