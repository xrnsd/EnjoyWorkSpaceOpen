package com.kuyou.avc.ui.thermal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.thermal.seekware.SeekUtility;

/**
 * action :温度字符显示
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-16 <br/>
 * <p>
 */
public class TemperatureFollowView extends View {
    private static final String TAG = "com.kuyou.avc.ui.infeare > TemperatureFollowView";

    private Point mPointMax, mPointMin;
    private Paint mPaintMax;
    private float mTextHeight;
    private String mTmpMax, mTmpMin;

    public static final int SEEK_IMAGE_VIEW_LEFT = 186; //1920*1080分辨率下横屏getLeft()为这个值

    public TemperatureFollowView(Context context) {
        super(context);
    }

    public TemperatureFollowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TemperatureFollowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TemperatureFollowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void updateMaxTemp(Point point, SeekUtility.Temperature temp) {
        mPointMax = point;
        mPointMax.set(SEEK_IMAGE_VIEW_LEFT + Float.valueOf(point.x * 4.5F).intValue(),
                Float.valueOf(point.y * 4.5F).intValue());
        mTmpMax = temp.toString();
    }

    public void updateMinTemp(Point point, SeekUtility.Temperature temp) {
        mPointMin = point;
        mPointMax.set(SEEK_IMAGE_VIEW_LEFT + Float.valueOf(point.x * 4.5F).intValue(),
                Float.valueOf(point.y * 4.5F).intValue());
        mTmpMin = temp.toString();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //@{ added by wgx Usefulness:
        if (null == mPaintMax) {
            mPaintMax = new Paint();
            mPaintMax.setStyle(Paint.Style.FILL);
            mPaintMax.setStrokeWidth(10);
            mPaintMax.setTextSize(80);
            mPaintMax.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fontMetrics = mPaintMax.getFontMetrics();
            mTextHeight = fontMetrics.bottom - fontMetrics.top;
        }
        if (null != mTmpMax) {
            float textWidth = mPaintMax.measureText(mTmpMax);
            canvas.drawText(mTmpMax, mPointMax.x - textWidth / 2, mPointMax.y + mTextHeight / 2, mPaintMax);
//            Log.d(TAG, new StringBuilder()
//                    .append("\nmTmpMax = ").append(mTmpMax)
//                    .append("\nmPointMax.x = ").append(mPointMax.x)
//                    .append("\nmPointMax.y = ").append(mPointMax.y)
//                    .toString());
        }
        //}@ end wgx
    }
}
