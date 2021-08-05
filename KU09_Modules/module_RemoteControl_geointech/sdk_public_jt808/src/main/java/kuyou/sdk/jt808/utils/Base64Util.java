package kuyou.sdk.jt808.utils;

import android.util.Log;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-4 <br/>
 * <p>
 */
public class Base64Util {
    private static final String TAG = "kuyou.sdk.jt808 > Base64Util";

    //解密密钥(自行随机生成)
    public static final String KEY = "DOORSENSOR123456";//密钥key

    public static byte[] encrypt(String sSrc) {
        try {
            byte[] raw = KEY.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

            return Base64.getEncoder().encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

}
