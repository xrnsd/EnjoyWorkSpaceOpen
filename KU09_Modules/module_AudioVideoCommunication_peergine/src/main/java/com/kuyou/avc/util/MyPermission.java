package com.kuyou.avc.util;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MyPermission
{
    private int m_iReqCode = 33886462;
    private String[] m_sPermList = {};
    private String[] m_sTextList = {};
    private int m_iInd = 0;

    public void Request(Activity Ctx, String[] sPermList, String[] sTextList) {
        m_sPermList = sPermList;
        m_sTextList = sTextList;

        int iInd = 0;
        while (iInd < m_sPermList.length) {
            if (!RequestOne(Ctx, m_sPermList[iInd], m_sTextList[iInd])) {
                break;
            }
            iInd++;
        }
    }

    public void onResult(Activity Ctx, int iReqCode, String sPerm, int iGrantResults) {
        if (iReqCode != m_iReqCode) {
            return;
        }

        int iInd = 0;
        while (iInd < m_sPermList.length) {
            if (sPerm.equals(m_sPermList[iInd])) {
                break;
            }
            iInd++;
        }
        if (iInd >= m_sPermList.length) {
            return;
        }

        onResultOne(Ctx, m_sPermList[iInd], m_sTextList[iInd], iGrantResults);

        iInd++;
        if (iInd >= m_sPermList.length) {
            return;
        }

        while (iInd < m_sPermList.length) {
            if (!RequestOne(Ctx, m_sPermList[iInd], m_sTextList[iInd])) {
                break;
            }
            iInd++;
        }
    }

    private boolean RequestOne(Activity Ctx, String sPerm, String sText) {
        if (ContextCompat.checkSelfPermission(Ctx, sPerm) != PackageManager.PERMISSION_GRANTED) {
            // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
            // 向用户解释为什么需要这个权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(Ctx, sPerm)) {
                ActivityCompat.requestPermissions(Ctx, new String[]{sPerm}, m_iReqCode);
            }
            else {
                ActivityCompat.requestPermissions(Ctx, new String[]{sPerm}, m_iReqCode);
            }
            return false;
        }
        else {
            Toast.makeText(Ctx, (sText + "权限已申请"), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public void onResultOne(Activity Ctx, String sPerm, String sText, int iGrantResults) {
        if (iGrantResults == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(Ctx, (sText + "权限已申请"), Toast.LENGTH_SHORT).show();
        }
        else {
            //用户勾选了不再询问
            //提示用户手动打开权限
            if (!ActivityCompat.shouldShowRequestPermissionRationale(Ctx, sPerm)) {
                Toast.makeText(Ctx, (sText + "权限已被禁止，请在‘设置’里手动开启"), Toast.LENGTH_LONG).show();
            }
        }
    }
}
