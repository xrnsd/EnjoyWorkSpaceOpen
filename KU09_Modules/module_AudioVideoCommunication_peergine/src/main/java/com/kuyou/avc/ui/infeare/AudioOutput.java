/*************************************************************************
 copyright   : Copyright (C) 2012-2012, chenbichao, All rights reserved.
 filename    : AudioOutput.java
 discription : 
 modify      : create, chenbichao, 2012/02/10
 *************************************************************************/

package com.kuyou.avc.ui.infeare;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

import com.peergine.plugin.android.pgDevAudioOut;
import com.peergine.plugin.android.pgSysAudioHandler;
import com.peergine.plugin.android.pgSysAudioIn;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


/*
public class AudioOutput {
    
    private AudioTrack m_Player = null;
    
    private int m_iDevID = -1;
    private Random m_Random = new Random();

    private int m_iSampleByte = 0;
    private int m_iBufSize = 0;
    private int m_iPackSize = 0;
    private int m_iUsedSize = 0;
    
    public AudioOutput() {
    }

    public int Open(int iSpeakerNO, int uSampleBits, int uSampleRate, int uChannels, int uPackBytes) {
        Log.d("DevExtend", "AudioOutput.Open, uSampleBits=" + uSampleBits + ", uSampleRate=" + uSampleRate + ", uPackBytes=" + uPackBytes);

        try {
            int iSampleFmt = (uSampleBits == 16) ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
            int iMinBufSize = AudioTrack.getMinBufferSize(uSampleRate, AudioFormat.CHANNEL_OUT_MONO, iSampleFmt);
            if (iMinBufSize <= 0) {
                Log.d("DevExtend", "AudioOutput.Open failed. get min buffer size");
                return -1;
            }
            
            Log.d("DevExtend", "AudioOutput.Open, iMinBufSize=" + iMinBufSize);

            int iBufSize = (iMinBufSize / uPackBytes) * uPackBytes;
            if ((iMinBufSize % uPackBytes) != 0) {
                iBufSize += uPackBytes;
            }
            
            if ((iBufSize / uPackBytes) < 3) {
                iBufSize = (uPackBytes * 3);
            }

            m_iSampleByte = (uSampleBits / 8);
            m_iBufSize = iBufSize;
            m_iPackSize = uPackBytes;
            m_iUsedSize = 0;

            m_Player = new AudioTrack(AudioManager.STREAM_MUSIC, uSampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, iBufSize, AudioTrack.MODE_STREAM);
            if (m_Player.getState() != m_Player.STATE_INITIALIZED) {
                Log.d("DevExtend", "AudioOutput.Open failed, not inited");
                return -1;
            }

            m_Player.play();

            m_bThreadPollRun = true;
            m_threadPoll = new PollThread();
            m_threadPoll.start();
    
            while (true) {
                m_iDevID = m_Random.nextInt();
                if (m_iDevID > 0) {
                    break;
                }
            }

            Log.d("DevExtend", "AudioOutput.Open ok");
            return m_iDevID;
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Open Exception, ex=" + ex.toString());
            return -1;
        }
    }

    public void Close() {
        try {
            Log.d("DevExtend", "AudioOutput.Close");
            
            m_bThreadPollRun = false;
            if (m_Player != null) {
                m_Player.stop();
                m_Player.release();
                m_Player = null;
            }

            Log.d("DevExtend", "AudioOutput.Close: finish");
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Close: Exception, ex=" + ex.toString());
        }
    }

    public int Write(byte[] byteData, int iSize) {
        try {        
            int iWrite = -1;
            synchronized(m_Player) {            
                if ((m_iBufSize - m_iUsedSize) >= m_iPackSize) {
                    iWrite = m_Player.write(byteData, 0, iSize);
                    if (iWrite > 0) {
                        m_iUsedSize += m_iPackSize;
                        if (iWrite != iSize) {
                            Log.d("DevExtend", "AudioOutput.Write: iWrite=" + iWrite + ", iSize=" + iSize);
                        }
                        //Log.d("DevExtend", "AudioOutput.Write: Stamp=" + (new Date()).getTime());
                    }
                }
            }

            return iWrite;
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Write: ex=" + ex.toString());
            return -1;
        }
    }

    ///
    // Thread of poll.
    private Thread m_threadPoll = null;
    private boolean m_bThreadPollRun = false;

    class PollThread extends Thread {
        public void run() {
            try {
                //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

                Log.d("DevExtend", "AudioOutput.LooperThread.run: start");

                int iPlaySizeBack = 0;
                while (m_bThreadPollRun && m_Player != null) {
                    int iPlayPos = m_Player.getPlaybackHeadPosition();
                    if (iPlayPos <= 0) {
                        Thread.sleep(10);
                        continue;
                    }
                    
                    int iPlaySize = iPlayPos * m_iSampleByte;
                    if ((iPlaySize - iPlaySizeBack) >= m_iPackSize) {
                        synchronized(m_Player) {
                            if (m_iUsedSize >= m_iPackSize) {
                                m_iUsedSize -= m_iPackSize;
                            }
                            else {
                                m_iUsedSize = 0;
                            }
                        }
                        
                        iPlaySizeBack += m_iPackSize;

                        pgDevAudioOut.PlayedProc(m_iDevID, iPlaySizeBack, 0);
                        //Log.d("DevExtend", "AudioOutput.LooperThread.run: Stamp=" + (new Date()).getTime());
                    }

                    Thread.sleep(10);
                }

                Log.d("DevExtend", "AudioOutput.LooperThread.run: exit");
            }
            catch (Exception ex) {
                Log.d("DevExtend", "AudioOutput.LooperThread Exception, ex=" + ex.toString());
            }
        }
    }
}
*/

/*
public class AudioOutput {
    
    private AudioTrack m_Player = null;
    
    private int m_iPackSize = 0;
    private int m_iWriteSize = 0;
    private int m_iPlayedSize = 0;
    
    private int m_iDevID = -1;
    private Random m_Random = new Random();
    
    private int m_iSpeakerNO = 0;
    private int m_uSampleBits = 0;
    private int m_uSampleRate = 0;
    private int m_uChannels = 0;
    private int m_uPackBytes = 0;
    
    private int m_iOpenReturn = -1;
    private boolean m_bOpenFinish = false;
    private boolean m_bCloseFinish = false;
    
    public AudioOutput() {
    }

    public int Open(int iSpeakerNO, int uSampleBits, int uSampleRate, int uChannels, int uPackBytes) {
        Log.d("DevExtend", "AudioOutput.Open, uSampleBits=" + uSampleBits + ", uSampleRate=" + uSampleRate + ", uPackBytes=" + uPackBytes);
        try {
            m_threadLooper = new AudioLooperThread();
            m_threadLooper.start();

            int iInd = 0;
            while (iInd < 50 && m_Handler == null) {
                Thread.sleep(10);
                iInd++;
            }
            if (m_Handler == null) {
                if (m_thisLooper != null) {
                    m_thisLooper.quit();
                    m_thisLooper = null;
                }
                return -1;                
            }

            m_iSpeakerNO = iSpeakerNO;
            m_uSampleBits = uSampleBits;
            m_uSampleRate = uSampleRate;
            m_uChannels = uChannels;
            m_uPackBytes = uPackBytes;
            m_iOpenReturn = -1;

            m_bOpenFinish = false;
            m_Handler.post(new Runnable() {
                public void run() {
                    m_iOpenReturn = OpenPrivate(m_iSpeakerNO, m_uSampleBits, m_uSampleRate, m_uChannels, m_uPackBytes);
                    m_bOpenFinish = true;
                }
            });

            iInd = 0;
            while (iInd < 200 && !m_bOpenFinish) {
                Thread.sleep(10);
                iInd++;
            }
            if (!m_bOpenFinish) {
                m_thisLooper.quit();
                return -1;                
            }

            if (m_iOpenReturn < 0) {
                m_thisLooper.quit();
                return -1;
            }

            //pgDevAudioOut.PlaySilent(iSpeakerNO, 1);
            m_thread = new AudioOutThread();
            m_thread.start();

            Log.d("DevExtend", "AudioOutput.Open ok");
            return m_iOpenReturn;
        }
        catch (Exception ex) {
            if (m_thisLooper != null) {
                m_thisLooper.quit();
                m_thisLooper = null;
            }
            Log.d("DevExtend", "AudioOutput.Open Exception, ex=" + ex.toString());
            return -1;
        }
    }

    public void Close() {
        try {
            Log.d("DevExtend", "AudioOutput.Close");

            m_iDevID = -1;
            
            if (m_thread != null) {
                m_thread.join(300);
                m_thread = null;
            }

            m_bCloseFinish = false;
            m_Handler.post(new Runnable() {
                public void run() {
                    ClosePrivate();
                    m_bCloseFinish = true;
                }
            });

            int iInd = 0;
            while (iInd < 30 && !m_bCloseFinish) {
                Thread.sleep(10);
                iInd++;
            }

            if (m_threadLooper != null) {
                m_thisLooper.quit();
                m_threadLooper.join(100);
                m_threadLooper = null;
                m_thisLooper = null;
                m_Handler = null;
            }
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Close, ex=" + ex.toString());            
        }
    }

    public int Write(byte[] byteData, int iSize) {
        try {
            if (m_Player != null) {
                int iWrite = m_Player.write(byteData, 0, iSize);
                if (iWrite > 0) {
                    if (iWrite != iSize) {
                        Log.d("DevExtend", "AudioOutput.Write: iWrite=" + iWrite + ", iSize=" + iSize);
                    }
                    m_iWriteSize += iWrite;
                }
                return iWrite;
            }
            return -1;
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Write: ex=" + ex.toString());
            return -1;
        }
    }
    
    @SuppressWarnings("static-access")
    private int OpenPrivate(int iSpeakerNO, int uSampleBits, int uSampleRate, int uChannels, int uPackBytes) {
        try {
            int iSampleFmt = (uSampleBits == 16) ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
            int iMinBufSize = AudioTrack.getMinBufferSize(uSampleRate, AudioFormat.CHANNEL_OUT_MONO, iSampleFmt);
            if (iMinBufSize <= 0) {
                Log.d("DevExtend", "AudioOutput.OpenPrivate failed. get min buffer size");
                return -1;
            }
            
            Log.d("DevExtend", "AudioOutput.OpenPrivate, iMinBufSize=" + iMinBufSize);
    
            int iBufSize = (iMinBufSize / uPackBytes) * uPackBytes;
            if ((iMinBufSize % uPackBytes) != 0) {
                iBufSize += uPackBytes;
            }
    
            m_Player = new AudioTrack(AudioManager.STREAM_MUSIC, uSampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, iBufSize, AudioTrack.MODE_STREAM);
            if (m_Player.getState() != m_Player.STATE_INITIALIZED) {
                Log.d("DevExtend", "AudioOutput.OpenPrivate failed, not inited");
                return -1;
            }
    
            m_iPackSize = uPackBytes;
            m_iWriteSize = 0;
            m_iPlayedSize = 0;
    
            m_Player.play();
            
            while (true) {
                m_iDevID = m_Random.nextInt();
                if (m_iDevID > 0) {
                    break;
                }
            }
            
            return m_iDevID;
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.OpenPrivate: ex=" + ex.toString());
            return -1;
        }
    }

    private void ClosePrivate() {
        if (m_Player != null) {
            m_Player.stop();
            m_Player.release();
            m_Player = null;
        }
    }


    Thread m_threadLooper = null;
    Looper m_thisLooper = null;
    Handler m_Handler = null;

    class AudioLooperThread extends Thread {
        public void run() {
            try {
                Log.d("DevExtend", "AudioLooperThread.run: start");

                Looper.prepare();
                m_thisLooper = Looper.myLooper();                
                m_Handler = new Handler();

                Looper.loop();
                
                m_thisLooper = null;
                m_Handler = null;
                Log.d("DevExtend", "AudioLooperThread.run: exit");
            }
            catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    
    Thread m_thread = null;

    class AudioOutThread extends Thread {
        public void run() {
            AudioOutput.this.ThreadProc();
        }
    }

    public void ThreadProc() {
        try {
            long lPlayDelta = 40;
            long lStartStamp = (new Date()).getTime();

            while (m_Player != null && m_iDevID > 0) {
                long lCurStamp = (new Date()).getTime();
                if ((lCurStamp - lStartStamp) >= lPlayDelta) {
                    if ((m_iWriteSize - m_iPlayedSize) >= m_iPackSize) {
                        m_iPlayedSize += m_iPackSize;

                    //    Log.d("DevExtend", "AudioOutput.ThreadProc: lPlayDelta=" + lPlayDelta
                    //        + ", iWriteSize=" + m_iWriteSize + ", iPlayedSize=" + m_iPlayedSize);
                        pgDevAudioOut.PlayedProc(m_iDevID, m_iPlayedSize, 0);
                    }

                    lPlayDelta += 40;
                }

                Thread.sleep(10);
            }

            Log.d("DevExtend", "AudioOutput.ThreadProc exit");
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.ThreadProc Exception, ex=" + ex.toString());
        }
    }
}
*/

/*
public class AudioOutput implements AudioTrack.OnPlaybackPositionUpdateListener {
    
    private AudioTrack m_Player = null;

    private int m_iDevID = -1;
    private Random m_Random = new Random();

    private int m_iBufSize = 0;
    private int m_iPackSize = 0;
    private int m_iUsedSize = 0;
    private int m_iPlayedSize = 0;

    private int m_iSpeakerNO = 0;
    private int m_uSampleBits = 0;
    private int m_uSampleRate = 0;
    private int m_uChannels = 0;
    private int m_uPackBytes = 0;

    private boolean m_bOpenReturn = false;
    private boolean m_bOpenFinish = false;
    private boolean m_bCloseFinish = false;

    public AudioOutput() {
    }

    public int Open(int iSpeakerNO, int uSampleBits, int uSampleRate, int uChannels, int uPackBytes) {
        try {
            Log.d("DevExtend", "AudioOutput.Open, uSampleBits=" + uSampleBits + ", uSampleRate=" + uSampleRate + ", uPackBytes=" + uPackBytes);

            if (!ThreadLooperStart()) {
                Log.d("DevExtend", "AudioOutput.Open, start looper thread failed");
                return -1;
            }

            m_iSpeakerNO = iSpeakerNO;
            m_uSampleBits = uSampleBits;
            m_uSampleRate = uSampleRate;
            m_uChannels = uChannels;
            m_uPackBytes = uPackBytes;

            m_bOpenReturn = false;
            m_bOpenFinish = false;
            m_Handler.post(new Runnable() {
                public void run() {
                    m_bOpenReturn = OpenPrivate(m_iSpeakerNO, m_uSampleBits, m_uSampleRate, m_uChannels, m_uPackBytes);
                    m_bOpenFinish = true;
                }
            });

            int iInd = 0;
            while (iInd < 300 && !m_bOpenFinish) {
                Thread.sleep(10);
                iInd++;
            }
            if (!m_bOpenFinish || !m_bOpenReturn) {
                ThreadLooperStop(false);
                return -1;                
            }

            while (true) {
                m_iDevID = m_Random.nextInt();
                if (m_iDevID > 0) {
                    break;
                }
            }

            Log.d("DevExtend", "AudioOutput.Open ok");
            return m_iDevID;
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Open Exception, ex=" + ex.toString());
            return -1;
        }
    }

    public void Close() {
        try {
            Log.d("DevExtend", "AudioOutput.Close");

            m_bCloseFinish = false;
            m_Handler.post(new Runnable() {
                public void run() {
                    ClosePrivate();
                    m_bCloseFinish = true;
                }
            });

            ThreadLooperStop(true);
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Close Exception, ex=" + ex.toString());
        }
    }

    public int Write(byte[] byteData, int iSize) {
        try {
            int iWrite = -1;
            synchronized(m_Player) {            
                if ((m_iBufSize - m_iUsedSize) >= m_iPackSize) {
                    iWrite = m_Player.write(byteData, 0, iSize);
                    if (iWrite > 0) {
                        m_iUsedSize += m_iPackSize;
                        if (iWrite != iSize) {
                            Log.d("DevExtend", "AudioOutput.Write: iWrite=" + iWrite + ", iSize=" + iSize);
                        }
                    }
                }
            }
            return iWrite;
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Write: ex=" + ex.toString());
            return -1;
        }
    }
    
    @SuppressWarnings("static-access")
    private boolean OpenPrivate(int iSpeakerNO, int uSampleBits, int uSampleRate, int uChannels, int uPackBytes) {
        try {
            int iSampleFmt = (uSampleBits == 16) ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
            int iMinBufSize = AudioTrack.getMinBufferSize(uSampleRate, AudioFormat.CHANNEL_OUT_MONO, iSampleFmt);
            if (iMinBufSize <= 0) {
                Log.d("DevExtend", "AudioOutput.OpenPrivate failed. get min buffer size");
                return false;
            }
            
            int iBufSize = (iMinBufSize / uPackBytes) * uPackBytes;
            if ((iMinBufSize % uPackBytes) != 0) {
                iBufSize += uPackBytes;
            }

            if ((iBufSize / uPackBytes) < 3) {
                iBufSize = (uPackBytes * 3);
            }

            Log.d("DevExtend", "AudioOutput.OpenPrivate, iMinBufSize=" + iMinBufSize + ", iBufSize=" + iBufSize);

            m_Player = new AudioTrack(AudioManager.STREAM_MUSIC, uSampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, iBufSize, AudioTrack.MODE_STREAM);
            if (m_Player.getState() != m_Player.STATE_INITIALIZED) {
                Log.d("DevExtend", "AudioOutput.OpenPrivate failed, not inited");
                return false;
            }

            if (m_Player.setPositionNotificationPeriod(uPackBytes / (uSampleBits / 8)) != AudioTrack.SUCCESS) {
                Log.d("DevExtend", "AudioOutput.OpenPrivate failed. set pos notify");
                return false;
            }

            m_Player.setPlaybackPositionUpdateListener(this);
            
            m_iBufSize = iBufSize;
            m_iPackSize = uPackBytes;
            m_iUsedSize = 0;
            m_iPlayedSize = 0;

            m_Player.play();
            return true;
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.OpenPrivate: ex=" + ex.toString());
            return false;
        }
    }

    private void ClosePrivate() {
        if (m_Player != null) {
            m_Player.setPlaybackPositionUpdateListener(null);
            m_Player.stop();
            m_Player.release();
            m_Player = null;
        }        
    }

    ///
    // Thread of looper.
    private Thread m_threadLooper = null;
    private Looper m_thisLooper = null;
    private Handler m_Handler = null;
    private boolean m_bThreadLooperStart = false;
    private boolean m_bThreadLooperRun = false;

    class LooperThread extends Thread {
        public void run() {
            AudioOutput.this.ThreadLooperProc();
        }
    }

    public void ThreadLooperProc() {
        try {
            Log.d("DevExtend", "AudioOutput.ThreadLooperProc: running");

            if (m_bThreadLooperStart) {
                Looper.prepare();
                m_thisLooper = Looper.myLooper();                
                m_Handler = new Handler();

                m_bThreadLooperRun = true;
                Looper.loop();

                if (!m_bCloseFinish) {
                    ClosePrivate();
                    m_bCloseFinish = true;
                }

                m_thisLooper = null;
                m_Handler = null;

                m_bThreadLooperStart = false;
                m_bThreadLooperRun = false;
            }

            Log.d("DevExtend", "AudioOutput.ThreadLooperProc: exit");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean ThreadLooperStart() {
        try {
            m_bThreadLooperStart = true;
            m_bThreadLooperRun = false;

            m_threadLooper = new LooperThread();
            m_threadLooper.start();

            int iInd = 0;
            while (iInd < 200 && !m_bThreadLooperRun) {
                Thread.sleep(10);
                iInd++;
            }
            if (!m_bThreadLooperRun) {
                m_bThreadLooperStart = false;
                if (m_thisLooper != null) {
                    m_thisLooper.quit();
                }
                return false;
            }

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void ThreadLooperStop(boolean bSafe) {
        try {
            if (m_threadLooper != null) {
                if (bSafe) {
                    m_thisLooper.quitSafely();
                    m_threadLooper.join(100);
                }
                else {
                    m_thisLooper.quit();
                    m_threadLooper.join(100);
                }
                m_threadLooper = null;
            }
            m_bThreadLooperStart = false;
            m_bThreadLooperRun = false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMarkerReached(AudioTrack track) {
    }

    @Override
    public void onPeriodicNotification(AudioTrack track) {
        try {
            //Log.d("DevExtend", "AudioOutput.onPeriodicNotification: Stamp=" + (new Date()).getTime());

            if (m_Player == null) {
                return;
            }

            synchronized(m_Player) {
                if (m_iUsedSize >= m_iPackSize) {
                    m_iUsedSize -= m_iPackSize;
                }
                else {
                    m_iUsedSize = 0;
                }
            }

            m_iPlayedSize += m_iPackSize;
            pgDevAudioOut.PlayedProc(m_iDevID, m_iPlayedSize, 0);
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.onPeriodicNotification: ex=" + ex.toString());
        }
    }
}
*/

/*
public class AudioOutput implements AudioTrack.OnPlaybackPositionUpdateListener {
    
    private AudioTrack m_Player = null;

    private int m_iDevID = -1;
    private Random m_Random = new Random();
    
    private int m_iBufSize = 0;
    private int m_iPackSize = 0;
    private int m_iUsedSize = 0;
    private int m_iPlayedSize = 0;
    
    public AudioOutput() {
    }

    public int Open(int iSpeakerNO, int uSampleBits, int uSampleRate, int uChannels, int uPackBytes) {
        Log.d("DevExtend", "AudioOutput.Open, uSampleBits=" + uSampleBits + ", uSampleRate=" + uSampleRate + ", uPackBytes=" + uPackBytes);
        try {
            int iSampleFmt = (uSampleBits == 16) ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
            int iMinBufSize = AudioTrack.getMinBufferSize(uSampleRate, AudioFormat.CHANNEL_OUT_MONO, iSampleFmt);
            if (iMinBufSize <= 0) {
                Log.d("DevExtend", "AudioOutput.Open failed. get min buffer size");
                return 0;
            }
            
            Log.d("DevExtend", "AudioOutput.Open, iMinBufSize=" + iMinBufSize);

            int iBufSize = (iMinBufSize / uPackBytes) * uPackBytes;
            if ((iMinBufSize % uPackBytes) != 0) {
                iBufSize += uPackBytes;
            }
            
            if ((iBufSize / uPackBytes) < 3) {
                iBufSize = (uPackBytes * 3);
            }

            m_iBufSize = iBufSize;
            m_iPackSize = uPackBytes;
            m_iUsedSize = 0;
            m_iPlayedSize = 0;
            
            Handler handler = pgSysAudioHandler.OutputHandlerGet();
            if (handler == null) {
                Log.d("DevExtend", "AudioOutput.Open failed, get handler");
                return -1;
            }

            m_Player = new AudioTrack(AudioManager.STREAM_MUSIC, uSampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, iBufSize, AudioTrack.MODE_STREAM);
            if (m_Player.getState() != m_Player.STATE_INITIALIZED) {
                pgSysAudioHandler.OutputHandlerRelease();
                Log.d("DevExtend", "AudioOutput.Open failed, not inited");
                return -1;
            }

            if (m_Player.setPositionNotificationPeriod(uPackBytes / (uSampleBits / 8)) != AudioTrack.SUCCESS) {
                pgSysAudioHandler.OutputHandlerRelease();
                Log.d("DevExtend", "AudioOutput.Open failed. set pos notify");
                return -1;
            }

            m_Player.setPlaybackPositionUpdateListener(this, handler);
            m_Player.play();

            while (true) {
                m_iDevID = m_Random.nextInt();
                if (m_iDevID > 0) {
                    break;
                }
            }
    
            Log.d("DevExtend", "AudioOutput.Open ok");
            return m_iDevID;
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Open Exception, ex=" + ex.toString());
            return -1;
        }
    }

    public void Close() {
        Log.d("DevExtend", "AudioOutput.Close");

        if (m_Player != null) {
            m_Player.setPlaybackPositionUpdateListener(null);
            m_Player.stop();
            m_Player.release();
            m_Player = null;
            pgSysAudioHandler.OutputHandlerRelease();
        }
    }

    public int Write(byte[] byteData, int iSize) {
        try {        
            int iWrite = -1;
            synchronized(m_Player) {            
                if ((m_iBufSize - m_iUsedSize) >= m_iPackSize) {
                    iWrite = m_Player.write(byteData, 0, iSize);
                    if (iWrite > 0) {
                        m_iUsedSize += m_iPackSize;
                        if (iWrite != iSize) {
                            Log.d("DevExtend", "AudioOutput.Write: iWrite=" + iWrite + ", iSize=" + iSize);
                        }
                        //Log.d("DevExtend", "AudioOutput.Write: Stamp=" + (new Date()).getTime());
                    }
                }
            }

            return iWrite;
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Write: ex=" + ex.toString());
            return -1;
        }
    }

    public int GetDelay() {
        return 0;
    }

    @Override
    public void onMarkerReached(AudioTrack track) {
    }

    @Override
    public void onPeriodicNotification(AudioTrack track) {
        try {
            if (m_Player != null) {
                synchronized(m_Player) {
                    if (m_iUsedSize >= m_iPackSize) {
                        m_iUsedSize -= m_iPackSize;
                    }
                    else {
                        m_iUsedSize = 0;
                    }
                }
        
                //Log.d("DevExtend", "AudioOutput.onPeriodicNotification: Stamp=" + (new Date()).getTime());
                m_iPlayedSize += m_iPackSize;
                pgDevAudioOut.PlayedProc(m_iDevID, m_iPlayedSize, 0);
            }
        }
        catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.onPeriodicNotification: ex=" + ex.toString());
        }
    }
}
*/

public class AudioOutput implements AudioTrack.OnPlaybackPositionUpdateListener {

    private int m_iDevID = -1;
    private Random m_Random = new Random();
    private boolean m_bPlayStartAtOpen = true;

    private AudioTrack m_Player = null;
    private AtomicInteger m_atomicPlayer = new AtomicInteger();
    private boolean m_bPlayStart = false;
    private boolean m_bInputPollCallback = false;

    private int m_iSampleByte = 0;
    private int m_iSampleRate = 0;
    private int m_iBufSize = 0;
    private int m_iPackSize = 0;
    private int m_iUsedSize = 0;
    private int m_iPlayedSize = 0;
    private int m_iPackInterval = 0;

    private long m_lCallbackStamp = 0;
    private int m_iCallbackPos = 0;
    private int m_iCallbackCount = 0;

    public AudioOutput() {
    }

    public int Open(int iSpeakerNO, int uSampleBits, int uSampleRate, int uChannels, int uPackBytes) {
        Log.d("DevExtend", "AudioOutput.Open, uSampleBits=" + uSampleBits + ", uSampleRate=" + uSampleRate + ", uPackBytes=" + uPackBytes);
        try {
            int iSampleFmt = (uSampleBits == 16) ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
            int iMinBufSize = AudioTrack.getMinBufferSize(uSampleRate, AudioFormat.CHANNEL_OUT_MONO, iSampleFmt);
            if (iMinBufSize <= 0) {
                Log.d("DevExtend", "AudioOutput.Open failed. get min buffer size");
                return -1;
            }

            Log.d("DevExtend", "AudioOutput.Open, iMinBufSize=" + iMinBufSize);

            int iBufSize = (iMinBufSize / uPackBytes) * uPackBytes;
            if ((iMinBufSize % uPackBytes) != 0) {
                iBufSize += uPackBytes;
            }

            if ((iBufSize / uPackBytes) < 3) {
                iBufSize = (uPackBytes * 3);
            }

            m_iSampleByte = (uSampleBits / 8);
            m_iSampleRate = uSampleRate;
            m_iBufSize = iBufSize;
            m_iPackSize = uPackBytes;
            m_iUsedSize = 0;
            m_iPlayedSize = 0;
            m_iPackInterval = (1000 * (uPackBytes / m_iSampleByte)) / uSampleRate;

            m_lCallbackStamp = 0;
            m_iCallbackPos = 0;
            m_iCallbackCount = 0;

            if (pgSysAudioIn.IsInputOpened()) {
                m_Player = new AudioTrack(AudioManager.STREAM_MUSIC, uSampleRate,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, iBufSize, AudioTrack.MODE_STREAM);
                if (m_Player.getState() != m_Player.STATE_INITIALIZED) {
                    Log.d("DevExtend", "AudioOutput.Open failed, not inited");
                    return -1;
                }


                pgSysAudioIn.SetOutCallback(m_outCallback);
                m_bInputPollCallback = true;

                if (m_bPlayStartAtOpen) {
                    m_Player.play();
                    m_bPlayStart = true;
                }

                Log.d("DevExtend", "AudioOutput.Open: input poll mode. PlayStart=" + m_bPlayStart);
            } else {
                Handler handler = pgSysAudioHandler.OutputHandlerGet();
                if (handler == null) {
                    Log.d("DevExtend", "AudioOutput.Open failed, get handler");
                    return -1;
                }

                m_Player = new AudioTrack(AudioManager.STREAM_MUSIC, uSampleRate,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, iBufSize, AudioTrack.MODE_STREAM);
                if (m_Player.getState() != m_Player.STATE_INITIALIZED) {
                    pgSysAudioHandler.OutputHandlerRelease();
                    Log.d("DevExtend", "AudioOutput.Open failed, not inited");
                    return -1;
                }

                if (m_Player.setPositionNotificationPeriod(uPackBytes / (uSampleBits / 8)) != AudioTrack.SUCCESS) {
                    pgSysAudioHandler.OutputHandlerRelease();
                    Log.d("DevExtend", "AudioOutput.Open failed. set pos notify");
                    return -1;
                }

                m_Player.setPlaybackPositionUpdateListener(this, handler);
                m_bInputPollCallback = false;

                if (m_bPlayStartAtOpen) {
                    m_Player.play();
                    m_bPlayStart = true;
                }

                Log.d("DevExtend", "AudioOutput.Open: thread handler mode. PlayStart=" + m_bPlayStart);
            }

            while (true) {
                m_iDevID = m_Random.nextInt();
                if (m_iDevID > 0) {
                    break;
                }
            }

            Log.d("DevExtend", "AudioOutput.Open: ok");
            return m_iDevID;
        } catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Open: ex=" + ex.toString());
            return -1;
        }
    }

    public void Close() {
        try {
            Log.d("DevExtend", "AudioOutput.Close");

            if (m_bInputPollCallback) {
                pgSysAudioIn.SetOutCallback(null);
                if (m_Player != null) {
                    m_Player.stop();
                    m_Player.release();
                    m_Player = null;
                }
            } else {
                if (m_Player != null) {
                    m_Player.setPlaybackPositionUpdateListener(null);
                    m_Player.stop();
                    m_Player.release();
                    m_Player = null;
                    pgSysAudioHandler.OutputHandlerRelease();
                }
            }

            m_bPlayStart = false;

            Log.d("DevExtend", "AudioOutput.Close: finish");
        } catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Close: ex=" + ex.toString());
        }
    }

    public int Write(byte[] byteData, int iSize) {
        try {
            int iWrite = -1;

            synchronized (m_atomicPlayer) {
                if (!m_bPlayStart) {
                    m_Player.play();
                    m_bPlayStart = true;
                    Log.d("DevExtend", "AudioOutput.Write: PlayStart=" + m_bPlayStart);
                }

                if ((m_iBufSize - m_iUsedSize) >= m_iPackSize) {
                    iWrite = m_Player.write(byteData, 0, iSize);
                    if (iWrite > 0) {

                        if (m_bInputPollCallback && m_iUsedSize <= 0) {
                            m_lCallbackStamp = (new Date()).getTime() + m_iPackInterval;
                            m_iCallbackPos = m_Player.getPlaybackHeadPosition();
                            m_iCallbackCount = 0;
                        }

                        m_iUsedSize += m_iPackSize;
                        if (iWrite != iSize) {
                            Log.d("DevExtend", "AudioOutput.Write: iWrite=" + iWrite + ", iSize=" + iSize);
                        }
                    }
                }
            }

            return iWrite;
        } catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.Write: ex=" + ex.toString());
            return -1;
        }
    }

    private pgSysAudioIn.OutCallback m_outCallback = new pgSysAudioIn.OutCallback() {
        public boolean OnPoll() {
            return AudioOutput.this.OnPollProc();
        }
    };

    private boolean OnPollProc() {
        try {
            boolean bCallback = false;
            synchronized (m_atomicPlayer) {
                if (m_iUsedSize >= m_iPackSize) {
                    long lCurStamp = (new Date()).getTime();
                    if ((lCurStamp - m_lCallbackStamp) >= m_iPackInterval) {

                        m_iCallbackCount++;
                        if ((m_iCallbackCount % 100) == 0) {
                            int iCurPos = m_Player.getPlaybackHeadPosition();
                            if (iCurPos > m_iCallbackPos) {
                                int iInterval = (1000 * ((iCurPos - m_iCallbackPos) / m_iCallbackCount)) / m_iSampleRate;
                                if (iInterval > m_iPackInterval && m_iPackInterval < 43) {
                                    m_iPackInterval++;
                                } else if (iInterval < m_iPackInterval && m_iPackInterval > 37) {
                                    m_iPackInterval--;
                                }
                            }
                        }

                        m_lCallbackStamp += m_iPackInterval;
                        m_iUsedSize -= m_iPackSize;
                        bCallback = true;
                    }
                }
            }

            if (bCallback) {
                m_iPlayedSize += m_iPackSize;
                pgDevAudioOut.PlayedProc(m_iDevID, m_iPlayedSize, 0);
            } else {
                Log.d("DevExtend", "AudioOutput.OnPollProc: Skip played callback");
            }

            return bCallback;
        } catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.OnPollProc: ex=" + ex.toString());
            return true;
        }
    }

    @Override
    public void onMarkerReached(AudioTrack track) {
    }

    @Override
    public void onPeriodicNotification(AudioTrack track) {
        try {
            if (m_Player != null) {
                synchronized (m_Player) {
                    if (m_iUsedSize >= m_iPackSize) {
                        m_iUsedSize -= m_iPackSize;
                    }
                }

                m_iPlayedSize += m_iPackSize;
                pgDevAudioOut.PlayedProc(m_iDevID, m_iPlayedSize, 0);
            }
        } catch (Exception ex) {
            Log.d("DevExtend", "AudioOutput.onPeriodicNotification: ex=" + ex.toString());
        }
    }
}


