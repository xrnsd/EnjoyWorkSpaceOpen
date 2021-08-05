package com.kuyou.rc.utils;

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
 * 上传文件到服务器
 *
 * @author tom
 */
public class UploadUtil {
    private static final String TAG = "kuyou.sdk.jt808 > UploadUtil";


    private static UploadUtil sMain;
    private OnUploadCallBack mOnUploadCallBack;

    private UploadUtil() {

    }

    public static UploadUtil getInstance() {
        if (null == sMain) {
            sMain = new UploadUtil();
        }
        return sMain;
    }

    public UploadUtil setOnUploadCallBack(OnUploadCallBack onUploadCallBack) {
        mOnUploadCallBack = onUploadCallBack;
        return UploadUtil.this;
    }

    private OnUploadCallBack getOnUploadCallBack() {
        return mOnUploadCallBack;
    }

    private void onUploadFinish(int resultCode) {
        if (null == mOnUploadCallBack) {
            Log.w(TAG, "onUploadFinish > mOnUploadImageListener is null");
            return;
        }
        mOnUploadCallBack.onUploadFinish(resultCode);
    }

    /**
     * 独立线程里面上传图片
     *
     * @throws IOException
     * @throws JSONException
     */
    public void uploadImageBySubThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    uploadImage();
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                    onUploadFinish(ResultCode.UPLOAD_FAIL);
                }
            }
        }).start();
    }

    /**
     * 上传图片
     *
     * @return 图片上传结果
     * @throws IOException
     * @throws JSONException
     */
    private JSONObject uploadImage() throws IOException {
        if (null == getOnUploadCallBack()) {
            Log.e(TAG, "uploadImage > process fail : OnUploadCallBack is null \n please platform method : setOnUploadCallBack");
            onUploadFinish(ResultCode.UPLOAD_CONFIG_INVALID);
            return null;
        }
        if (null == getOnUploadCallBack().getConfig()
                || getOnUploadCallBack().getConfig().isInvalid()) {
            Log.e(TAG, "uploadImage > process fail : OnUploadCallBack.getConfig is invalid");
            onUploadFinish(ResultCode.UPLOAD_CONFIG_INVALID);
            return null;
        }

        UploadConfig uploadConfig = getOnUploadCallBack().getConfig();
        String deviceId = uploadConfig.getStrDeviceId();
        String serverUrl = uploadConfig.getStrServerUrl();
        File fileImageLocal = uploadConfig.getFileImageLocal();
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
        int resultCode = -1;
        try {
            jsonObject = new JSONObject(response.body().string());
            resultCode = jsonObject.getInt("code");
            Log.d(TAG, jsonObject.toString());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        onUploadFinish(resultCode);
        return jsonObject;
    }

    protected String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static interface OnUploadCallBack {

        /**
         * 上传配置
         *
         * @return 上传用的配置信息
         */
        public UploadConfig getConfig();

        /**
         * action:上传结果
         * UPLOAD_SUCCESS = 0
         * UPLOAD_FAIL = 1
         * UPLOAD_CONFIG_INVALID = 2
         * */
        public void onUploadFinish(int resultCode);
    }

    public static interface ResultCode {
        public final static int UPLOAD_SUCCESS = 0;
        public final static int UPLOAD_FAIL = 2;
        public final static int UPLOAD_CONFIG_INVALID = 3;
    }

    /**
     * action :上传配置:服务器地址，文件路径，设备ID
     * <p>
     * author: wuguoxian <br/>
     * date: 20-10-24 <br/>
     * <p>
     */
    public static class UploadConfig {
        private String mStrServerUrl, mStrDeviceId;
        private File mFileImageLocal;

        public String getStrServerUrl() {
            return mStrServerUrl;
        }

        public UploadConfig setStrServerUrl(String strServerUrl) {
            mStrServerUrl = strServerUrl;
            return UploadConfig.this;
        }

        public File getFileImageLocal() {
            return mFileImageLocal;
        }

        public UploadConfig setFileImageLocal(File fileImageLocal) {
            mFileImageLocal = fileImageLocal;
            return UploadConfig.this;
        }

        public String getStrDeviceId() {
            return mStrDeviceId;
        }

        public UploadConfig setStrDeviceId(String strDeviceId) {
            this.mStrDeviceId = strDeviceId;
            return UploadConfig.this;
        }

        public boolean isInvalid() {
            return null == mStrServerUrl || null == mFileImageLocal || null == mStrDeviceId;
        }
    }
}