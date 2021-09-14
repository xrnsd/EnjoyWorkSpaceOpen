package com.kuyou.ft.basic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

public class TimeView extends TextView {
    private static int j = 0;
    private int hour, min, second, i = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message paramMessage) {
            TimeView.this.second = paramMessage.what;
            if (TimeView.this.second == 60) {
                TimeView localTime2 = TimeView.this;
                localTime2.min = (1 + localTime2.min);
                TimeView.this.second = 0;
                if (TimeView.this.min == 60) {
                    TimeView localTime3 = TimeView.this;
                    localTime3.hour = (1 + localTime3.hour);
                    TimeView.this.min = 0;
                }
            }

            TimeView localTime1 = TimeView.this;
            StringBuilder localStringBuilder1 = new StringBuilder().append(" Time ");
            String str1;
            String str2;
            String str3;


            StringBuilder localStringBuilder3;
            if (TimeView.this.hour < 10) {
                str1 = "0" + TimeView.this.hour;
                //StringBuilder localStringBuilder2 = localStringBuilder1.append(str1);

            } else {
                str1 = ":" + TimeView.this.hour;

            }
            if (TimeView.this.min < 10) {
                str2 = ":0" + TimeView.this.min;
                //localStringBuilder3 = localStringBuilder2.append(str2);
            } else {
                str2 = ":" + TimeView.this.min;

            }
            if (TimeView.this.second < 10) {
                str3 = ":0" + TimeView.this.second;
            } else {
                str3 = ":" + TimeView.this.second;

            }
            setText("Time " + str1 + str2 + str3);

        }
    };

    public TimeView(Context paramContext) {
        super(paramContext);
        init();
    }

    public TimeView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    public TimeView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }

    public void init() {
        hour = 0;
        min = 0;
        second = 0;

        setText("Time 00:00:00");
        new SubThread().start();
    }

    class SubThread extends Thread {
        SubThread() {
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000L);
                    second = second + 1;
                    Message localMessage = new Message();
                    localMessage.what = second;
                    TimeView.this.mHandler.sendMessage(localMessage);
                } catch (InterruptedException localInterruptedException) {
                    localInterruptedException.printStackTrace();
                }
            }
        }
    }
}