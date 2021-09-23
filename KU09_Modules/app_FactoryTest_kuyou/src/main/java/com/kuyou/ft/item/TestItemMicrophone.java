package com.kuyou.ft.item;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;
import com.kuyou.ft.basic.microphone.MicrophoneVUMeterTestView;
import com.kuyou.ft.util.Recorder;

public class TestItemMicrophone extends TestItemBasic {

    private int mStatus = 1;

    private Button mBtnMicrophoneStart;

    private MediaPlayer mMediaPlayer;
    private MediaRecorder mMediaRecorder = null;
    private Recorder mRecorder;
    private MicrophoneVUMeterTestView mVUMeter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.v(TAG, "FactoryTest handler msg is:" + msg.what);
            switch (msg.what) {
                case 0:
                    if (Environment.getExternalStorageState().equals("mounted")) {
                        mVUMeter.setRecorder(mRecorder);
                        mVUMeter.mCurrentAngle = 0;
                        mRecorder.startRecording(1, 1, "", TestItemMicrophone.this);
                        mBtnMicrophoneStart.setText(getString(R.string.stop_luyin));
                        mStatus = 2;
                    }
                    break;
                case 1:
                    if (Environment.getExternalStorageState().equals("mounted")) {
                        mVUMeter.mCurrentAngle = 0;
                        mRecorder.stopRecording();
                        mRecorder.startPlayback();
                        mStatus = 1;
                        mBtnMicrophoneStart.setText(getString(R.string.start_luyin));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int getTestId() {
        return R.id.test_microphone;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_mircorphone;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_microphone);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        mVUMeter = findViewById(R.id.uvMeter);
        mRecorder = new Recorder();
        if (Environment.getExternalStorageState().equals("mounted")) {
            mMediaRecorder = new MediaRecorder();
            mMediaPlayer = new MediaPlayer();
        } else {
            Toast.makeText(this, getString(R.string.no_sdcard), 1).show();
        }
    }

    @Override
    protected void initWindowConfig() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mBtnMicrophoneStart = findViewById(R.id.micor_start);
        mBtnMicrophoneStart.setOnClickListener(TestItemMicrophone.this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.micor_start:
                if (Environment.getExternalStorageState().equals("mounted")) {
                    if (mStatus == 1) {
                        mVUMeter.setRecorder(mRecorder);
                        mVUMeter.mCurrentAngle = 0;
                        mRecorder.startRecording(1, 1, "", TestItemMicrophone.this);
                        mBtnMicrophoneStart.setText(getString(R.string.stop_luyin));
                        mStatus = 2;
                    } else if (mStatus == 2) {
                        mVUMeter.mCurrentAngle = 0;
                        mRecorder.stopRecording();
                        mRecorder.startPlayback();
                        mStatus = 1;
                        mBtnMicrophoneStart.setText(getString(R.string.start_luyin));
                    }
                } else {
                    Toast.makeText(TestItemMicrophone.this, getString(R.string.no_sdcard), 1).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> mBtnMicrophoneStart.performClick(), 5000 * 0);
        onAgingTestItem(() -> mBtnMicrophoneStart.performClick(), 5000 * 1);
        onAgingTestItem(() -> onResult(true), 5000 * 2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.delete();
        }
    }
}
