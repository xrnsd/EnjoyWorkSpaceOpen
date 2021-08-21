package com.kuyou.tts.basic;

import android.content.Context;

public interface TTSConfig {
    public static final String APP_KEY = "wqmjrvwljeca5nrav5eazifpisdne23c3u2ipia6";
    public static final String SECRET = "f34e2280fe7dc09dfe48a9aef866e71f";

    //String sdPath = Environment.getExternalStorageDirectory().getPath();
    public static final String DIR_PATH_MODEL = "/sdcard/kuyou/tts";
    public static final String FILE_NAME_MODEL_FRONTEND = "frontend_model";
    public static final String FILE_NAME_MODEL_BACKEND = "backend_lzl";
    public static final String FILE_PATH_MODEL_FRONTEND = DIR_PATH_MODEL + "/" + FILE_NAME_MODEL_FRONTEND;
    public static final String FILE_PATH_MODEL_BACKEND = DIR_PATH_MODEL + "/" + FILE_NAME_MODEL_BACKEND;

    public void initModelFileConfig(Context context);
}
