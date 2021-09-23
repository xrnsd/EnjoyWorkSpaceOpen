package com.kuyou.ft.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class Recorder {
    MediaPlayer mPlayer = null;
    MediaRecorder mRecorder = null;
    File mSampleFile = null;
    int mSampleLength = 0;
    long mSampleStart = 0L;
    public int mState = 0;
    public static final int RECORD_STATE_IDLE = 0;
    public static final int RECORD_STATE_RECORDING = 1;

    private static final String TAG = "FMRecorder";

    public void delete() {
        stop();
        stopPlayback();

        if (this.mSampleFile != null) {
            this.mSampleFile.delete();
        }
        this.mSampleFile = null;
        this.mSampleLength = 0;
    }

    public void startPlayback() {
        // stop();
        stopPlayback();

        this.mPlayer = new MediaPlayer();
        try {
            this.mPlayer.setDataSource(this.mSampleFile.getAbsolutePath());
            this.mPlayer.prepare();
            this.mPlayer.start();
            this.mSampleStart = System.currentTimeMillis();
        } catch (IllegalArgumentException localIllegalArgumentException) {
            this.mPlayer = null;
        } catch (IOException localIOException) {
            this.mPlayer = null;

        }
    }

    public void startRecording(int paramInt1, int paramInt2, String paramString, Context paramContext) {
        stop();
        stopPlayback();

        File sampleDir = Environment.getExternalStorageDirectory();

        if (!sampleDir.canWrite()) {
            Log.i(TAG, "----- file can't write!! ---");
            // Workaround for broken sdcard support on the device.
            sampleDir = new File("/sdcard/sdcard");
        }

        sampleDir = new File(sampleDir.getAbsolutePath() + "/PhoneRecord");
        if (sampleDir.exists() == false) {
            sampleDir.mkdirs();
        }

        try {
            mSampleFile = File.createTempFile("recording", paramString, sampleDir);
        } catch (IOException e) {
            //setError(SDCARD_ACCESS_ERROR);
            Log.i(TAG, "----***------- can't access sdcard !!");
        }


        mRecorder = new MediaRecorder();
        //mRecorder.setOnErrorListener(this);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(1);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mSampleFile.getAbsolutePath());

        try {
            mRecorder.prepare();
            mRecorder.start();
            mSampleStart = System.currentTimeMillis();
            mState = RECORD_STATE_RECORDING;
            //setState(RECORDING_STATE);
        } catch (IOException exception) {
            //setError(INTERNAL_ERROR);
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        } catch (RuntimeException exception) {
            //setError(INTERNAL_ERROR);
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void stop() {
        stopRecording();
        //stopPlayback();
    }

    public void stopPlayback() {
        if (this.mPlayer == null) {
            return;
        } else {
            this.mPlayer.stop();
            this.mPlayer.release();
            this.mPlayer = null;
        }
    }

    public void stopRecording() {
        if (this.mRecorder == null) {
            return;

        } else {
            this.mRecorder.stop();
            this.mRecorder.reset();
            this.mRecorder.release();
            this.mRecorder = null;
            this.mSampleLength = (int) ((System.currentTimeMillis() - this.mSampleStart) / 1000L);
            mState = RECORD_STATE_IDLE;
        }
    }

    public int getMaxAmplitude() {
        if (mState != RECORD_STATE_RECORDING) {
            return 0;
        }
        return mRecorder.getMaxAmplitude();
    }
}
