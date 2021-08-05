package kuyou.sdk.jt808.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 上传文件到服务器类
 *
 * @author tom
 */
public class UploadUtil {
    private static final String TAG = "kuyou.sdk.jt808 > UploadUtil";


    /**
     * 上传图片
     *
     * @param serverUrl
     * @param filePathImageLocal 图片路径
     * @return 新图片的路径
     * @throws IOException
     * @throws JSONException
     */
    public static void uploadImage(String serverUrl, File fileImageLocal, String deviceId, OnUploadImageListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    uploadImageBySubThread(serverUrl, fileImageLocal, deviceId, listener);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }).start();
    }

    /**
     * 上传图片
     *
     * @param serverUrl
     * @param filePathImageLocal 图片路径
     * @return 新图片的路径
     * @throws IOException
     * @throws JSONException
     */
    private static JSONObject uploadImageBySubThread(String serverUrl, File fileImageLocal, String deviceId, OnUploadImageListener listener) throws IOException {
        Log.d(TAG, " uploadImage > start upload img file : " + fileImageLocal.getPath());
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody image = RequestBody.create(MediaType.parse("image/jpeg"), fileImageLocal);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fileName", getFileNameNoEx(fileImageLocal.getName()))
                .addFormDataPart("deviceSn", deviceId)
                .addFormDataPart("multipartFile", fileImageLocal.getPath(), image)
                .build();
        Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response.body().string());
            Log.d(TAG, jsonObject.toString());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        if (null != listener)
            listener.onUploadFinish(jsonObject);
        return jsonObject;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static interface OnUploadImageListener {
        public void onUploadFinish(JSONObject jsonResult);
    }
}