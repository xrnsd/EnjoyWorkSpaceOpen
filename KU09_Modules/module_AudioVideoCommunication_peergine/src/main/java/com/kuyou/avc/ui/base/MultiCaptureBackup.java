package com.kuyou.avc.ui.base;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.kuyou.avc.R;
import com.kuyou.avc.util.MyPermission;
import com.peergine.android.livemulti.pgLibLiveMultiCapture;
import com.peergine.android.livemulti.pgLibLiveMultiError;
import com.peergine.plugin.lib.pgLibJNINode;


public class MultiCaptureBackup extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private android.widget.EditText m_editServer;
    private android.widget.EditText m_editDevID;
    private android.widget.Button m_btnStart;
    private android.widget.Button m_btnStop;

    private android.widget.EditText m_editMsg;
    private android.widget.Button m_btnSend;

    private android.widget.Button m_btnAccept;
    private android.widget.Button m_btnRefuse;

    public static android.widget.TextView m_sTransferInfo;
    public static android.widget.TextView m_sStatusInfo;
    public static android.widget.TextView m_sDebug;

    private String m_sReplyPeer = "";
    private String m_sReplyFile = "";
    private String m_sTransferPeer = "";
    private boolean m_bIsReplying = false;


    String m_sServerAddr = "connect.peergine.com:7781";
    String m_sDevID = "";

    pgLibLiveMultiCapture m_Live = new pgLibLiveMultiCapture();
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
            // TODO Auto-generated method stub
            if (sAct.equals("VideoStatus")) {
                // Video status report
            } else if (sAct.equals("Notify")) {
                // Receive the notify from capture side
                String sInfo = "Receive notify: data=" + sData;
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("RenderJoin")) {
                // Disable video and audio access for this Render id.
                //m_Live.RenderAccess(sRenID, false, false);

                // A render join
                String sInfo = "Render join: render=" + sRenID;
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();

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
                // Enable video and audio access for this Render id.
                //m_Live.RenderAccess(sRenID, true, true);

                // A render leave
                String sInfo = "Render leave: render=" + sRenID;
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();

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
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();

                // Test message transfer delay stamp.
                if (sData.indexOf("stamp:") == 0) {
                    m_Live.MessageSend(sRenID, sData);
                }

				/*
				// Receive play request, enable video and audio access for this Render id.
				if (sData.equals("PlayRequest")) {
					m_Live.RenderAccess(sRenID, true, true);
					m_Live.MessageSend(sRenID, "PlayReply");
				}
				*/

                // Video and audio start and stop control.
                //LiveControl(sData);
            } else if (sAct.equals("Login")) {
                // Login reply
                if (sData.equals("0")) {
                    String sInfo = "Login success";
                    Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
                } else {
                    String sInfo = "Login failed, error=" + sData;
                    Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
                }
            } else if (sAct.equals("Logout")) {
                // Logout
                String sInfo = "Logout";
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("Connect")) {
                // Connect to capture
                String sInfo = "Connect to capture";
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("Disconnect")) {
                // Disconnect from capture
                String sInfo = "Diconnect from capture";
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("Offline")) {
                // The capture is offline.
                String sInfo = "Capture offline";
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("RecordStopVideo")) {
                // Record stop video.
                String sInfo = "Record stop video: " + sData;
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("RecordStopAudio")) {
                // Record stop video.
                String sInfo = "Record stop audio: " + sData;
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("ForwardAllocReply")) {
                String sInfo = "Forward alloc relpy: error=" + sData;
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("ForwardFreeReply")) {
                String sInfo = "Forward free relpy: error=" + sData;
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("VideoCamera")) {
                String sInfo = "The picture is save to: " + sData;
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("FilePutRequest")) {
                FilePutRequest(sData, sRenID);
            } else if (sAct.equals("FileGetRequest")) {
                FileGetRequest(sData, sRenID);
            } else if (sAct.equals("FileAccept")) {
                String sInfo = "File accept: " + sData;
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MultiCaptureBackup.this, sInfo, Toast.LENGTH_SHORT).show();
            }

            Log.d("pgLiveMultiCapture", "OnEvent: Act=" + sAct + ", Data=" + sData
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
        if (MultiCaptureBackup.m_sDebug.getText().length() > 600) {
            MultiCaptureBackup.m_sDebug.setText("");
        }

        MultiCaptureBackup.m_sDebug.setText(MultiCaptureBackup.m_sDebug.getText() + "  " + str);
    }

    private boolean CheckPlugin() {
        if (pgLibJNINode.Initialize(this)) {
            pgLibJNINode.Clean();
            return true;
        } else {
            Alert("Error", "Please import 'pgPluginLib' peergine middle ware!");
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_capture);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // LinearLayout layoutinfo = (LinearLayout)
        // findViewById(R.id.layoutTransferInfo);
        m_editServer = (android.widget.EditText) findViewById(R.id.editServer);
        m_editDevID = (android.widget.EditText) findViewById(R.id.editDevID);

        m_sTransferInfo = (android.widget.TextView) findViewById(R.id.TransferInfo);
        m_sStatusInfo = (android.widget.TextView) findViewById(R.id.StatusInfo);
        m_sTransferInfo.setText("您未进行任何操作!");
        m_sStatusInfo.setText("没有文件传输");
        m_sDebug = (android.widget.TextView) findViewById(R.id.debug);

        m_btnStart = (android.widget.Button) findViewById(R.id.btnStart);
        m_btnStart.setOnClickListener(m_OnClink);
        m_btnStop = (android.widget.Button) findViewById(R.id.btnStop);
        m_btnStop.setOnClickListener(m_OnClink);

        m_editMsg = (android.widget.EditText) findViewById(R.id.editMsg);
        m_btnSend = (android.widget.Button) findViewById(R.id.btnSend);
        m_btnSend.setOnClickListener(m_OnClink);

        LinearLayout layoutGet = (LinearLayout) findViewById(R.id.layoutSelectBtn);
        m_btnAccept = (android.widget.Button) layoutGet.findViewById(R.id.btnAccept);
        m_btnAccept.setOnClickListener(m_OnClink);
        m_btnRefuse = (android.widget.Button) layoutGet.findViewById(R.id.btnRefuse);
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

    public void onDestroy() {
        LiveStop();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        m_Live.LoginNow(0);
        Toast.makeText(MultiCaptureBackup.this, "Enter foreground, login now", Toast.LENGTH_SHORT).show();
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
            Log.d("pgLiveCapture", "onKeyDown, KEYCODE_BACK");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure to exit?");
        builder.setPositiveButton("YES", m_DlgClick);
        builder.setNegativeButton("NO", m_DlgClick);
        builder.show();
    }

    public void Alert(String sTitle, String sMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(sTitle);
        builder.setMessage(sMsg);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void LiveStart() {
        if (m_Wnd != null) {
            return;
        }

        String sServerAddr = m_editServer.getText().toString();
        if (sServerAddr.equals("")) {
            Alert("Error", "LiveStart: The server address is empty!");
            return;
        }

        String sInitParam = "(Debug){1}";

        m_sDevID = m_editDevID.getText().toString();
        int iErr = m_Live.Initialize(m_sDevID, "", sServerAddr, "", 3, sInitParam, this);
        if (iErr != 0) {
            Log.d("pgLiveMultiCapture", "LiveStart: Live.Initialize failed! iErr=" + iErr);
            Alert("Error", "LiveStart: Live.Initialize failed! iErr=" + iErr);
            return;
        }

        m_Wnd = (SurfaceView) m_Live.CameraViewGet();
        m_View = (LinearLayout) findViewById(R.id.layoutVideo);
        m_View.addView(m_Wnd);
        m_Wnd.setVisibility(View.GONE);

        //SetVolumeGate(1);
        //SetMobileAec(0);

        String sVideoParam = "(Code){3}(Mode){3}(Rate){66}(Portrait){1}(BitRate){500}(MaxStream){3}(SendCache){1}";
        m_Live.VideoStart(0, sVideoParam, null);

		/* Playback media file.
		String sVideoParam = "(Playback){1}(Path){/sdcard/Download/capture.mp4}(SendCache){1}";
		m_Live.VideoStart(0, sVideoParam, null);
		*/

        String sAudioParam = "";
        // String sAudioParam = "(AecConfig){1,-1,-1,-1,-1}"; // Low level aec.
        m_Live.AudioStart(0, sAudioParam);
    }

    private void LiveStop() {
        m_Live.AudioStop(0);
        m_Live.VideoStop(0);

        if (m_Wnd != null) {
            m_View.removeView(m_Wnd);
            m_Live.CameraViewRelease();
            m_View = null;
            m_Wnd = null;
            m_Live.Clean();
        }
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
                    LiveStart();

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
                Log.d("pgLiveMultiCapture", "btnAccept: iErr = " + iErr);
            } else {
                m_bIsReplying = false;
                m_sTransferPeer = m_sReplyPeer;
                FileReplyStatus(m_sReplyPeer, m_sReplyFile, 0);
            }
        }
    }

    private void btnRefuse() {
        int iErr = 0;
        if (m_bIsReplying) {
            iErr = m_Live.FileReject(m_sReplyPeer, pgLibLiveMultiError.PG_ERR_Reject);
        } else {
            iErr = m_Live.FileCancel(m_sTransferPeer);
        }

        if (iErr != 0) {
            Log.d("pgLiveMultiCapture", "btnRefuse: iErr = " + iErr);
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

	/* for test.
	public boolean RecordAudioBothStart(String sAviPath, int iVideoID) {
		pgLibJNINode Node = m_Live.GetNode();
		if (Node != null) {
			if (Node.ObjectAdd("_aTemp", "PG_CLASS_Audio", "", 0)) {
				int iHasVideo = (iVideoID < 0) ? 0 : 1;
				String sData = "(Path){" + Node.omlEncode(sAviPath) + "}(Action){1}(MicNo){65535}(SpeakerNo){65535}(HasVideo){" + iHasVideo + "}";
				int iErr = Node.ObjectRequest("_aTemp", 38, sData, "");
				if (iErr > pgLibLiveMultiError.PG_ERR_Normal) {
					Log.d("pgLiveCapture", "RecordBothStart, iErr=" + iErr);
				}
				Node.ObjectDelete("_aTemp");
			}

			if (iVideoID >= 0) {
				String sData = "(Path){" + Node.omlEncode(sAviPath) + "}(HasAudio){1}";
				String sObjLive = "Live_" + m_Live.GetSelfPeer().substring(5) + "_" + iVideoID;
				int iErr = Node.ObjectRequest(sObjLive,
					36, sData, "pgLibLiveMultiCapture.RecordStartVideo");
				if (iErr > pgLibLiveMultiError.PG_ERR_Normal) {
					Log.d("pgLiveCapture", "RecordStartVideo: iErr=" + iErr);
				}
			}
		}
		return false;		
	}
	
	public void RecordAudioBothStop(String sAviPath, int iVideoID) {
		pgLibJNINode Node = m_Live.GetNode();
		if (Node != null) {
			if (iVideoID >= 0) {
				String sObjLive = "Live_" + m_Live.GetSelfPeer().substring(5) + "_" + iVideoID;
				int iErr = Node.ObjectRequest(sObjLive,
					36, "(Path){}", "pgLibLiveMultiCapture.RecordStopVideo");
				if (iErr > pgLibLiveMultiError.PG_ERR_Normal) {
					Log.d("pgLiveCapture", "RecordStopVideo: iErr=" + iErr);
				}
			}

			if (Node.ObjectAdd("_aTemp", "PG_CLASS_Audio", "", 0)) {
				String sData = "(Path){" + Node.omlEncode(sAviPath) + "}(Action){0}";
				int iErr = Node.ObjectRequest("_aTemp", 38, sData, "");
				if (iErr > pgLibLiveMultiError.PG_ERR_Normal) {
					Log.d("pgLiveCapture", "RecordBothStop, iErr=" + iErr);
				}
				Node.ObjectDelete("_aTemp");
			}
		}
	}
	*/
}
