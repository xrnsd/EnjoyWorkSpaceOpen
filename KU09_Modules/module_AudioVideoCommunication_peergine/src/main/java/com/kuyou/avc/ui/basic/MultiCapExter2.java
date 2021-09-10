package com.kuyou.avc.ui.basic;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.kuyou.avc.R;
import com.kuyou.avc.ui.thermal.CameraView;
import com.peergine.android.livemulti.pgLibLiveMultiCapture;
import com.peergine.plugin.android.pgDevAudioOut;
import com.peergine.plugin.android.pgDevVideoIn;
import com.peergine.plugin.lib.pgLibJNINode;

import kuyou.common.ku09.protocol.basic.IJT808ExtensionProtocol;

public abstract class MultiCapExter2 extends AVCActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private boolean m_bIsReplying = false;

    String m_sDevID = "";

     pgLibLiveMultiCapture m_Live = new pgLibLiveMultiCapture();
    LinearLayout m_View = null;
    CameraView m_CameraView = null;

    private boolean m_bExtAudioOut = true;  // If play audio output at external p2p SDK.
    private boolean m_bExtVideoIn = true; // If Capture video input at external p2p SDK.

    private int m_iVideoMode = 2;
    private String m_sVideoParamHD = "(Code){3}(Mode){10}(Rate){66}(BitRate){1000}(Delay){500}(SendCache){1}";
    private String m_sVideoParamSD = "(Code){3}(Mode){3}(Rate){66}(BitRate){500}(Delay){300}(SendCache){1}";
    private String m_sVideoParamQD = "(Code){3}(Mode){2}(Rate){66}(BitRate){300}(Delay){200}(SendCache){1}";

    //private MyPermission m_myPerm = new MyPermission();

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //m_myPerm.onResult(this, requestCode, permissions[0], grantResults[0]);
    }

    private pgLibLiveMultiCapture.OnEventListener m_OnEvent = new pgLibLiveMultiCapture.OnEventListener() {

        @Override
        public void event(String sAct, String sData, String sRenID) {
            // TODO Auto-generated method stub
            if (sAct.equals("VideoStatus")) {
                // Video status report
            } else if (sAct.equals("Notify")) {
                // Receive the notify from capture side
                String sInfo = "Receive notify: data=" + sData;
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("RenderJoin")) {
                // A render join
                String sInfo = "Render join: render=" + sRenID;
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();

                int i = 0;
                while (true) {
                    String sRenID1 = m_Live.RenderEnum(i);
                    if (sRenID1.equals("")) {
                        break;
                    }
                    Log.d("pgLiveCapture", "RenderEnum: Index=" + i + ", RenID=" + sRenID1);
                    i++;
                }
            } else if (sAct.equals("RenderLeave")) {
                // A render leave
                String sInfo = "Render leave: render=" + sRenID;
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();

                int i = 0;
                while (true) {
                    String sRenID1 = m_Live.RenderEnum(i);
                    if (sRenID1.equals("")) {
                        break;
                    }
                    Log.d("pgLiveCapture", "RenderEnum: Index=" + i + ", RenID=" + sRenID1);
                    i++;
                }
            } else if (sAct.equals("Message")) {
                // Receive the message from render or capture
                String sInfo = "Receive msg: data=" + sData + ", render=" + sRenID;
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();

                LiveControl(sData);
            } else if (sAct.equals("Login")) {
                // Login reply
                if (sData.equals("0")) {
                    String sInfo = "Login success";
                    Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
                } else {
                    String sInfo = "Login failed, error=" + sData;
                    Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
                }
            } else if (sAct.equals("Logout")) {
                // Logout
                String sInfo = "Logout";
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("Connect")) {
                // Connect to capture
                String sInfo = "Connect to capture";
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("Disconnect")) {
                // Disconnect from capture
                String sInfo = "Diconnect from capture";
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("Offline")) {
                // The capture is offline.
                String sInfo = "Capture offline";
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("LanScanResult")) {
                // Lan scan result.
                String sInfo = "Lan scan result: " + sData;
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("ForwardAllocReply")) {
                String sInfo = "Forward alloc relpy: error=" + sData;
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("ForwardFreeReply")) {
                String sInfo = "Forward free relpy: error=" + sData;
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("VideoCamera")) {
                String sInfo = "The picture is save to: " + sData;
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("FilePutRequest")) {
                //FilePutRequest(sData, sRenID);
            } else if (sAct.equals("FileGetRequest")) {
                //FileGetRequest(sData, sRenID);
            } else if (sAct.equals("FileAccept")) {
                String sInfo = "File accept: " + sData;
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("FileReject")) {
                //FileReject();
            } else if (sAct.equals("FileAbort")) {
                // 取消 上传或下载
                //FileAbort();
            } else if (sAct.equals("FileFinish")) {
                // 文件传输完毕
                //FileFinish();
            } else if (sAct.equals("FileProgress")) {
                //FileProgress(sData);
            } else if (sAct.equals("SvrNotify")) {
                String sInfo = "Receive server notify: " + sData;
                Toast.makeText(MultiCapExter2.this, sInfo, Toast.LENGTH_SHORT).show();
            }

            Log.d("pgLiveMultiCapture", "OnEvent: Act=" + sAct + ", Data=" + sData
                    + ", Render=" + sRenID);
        }
    };

    public pgDevVideoIn.OnCallback m_oVideoInCB = new pgDevVideoIn.OnCallback() {

        @Override
        public int Open(int iDevNO, int iPixBytes, int iWidth, int iHeight,
                        int iBitRate, int iFrmRate, int iKeyFrmRate) {
            // TODO Auto-generated method stub
            Log.d("pgLiveCapExter", "pgDevVideoIn.OnCallback.Open");

            // Enable assem h264/h265 frames in SDK.
            //pgDevVideoIn.SetParam(iDevNO, pgDevVideoIn.PG_DEV_VIDEO_IN_PARAM_ASSEM_FRAME, 1);

            // The iDevID is '1234'.
            int iDevID = 1234;
            if (!m_CameraView.Start(iDevID, iDevNO, iWidth, iHeight, iBitRate, iFrmRate, iKeyFrmRate)) {
                return -1;
            }

            return iDevID;
        }

        @Override
        public void Close(int iDevID) {
            // TODO Auto-generated method stub

            m_CameraView.Stop();
            Log.d("pgLiveCapExter", "pgDevVideoIn.OnCallback.Close");
        }

        @Override
        public void Ctrl(int iDevID, int iCtrl, int iParam) {
            // TODO Auto-generated method stub
            Log.d("pgLiveCapExter", "pgDevVideoIn.OnCallback.Ctrl");

            m_CameraView.Ctrl(iDevID, iCtrl, iParam);
        }
    };

    private boolean CheckPlugin() {
        if (pgLibJNINode.Initialize(this)) {
            pgLibJNINode.Clean();
            return true;
        } else {
            Log.e(TAG, "Please import 'pgPluginLib' peergine middle ware!");
            return false;
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        if (!CheckPlugin()) {
            onResult(IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
            return;
        }
        m_Live.SetEventListener(m_OnEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        liveStart();
    }

    @Override
    public void onDestroy() {
        liveStop();
        super.onDestroy();
    }

    @Override
    public void exit() {
        liveStop();
        super.exit();
    }

    protected abstract View getPreviewView();

    protected void liveStart() {
        String sServerAddr = getConfig().getServerAddress();

        String sInitParam = "(Debug){1}";
        if (m_bExtAudioOut) {
            sInitParam += "(AudioOutExternal){1}";
        }

        if (m_bExtVideoIn) {
            sInitParam += "(VideoInExternal){1}";
        }

        m_sDevID = getConfig().getDevCollectingEndId();
        int iErr = m_Live.Initialize(m_sDevID, "", sServerAddr, "", 3, sInitParam, this);
        if (iErr != 0) {
            Log.d("pgLiveMultiCapture", "LiveStart: Live.Initialize failed! iErr=" + iErr);
            Log.e(TAG, "LiveStart: Live.Initialize failed! iErr=" + iErr);
            return;
        }

        m_View = (LinearLayout) findViewById(R.id.layoutVideo);

        // vertical
        boolean bVertical = true;

        if (m_bExtVideoIn) {
            pgDevVideoIn.SetCallback(m_oVideoInCB);
            m_View.addView(getPreviewView());
        }

        //SetVolumeGate(1);
        //SetMobileAec(0);

        // (Code){3}: H.264
        // (Mode){3}: 640 x 480
        // (Rate){66}: 1000 / 66 = 15fps
        // (BitRate){600}: 600Kbps

        String sVideoParam = "";
        if (m_iVideoMode == 10) {
            sVideoParam = m_sVideoParamHD;
        } else if (m_iVideoMode == 3) {
            sVideoParam = m_sVideoParamSD;
        } else if (m_iVideoMode == 2) {
            sVideoParam = m_sVideoParamQD;
        }

        if (bVertical) {
            sVideoParam += "(Portrait){1}";
        }

        m_Live.VideoStart(0, sVideoParam, null);
    }

    protected void liveStop() {
        m_Live.VideoStop(0);

        if (m_bExtVideoIn) {
            m_View.removeView(getPreviewView());
            pgDevAudioOut.SetCallback(null);
            pgDevVideoIn.SetCallback(null);
            m_Live.Clean();
        }
    }

    private void LiveControl(String sData) {
        if (sData.equals("VideoStart")) {
            String sVideoParam = "";
            if (m_iVideoMode == 10) {
                sVideoParam = m_sVideoParamHD;
            } else if (m_iVideoMode == 3) {
                sVideoParam = m_sVideoParamSD;
            } else if (m_iVideoMode == 2) {
                sVideoParam = m_sVideoParamQD;
            }
            m_Live.VideoStart(0, sVideoParam, null);
        } else if (sData.equals("VideoStop")) {
            m_Live.VideoStop(0);
        } else if (sData.equals("VideoHD")) {
            m_iVideoMode = 10;
            m_Live.VideoParam(0, m_sVideoParamHD);
        } else if (sData.equals("VideoSD")) {
            m_iVideoMode = 3;
            m_Live.VideoParam(0, m_sVideoParamSD);
        } else if (sData.equals("VideoQD")) {
            m_iVideoMode = 2;
            m_Live.VideoParam(0, m_sVideoParamQD);
        } else if (sData.equals("AudioStart")) {
            String sAudioParam = "";
            m_Live.AudioStart(0, sAudioParam);
        } else if (sData.equals("AudioStop")) {
            m_Live.AudioStop(0);
        }
    }
    //@{ added by wgx Usefulness:
    @Override
    public void recover() {
        super.recover();
        if (null!=m_Live) {
            m_Live.LoginNow(0);
        }
    }
    //}@ end wgx
}
