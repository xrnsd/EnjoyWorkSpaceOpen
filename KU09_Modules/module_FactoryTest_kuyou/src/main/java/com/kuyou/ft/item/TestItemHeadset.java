package com.kuyou.ft.item;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;
import com.kuyou.ft.util.Recorder;

public class TestItemHeadset extends TestItemBasic {

    private int mStatus = 0;
    private File mFileAudio, mFileRecAudio;
    private MediaPlayer mMediaPlayer;
    private MediaRecorder mMediaRecorderRec = null;
    private Recorder mRecorder;
    private Button mBtnHeadsetStatus;

    @Override
    public int getTestPolicy() {
        int policy = 0;
        policy |= POLICY_TEST;
        policy |= POLICY_TEST_AUTO;
        return policy;
    }

    @Override
    public int getTestId() {
        return R.id.test_headset;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_headset;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_headset);
    }

    private final BroadcastReceiver mBroadcastReceiverHeadset = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            if (paramIntent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
                if (paramIntent.getIntExtra("state", 0) == 1) {
                    Log.d(TAG, "this is headphone plugged");
                    mBtnHeadsetStatus.setText(R.string.start_luyin);
                    mStatus = 1;
                } else {
                    Log.d(TAG, "this is headphone unplugged");
                    mBtnHeadsetStatus.setText(R.string.insert_headset);
                    mStatus = 0;
                }
            }
        }
    };

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        mRecorder = new Recorder();
        mMediaPlayer = new MediaPlayer();
        mBtnHeadsetStatus.setText(R.string.insert_headset);
        if (Environment.getExternalStorageState().equals("mounted")) {
            mFileRecAudio = Environment.getExternalStorageDirectory();
        }
        registerReceiver(this.mBroadcastReceiverHeadset, new IntentFilter("android.intent.action.HEADSET_PLUG"));
    }

    @Override
    protected void initViews() {
        super.initViews();
        mBtnHeadsetStatus = findViewById(R.id.headset_status);
        mBtnHeadsetStatus.setOnClickListener(TestItemHeadset.this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.headset_status:
                if (mStatus == 1) {
                    try {
                        startRecording();
                        mBtnHeadsetStatus.setText(R.string.stop_luyin);
                        mStatus = 2;
                    } catch (IOException localIOException) {
                        localIOException.printStackTrace();
                    }
                } else if (mStatus == 2) {
                    stopRecording();
                    playRecord();
                    mBtnHeadsetStatus.setText(getString(R.string.start_luyin));
                    mStatus = 1;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mBroadcastReceiverHeadset);
        mRecorder.delete();
    }

    protected void playRecord() {
        if (!Environment.getExternalStorageState().equals("mounted")) {
            return;
        }
        try {
            mMediaPlayer.setDataSource(this.mFileAudio.getAbsolutePath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    protected void startRecording()
            throws IOException {
        sendBroadcast(new Intent("com.mediatek.FMRadio.FMRadioService.ACTION_TOFMSERVICE_POWERDOWN"));
        mMediaRecorderRec = new MediaRecorder();
        mMediaRecorderRec.setAudioSource(1);
        mMediaRecorderRec.setOutputFormat(1);
        mMediaRecorderRec.setAudioEncoder(1);
        File localFile;
        try {
            if (this.mFileAudio == null) {
                localFile = Environment.getExternalStorageDirectory();
                mFileAudio = File.createTempFile("test", ".3gp", localFile);
                mMediaRecorderRec.setOutputFile(this.mFileAudio.getAbsolutePath());
                mMediaRecorderRec.prepare();
                mMediaRecorderRec.start();
            }
        } catch (IOException localIOException) {
            Log.e(TAG, "sdcard access error");
        }
    }

    protected void stopRecording() {
        if (this.mMediaRecorderRec != null) {
            try {
                mMediaRecorderRec.stop();
                mMediaRecorderRec.reset();
                mMediaRecorderRec.release();
                mMediaRecorderRec = null;
            } catch (IllegalArgumentException localIllegalArgumentException) {
                localIllegalArgumentException.printStackTrace();
            } catch (IllegalStateException localIllegalStateException) {
                localIllegalStateException.printStackTrace();
            }
        }
    }
}
