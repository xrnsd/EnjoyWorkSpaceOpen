package com.kuyou.avc.ui.base;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.kuyou.avc.R;
import com.kuyou.avc.util.MyPermission;
import com.peergine.android.livemulti.pgLibLiveMultiCapture;
import com.peergine.android.livemulti.pgLibLiveMultiError;
import com.peergine.plugin.lib.pgLibJNINode;

import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;


public abstract class MultiCapture extends BaseAVCActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    protected final String TAG = "com.kuyou.avc.ui.custom >" + this.getClass().getSimpleName();

    private EditText m_editServer;
    private EditText m_editDevID;
    private Button m_btnStart;
    private Button m_btnStop;

    private EditText m_editMsg;
    private Button m_btnSend;

    private Button m_btnAccept;
    private Button m_btnRefuse;

    public static TextView m_sTransferInfo;
    public static TextView m_sStatusInfo;
    public static TextView m_sDebug;

    private String m_sReplyPeer = "";
    private String m_sReplyFile = "";
    private String m_sTransferPeer = "";
    private boolean m_bIsReplying = false;


    String m_sServerAddr = "connect.peergine.com:7781";
    String m_sDevID = "";

    protected pgLibLiveMultiCapture m_Live = new pgLibLiveMultiCapture();
    LinearLayout m_View = null;
    SurfaceView m_Wnd = null;

    private boolean m_bTransfering = false;

    public void setTransfering(boolean bTransfering) {
        m_btnRefuse.setEnabled(bTransfering);
        m_bTransfering = bTransfering;
    }

    public boolean getTransfering() {
        return m_bTransfering;
    }

    MyPermission m_myPerm = new MyPermission();

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        m_myPerm.onResult(this, requestCode, permissions[0], grantResults[0]);
    }

    private pgLibLiveMultiCapture.OnEventListener m_OnEvent = new pgLibLiveMultiCapture.OnEventListener() {

        @Override
        public void event(String sAct, String sData, String sRenID) {
            onPeergineEvent(sAct, sData, sRenID);
            Log.d(TAG, new StringBuilder()
                    .append("pgLibLiveMultiCapture.OnEventListener > event >")
                    .append("\nsAct=").append(sAct)
                    .append("\nsData=").append(sData)
                    .append("\nsRenID=").append(sRenID)
                    .toString());
            // TODO Auto-generated method stub
            if (sAct.equals("VideoStatus")) {
                // Video status report
            } else if (sAct.equals("Notify")) {
                // Receive the notify from capture side
                String sInfo = "Receive notify: data=" + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("RenderJoin")) {
                // Disable video and audio access for this Render id.
                //m_Live.RenderAccess(sRenID, false, false);

                // A render join
                String sInfo = "Render join: render=" + sRenID;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);

                int i = 0;
                while (true) {
                    String sRenID1 = m_Live.RenderEnum(i);
                    if (sRenID1.equals("")) {
                        break;
                    }
                    Log.d(TAG, "RenderEnum: Index=" + i + ", RenID=" + sRenID1);
                    i++;
                }
            } else if (sAct.equals("RenderLeave")) {
                // Enable video and audio access for this Render id.
                //m_Live.RenderAccess(sRenID, true, true);

                // A render leave
                String sInfo = "Render leave: render=" + sRenID;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);

                int i = 0;
                while (true) {
                    String sRenID1 = m_Live.RenderEnum(i);
                    if (sRenID1.equals("")) {
                        break;
                    }
                    Log.d(TAG, "RenderEnum: Index=" + i + ", RenID=" + sRenID1);
                    i++;
                }
            } else if (sAct.equals("Message")) {
                // Receive the message from render or capture
                String sInfo = "Receive msg: data=" + sData + ", render=" + sRenID;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);

                // Test message transfer delay stamp.
                if (sData.indexOf("stamp:") == 0) {
                    m_Live.MessageSend(sRenID, sData);
                }

                // Video and audio start and stop control.
                //LiveControl(sData);
            } else if (sAct.equals("Login")) {
                // Login reply
                if (sData.equals("0")) {
                    String sInfo = "Login success";
                    Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
                    onResult(IJT808ExtensionProtocol.RESULT_SUCCESS);
                } else {
                    if ("8".equals(sData)) {
                        onResult(IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
                    }
                    onResult(IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_SERVER_EXCEPTION);
                    String sInfo = "Login failed, error=" + sData;
                    Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
                }
            } else if (sAct.equals("Logout")) {
                // Logout
                String sInfo = "Logout";
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("Connect")) {
                // Connect to capture
                String sInfo = "Connect to capture";
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("Disconnect")) {
                // Disconnect from capture
                String sInfo = "Diconnect from capture";
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("Offline")) {
                // The capture is offline.
                String sInfo = "Capture offline";
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("RecordStopVideo")) {
                // Record stop video.
                String sInfo = "Record stop video: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("RecordStopAudio")) {
                // Record stop video.
                String sInfo = "Record stop audio: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("ForwardAllocReply")) {
                String sInfo = "Forward alloc relpy: error=" + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("ForwardFreeReply")) {
                String sInfo = "Forward free relpy: error=" + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("VideoCamera")) {
                onScreenshot(sData);
                String sInfo = "The picture is save to: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("FilePutRequest")) {
                FilePutRequest(sData, sRenID);
            } else if (sAct.equals("FileGetRequest")) {
                FileGetRequest(sData, sRenID);
            } else if (sAct.equals("FileAccept")) {
                String sInfo = "File accept: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("FileReject")) {
                FileReject();
            } else if (sAct.equals("FileAbort")) {
                // 取消 上传�?下载
                FileAbort();
            } else if (sAct.equals("FileFinish")) {
                // 文件传输完毕
                FileFinish();
            } else if (sAct.equals("FileProgress")) {
                FileProgress(sData);
            } else if (sAct.equals("SvrNotify")) {
                String sInfo = "Receive server notify: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            }

            Log.d(TAG, "OnEvent: Act=" + sAct + ", Data=" + sData
                    + ", Render=" + sRenID);
        }
    };


    private void FilePutRequest(String sDate, String sPeer) {
        String file = getcontent(sDate, "peerpath");

        m_sReplyFile = file;
        m_sReplyPeer = sPeer;
        m_bIsReplying = true;

        String info = "是否接受" + sPeer + "上传的文�? " + file + " ...";
        m_sTransferInfo.setText(info);

        m_btnAccept.setEnabled(true);
        m_btnRefuse.setEnabled(true);
    }

    private void FileGetRequest(String sDate, String sPeer) {
        String file = getcontent(sDate, "peerpath");

        m_sReplyFile = file;
        m_sReplyPeer = sPeer;
        m_bIsReplying = true;

        String info = "是否接受" + sPeer + "下载文件(" + file + ")的请�?...";
        m_sTransferInfo.setText(info);

        m_btnAccept.setEnabled(true);
        m_btnRefuse.setEnabled(true);
    }

    private void FileProgress(String sData) {
        String sPath = getcontent(sData, "path");
        String sReqSize = getcontent(sData, "total");
        String sCurSize = getcontent(sData, "position");
        m_sStatusInfo.setText("本地文件路径:" + sPath + " 已传输大�?" + sCurSize + "/文件总大�?" + sReqSize);
    }

    private void FileReject() {
        m_btnRefuse.setEnabled(false);
        m_btnAccept.setEnabled(false);
        setTransfering(false);
        m_sTransferInfo.setText("对方拒绝了文件传�?..");
    }

    private void FileAbort() {
        m_btnRefuse.setEnabled(false);
        m_btnAccept.setEnabled(false);
        setTransfering(false);
        m_sTransferInfo.setText("文件传输中断�?..");
    }

    private void FileFinish() {
        m_btnRefuse.setEnabled(false);
        setTransfering(false);
        m_sTransferInfo.setText("文件传输完毕...");
    }

    public static void outString(String str) {
        if (MultiCapture.m_sDebug.getText().length() > 600) {
            MultiCapture.m_sDebug.setText("");
        }

        MultiCapture.m_sDebug.setText(MultiCapture.m_sDebug.getText() + "  " + str);
    }

    private boolean CheckPlugin() {
        if (pgLibJNINode.Initialize(this)) {
            pgLibJNINode.Clean();
            return true;
        } else {
            //Alert("Error", "Please import 'pgPluginLib' peergine middle ware!");
            return false;
        }
    }

    @Override
    protected void initViews() {
        super.initViews();

        m_editServer = (EditText) findViewById(R.id.editServer);
        m_editDevID = (EditText) findViewById(R.id.editDevID);

        m_sTransferInfo = (TextView) findViewById(R.id.TransferInfo);
        m_sStatusInfo = (TextView) findViewById(R.id.StatusInfo);
        m_sTransferInfo.setText("您未进行任何操作!");
        m_sStatusInfo.setText("没有文件传输");
        m_sDebug = (TextView) findViewById(R.id.debug);

        m_btnStart = (Button) findViewById(R.id.btnStart);
        m_btnStart.setOnClickListener(m_OnClink);
        m_btnStop = (Button) findViewById(R.id.btnStop);
        m_btnStop.setOnClickListener(m_OnClink);

        m_editMsg = (EditText) findViewById(R.id.editMsg);
        m_btnSend = (Button) findViewById(R.id.btnSend);
        m_btnSend.setOnClickListener(m_OnClink);

        LinearLayout layoutGet = (LinearLayout) findViewById(R.id.layoutSelectBtn);
        m_btnAccept = (Button) layoutGet.findViewById(R.id.btnAccept);
        m_btnAccept.setOnClickListener(m_OnClink);
        m_btnRefuse = (Button) layoutGet.findViewById(R.id.btnRefuse);
        m_btnRefuse.setOnClickListener(m_OnClink);
        m_btnAccept.setEnabled(false);
        m_btnRefuse.setEnabled(false);

        if (!CheckPlugin()) {
            return;
        }

        m_editServer.setText(m_sServerAddr);
        m_editDevID.requestFocus();

        m_Live.SetEventListener(m_OnEvent);

        String[] sPermList = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        String[] sTextList = {"摄像头", "麦克风", "写存储"};
        m_myPerm.Request(this, sPermList, sTextList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        m_Live.LoginNow(0);
        Toast.makeText(MultiCapture.this, "Enter foreground, login now", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int iRotate = 90;
        int iRotateType = this.getWindowManager().getDefaultDisplay().getRotation();

        switch (iRotateType) {
            case Surface.ROTATION_0:
                iRotate += 0;
                break;

            case Surface.ROTATION_90:
                iRotate += -90;
                break;

            case Surface.ROTATION_180:
                iRotate += -180;
                break;

            case Surface.ROTATION_270:
                iRotate += -270;
                break;
        }

        iRotate = (iRotate + 360) % 360;

        SetRotate(iRotate);
    }

    public void SetRotate(int iAngle) {
        pgLibJNINode Node = m_Live.GetNode();
        if (Node != null) {
            if (Node.ObjectAdd("_vTemp", "PG_CLASS_Video", "", 0)) {
                Node.ObjectRequest("_vTemp", 2, "(Item){2}(Value){" + iAngle + "}", "");
                Node.ObjectDelete("_vTemp");
            }
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.d(TAG, "onKeyDown, KEYCODE_BACK");
            ExitDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private DialogInterface.OnClickListener m_DlgClick = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
                LiveStop();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    };

    public void ExitDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Confirm");
//        builder.setMessage("Are you sure to exit?");
//        builder.setPositiveButton("YES", m_DlgClick);
//        builder.setNegativeButton("NO", m_DlgClick);
//        builder.show();
    }

    public void Alert(String sTitle, String sMsg) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(sTitle);
//        builder.setMessage(sMsg);
//        builder.setPositiveButton("OK", null);
//        builder.show();
    }

    protected void liveStart() {
        if (m_Wnd != null) {
            Log.d(TAG, "liveStart > m_Wnd is not null");
            return;
        }
        Log.d(TAG, "liveStart > ");

        String sInitParam = "(Debug){1}";

        m_sDevID = getConfig().getDevCollectingEndId();
        int iErr = m_Live.Initialize(m_sDevID, "", getConfig().getServerAddress(), "", 3, sInitParam, this);
        if (iErr != 0) {
            Log.d(TAG, "LiveStart: Live.Initialize failed! iErr=" + iErr);
            onResult(IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_SERVER_EXCEPTION);
            return;
        }

        m_Wnd = (SurfaceView) m_Live.CameraViewGet();
        m_View = (LinearLayout) findViewById(R.id.layoutVideo);
        m_View.addView(m_Wnd);
        m_Wnd.setVisibility(View.GONE);

        if (getTypeCode() == IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO
                || getTypeCode() == IJT808ExtensionProtocol.MEDIA_TYPE_GROUP) {
            final String sVideoParam = "(Code){3}(Mode){3}(Rate){66}(BitRate){500}(MaxStream){3}(SendCache){1}";
            m_Live.VideoStart(0, sVideoParam, null);
        }

        if (getTypeCode() == IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO
                || getTypeCode() == IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO
                || getTypeCode() == IJT808ExtensionProtocol.MEDIA_TYPE_GROUP) {
            String sAudioParam = "";
            // String sAudioParam = "(AecConfig){1,-1,-1,-1,-1}"; // Low level aec.
            m_Live.AudioStart(0, sAudioParam);
        }
    }

    protected void LiveStop() {
        Log.d(TAG, "LiveStop > ");
//        m_Live.AudioStop(0);
//        m_Live.VideoStop(0);
//
//        if (m_Wnd != null) {
//            m_View.removeView(m_Wnd);
//            m_Live.CameraViewRelease();
//            m_View = null;
//            m_Wnd = null;
//            m_Live.Clean();
//        }
        if (m_Live == null) {
            return;
        }
        m_Live.AudioStop(0);
        m_Live.VideoStop(0);

        if (m_Wnd != null) {
            m_View.removeView(m_Wnd);
            m_Live.CameraViewRelease();
            m_View = null;
            m_Wnd = null;
        }
        m_Live.Clean();
    }

    private void LiveControl(String sData) {
        if (sData.equals("VideoStart")) {
            String sVideoParam = "(Code){3}(Mode){3}(Rate){66}(Portrait){1}(BitRate){500}(MaxStream){3}";
            m_Live.VideoStart(0, sVideoParam, null);
        } else if (sData.equals("VideoStop")) {
            m_Live.VideoStop(0);
        } else if (sData.equals("VideoHD")) {
            m_Live.VideoParam(0, "(Code){3}(Mode){10}(Rate){66}(BitRate){1000}");
        } else if (sData.equals("VideoSD")) {
            m_Live.VideoParam(0, "(Code){3}(Mode){3}(Rate){66}(BitRate){500}");
        } else if (sData.equals("AudioStart")) {
            String sAudioParam = "";
            m_Live.AudioStart(0, sAudioParam);
        } else if (sData.equals("AudioStop")) {
            m_Live.AudioStop(0);
        }
    }

    private View.OnClickListener m_OnClink = new View.OnClickListener() {
        // Control clicked
        public void onClick(View args0) {
            switch (args0.getId()) {
                case R.id.btnStart:
                    liveStart();

                    // For test ...
                    //RecordAudioBothStart("/sdcard/Download/capture.avi", 0);
                    break;

                case R.id.btnStop:
                    outString(getTransfering() + "" + m_bIsReplying + "");
                    if (getTransfering()) {
                        btnRefuse();
                        setTransfering(false);
                    }

                    // For test ...
                    //RecordAudioBothStop("/sdcard/Download/capture.avi", 0);
                    LiveStop();
                    break;

                case R.id.btnSend:
                    btnSend();
                    break;

                case R.id.btnAccept:
                    btnAccept();
                    break;

                case R.id.btnRefuse:
                    btnRefuse();
                    break;

                default:
                    break;
            }
        }

    };

    private void btnSend() {
        String sMsg = m_editMsg.getText().toString();
        m_Live.NotifySend(sMsg);

        // Just for test.
        if (sMsg.equals("videosource=0")) {
            m_Live.VideoParam(0, "(CameraNo){" + CameraInfo.CAMERA_FACING_BACK + "}");
        } else if (sMsg.equals("videosource=1")) {
            m_Live.VideoParam(0, "(CameraNo){" + CameraInfo.CAMERA_FACING_FRONT + "}");
        } else if (sMsg.equals("playbackpause")) {
            m_Live.VideoParam(0, "(Pause){1}");
        } else if (sMsg.equals("playbackcontinue")) {
            m_Live.VideoParam(0, "(Pause){0}");
        } else if (sMsg.equals("playbackseek")) {
            m_Live.VideoParam(0, "(Seek){300}");
        } else if (sMsg.equals("recstart")) {
            m_Live.RecordStart("Tag", "/sdcard/Download/capture.mp4", 0, 0);
        } else if (sMsg.equals("recstop")) {
            m_Live.RecordStop("Tag");
        } else if (sMsg.equals("videocamera")) {
            m_Live.VideoCamera(0, "/sdcard/Download/capture.jpg");
        }
    }

    private void btnAccept() {
        if (m_bIsReplying) {
            String sPath = "/sdcard/Download/pgtest";
            int iErr = m_Live.FileAccept(m_sReplyPeer, sPath);
            if (iErr != 0) {
                Log.d(TAG, "btnAccept: iErr = " + iErr);
            } else {
                m_bIsReplying = false;
                m_sTransferPeer = m_sReplyPeer;
                FileReplyStatus(m_sReplyPeer, m_sReplyFile, 0);
            }
        }
    }

    protected void btnRefuse() {
        int iErr = 0;
        if (m_bIsReplying) {
            iErr = m_Live.FileReject(m_sReplyPeer, pgLibLiveMultiError.PG_ERR_Reject);
        } else {
            iErr = m_Live.FileCancel(m_sTransferPeer);
        }

        if (iErr != 0) {
            Log.d(TAG, "btnRefuse: iErr = " + iErr);
        } else {
            m_bIsReplying = false;
            FileReplyStatus(m_sReplyPeer, m_sReplyFile, 1);
        }
    }

    private void FileReplyStatus(String sPeer, String sPeerPath, int iAction) {
        if (iAction != 1) {
            setTransfering(true);
            m_btnRefuse.setEnabled(true);
            m_btnAccept.setEnabled(false);
            String str = "您正在进行文件传�? " + sPeerPath + "...";
            m_sTransferInfo.setText(str);
        } else {
            setTransfering(false);
            m_btnAccept.setEnabled(false);
            m_btnRefuse.setEnabled(false);
            String str = "您拒绝了文件传输: " + sPeerPath + "...";
            m_sTransferInfo.setText(str);
        }
    }

    public String getcontent(String sData, String key) {
        String result = "";
        String[] values = sData.split("&");
        for (int i = 0; i < values.length; i++) {
            String[] entry = values[i].split("=");
            if (entry[0].equals(key)) {
                result = entry[1];
                break;
            }
        }
        return result;
    }

    private boolean setFlashlightEnabled(boolean isEnable) {
        pgLibJNINode Node = m_Live.GetNode();
        if (Node != null) {
            if (Node.ObjectAdd("_vTemp", "PG_CLASS_Video", "", 0)) {
                Node.ObjectRequest("_vTemp", 2, "(Item){14}(Value){" + (isEnable ? 1 : 0) + "}", "");
                Node.ObjectDelete("_vTemp");
                return true;
            }
        }
        return false;
    }

    public boolean SetAudioSuppress(int iDebug, int iDelay, int iKeep) {
        pgLibJNINode Node = m_Live.GetNode();
        if (Node != null) {
            if (Node.ObjectAdd("_AudioTemp", "PG_CLASS_Audio", "", 0)) {
                String sValue = "(Debug){" + iDebug + "}(Delay){" + iDelay + "}(Keep){" + iKeep + "}";
                Node.ObjectRequest("_AudioTemp", 2, "(Item){0}(Value){" + Node.omlEncode(sValue) + "}", "");
                Node.ObjectDelete("_AudioTemp");
                return true;
            }
        }
        return false;
    }

    public boolean SetVolumeGate(int iVolumeGate) {
        pgLibJNINode Node = m_Live.GetNode();
        if (Node != null) {
            if (Node.ObjectAdd("_aTemp", "PG_CLASS_Audio", "", 0)) {
                String sValue = Node.omlEncode("(TailLen){0}(VolGate){" + iVolumeGate + "}");
                Node.ObjectRequest("_aTemp", 2, "(Item){3}(Value){" + sValue + "}", "");
                Node.ObjectDelete("_aTemp");
                return true;
            }
        }
        return false;
    }

    public boolean SetMobileAec(int iEnable) {
        pgLibJNINode Node = m_Live.GetNode();
        if (Node != null) {
            if (Node.ObjectAdd("_aTemp", "PG_CLASS_Audio", "", 0)) {
                Node.ObjectRequest("_aTemp", 2, "(Item){11}(Value){" + iEnable + "}", "");
                Node.ObjectDelete("_aTemp");
                return true;
            }
        }
        return false;
    }

    //@{ added by wgx Usefulness:
    @Override
    protected void onResume() {
        super.onResume();
        liveStart();
    }

    @Override
    public void onDestroy() {
        LiveStop();
        super.onDestroy();
    }

    @Override
    public void exit() {
        outString(getTransfering() + "" + m_bIsReplying + "");
        if (getTransfering()) {
            btnRefuse();
            setTransfering(false);
        }

        // For test ...
        //RecordAudioBothStop("/sdcard/Download/capture.avi", 0);
        LiveStop();
        super.exit();
    }

    //}@ end wgx
}
