package com.kuyou.ft.basic.touch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * action :触屏测试[画X字测试]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-10 <br/>
 * </p>
 */
public class TouchObliqueTestView extends View {
    
    protected final String TAG = "com.kuyou.ft.basic.touch > TouchObliqueTestView";

    private Paint mPaint, mTargetPaint, mRectPaint;
    private ArrayList<RectFill> mRect = new ArrayList<RectFill>();
    private ArrayList<ArrayList<PT>> mLines = new ArrayList<ArrayList<PT>>();
    private ArrayList<PT> mLineCur;

    private int mHeaderBottom;
    private boolean mCurDown;
    private int mCurX;
    private int mCurY;

    private int mRectSize = 0;
    private int mRectWidth = 0;
    private int mRectHeight = 0;
    private int mLineLen = 0;
    private int mScreenWidth = 1080;
    private int mScreenHeight = 1920;
    protected ICallBackTouchest mCallBack;

    public TouchObliqueTestView(Context c) {
        super(c);
        init();
    }

    public TouchObliqueTestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchObliqueTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TouchObliqueTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setARGB(255, 255, 0, 0);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);

        mRectPaint = new Paint();
        mRectPaint.setARGB(255, 0, 255, 0);
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStrokeWidth(10);

        mTargetPaint = new Paint();
        mTargetPaint.setAntiAlias(true);
        mTargetPaint.setARGB(255, 0, 0, 0);
        mTargetPaint.setStyle(Paint.Style.STROKE);
        mTargetPaint.setStrokeWidth(4);

        initWindowParams();
        initRectFill();
        mRectSize = mRect.size();
    }

    private void initWindowParams() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        switch (mScreenWidth) {
            case 480:
                mRectWidth = 35;
                mRectHeight = 35;
                mLineLen = 35;
                break;
            case 540:
                mRectWidth = 40;
                mRectHeight = 40;
                mLineLen = 40;
                break;
            case 720:
                mRectWidth = 40;
                mRectHeight = 40;
                mLineLen = 55;
                break;
            case 1080:
            case 1200:
                mRectWidth = 70;
                mRectHeight = 70;
                mLineLen = 80;
                break;
            case 768:
                mRectWidth = 40;
                mRectHeight = 40;
                mLineLen = 55;
                break;
            default:
                break;
        }
        Log.d(TAG, "SCREEN_WIDTH = " + mScreenWidth + "; SCREEN_HEIGHT = " + mScreenHeight);
        Log.d(TAG, "RECT_WIDTH = " + mRectWidth + "; RECT_HEIGHT = " + mRectHeight + "; LINE_LEN = " + mLineLen);
    }

    private void initRectFill() {
        int i = 0;
        int len = (int) Math.sqrt(mScreenHeight * mScreenHeight + mScreenWidth * mScreenWidth);
        int num = (len - mRectWidth) / mRectWidth;
        int width = mScreenWidth / num;
        int height = mScreenHeight / num;
        Log.d(TAG, "width = " + width + "; height = " + height + "; num = " + num);
        //720 *1280
        for (i = 0; i < num; i++) {
            RectFill rect = new RectFill(i * width, (i) * height, i * width + mLineLen, (i + 1) * height);
            rect.setRectFillFlag(false);
            rect.setDirectionFlag(0);
            mRect.add(rect);
        }
        for (i = 0; i < num; i++) {
            RectFill rect1 = new RectFill(i * width, mScreenHeight - ((i + 1) * height), i * width + mLineLen, mScreenHeight - i * height);
            rect1.setRectFillFlag(false);
            rect1.setDirectionFlag(1);
            mRect.add(rect1);
        }
    }

    public void setCallBack(ICallBackTouchest callBack) {
        mCallBack = callBack;
    }

    protected void dispatchResult(boolean val) {
        if (null == mCallBack) {
            Log.e(TAG, "dispatchResult > process fail : mCallBack is null");
            return;
        }
        mCallBack.onResultTouchTest(TouchObliqueTestView.this, val);
    }


    public void judgeRectFill(int x, int y) {
        int i;
        for (i = 0; i < mRectSize; i++) {
            RectFill rect = mRect.get(i);
            if ((x > rect.left) && (x < rect.right) && (y > rect.top) && (y < rect.bottom)) {
                rect.setRectFillFlag(true);
            }
        }
    }

    public boolean allRectFilled() {
        int i;
        for (i = 0; i < mRectSize; i++) {
            RectFill rect = mRect.get(i);
            if (rect.getRectFillFlag() == false) {
                Log.d(TAG, "allRectFilled---false---i=" + i);
                return false;
            }
        }
        Log.d(TAG, "allRectFilled---true---");
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int len = (int) Math.sqrt(mScreenHeight * mScreenHeight + mScreenWidth * mScreenWidth);
        int num = (len - mRectWidth) / mRectWidth;
        int width = mScreenWidth / num;
        int height = mScreenHeight / num;
        Log.d(TAG, "width = " + width + "; height = " + height + "; num = " + num);
        for (int i = 0; i < mRectSize; i++) {
            RectFill rect = mRect.get(i);
            if (rect.getRectFillFlag()) {
                int rect_right = rect.right;
                int rect_bottom = rect.bottom;
                if (rect_right > mScreenWidth - mRectWidth) {
                    rect_right = mScreenWidth;
                }
                if (rect_bottom > mScreenHeight - mRectHeight) {
                    rect_bottom = mScreenHeight;
                }

                if (rect.getDirectionFlag() == 0) {
                    canvas.drawLine(rect.left, rect.top, rect.left + mLineLen, rect.top, mRectPaint);
                    canvas.drawLine(rect.left, rect.top, rect.left + width, rect.bottom, mRectPaint);
                    canvas.drawLine(rect.left + mLineLen, rect.top, rect.left + width + mLineLen, rect.bottom, mRectPaint);
                    canvas.drawLine(rect.left + width, rect.bottom, rect.left + width + mLineLen, rect.bottom, mRectPaint);
                } else {
                    canvas.drawLine(rect.left, rect.top, rect.left + mLineLen, rect.top, mRectPaint);
                    canvas.drawLine(rect.left, rect.top, rect.left - width, rect.bottom, mRectPaint);
                    canvas.drawLine(rect.left + mLineLen, rect.top, rect.left - width + mLineLen, rect.bottom, mRectPaint);
                    canvas.drawLine(rect.left - width, rect.bottom, rect.left - width + mLineLen, rect.bottom, mRectPaint);
                }
            } else {
                if (rect.getDirectionFlag() == 0) {
                    canvas.drawLine(rect.left, rect.top, rect.left + mLineLen, rect.top, mPaint);
                    canvas.drawLine(rect.left, rect.top, rect.left + width, rect.bottom, mPaint);
                    canvas.drawLine(rect.left + mLineLen, rect.top, rect.left + width + mLineLen, rect.bottom, mPaint);
                    canvas.drawLine(rect.left + width, rect.bottom, rect.left + width + mLineLen, rect.bottom, mPaint);
                } else {
                    canvas.drawLine(rect.left, rect.top, rect.left + mLineLen, rect.top, mPaint);
                    canvas.drawLine(rect.left, rect.top, rect.left - width, rect.bottom, mPaint);
                    canvas.drawLine(rect.left + mLineLen, rect.top, rect.left - width + mLineLen, rect.bottom, mPaint);
                    canvas.drawLine(rect.left - width, rect.bottom, rect.left - width + mLineLen, rect.bottom, mPaint);
                }
            }
        }

        int lineSz = mLines.size();
        int k = 0;
        for (k = 0; k < lineSz; k++) {
            ArrayList<PT> m = mLines.get(k);

            float lastX = 0, lastY = 0;
            int sz = m.size();
            int i = 0;
            for (i = 0; i < sz; i++) {
                PT n = m.get(i);
                if (i > 0) {
                    canvas.drawLine(lastX, lastY, n.x, n.y, mTargetPaint);
                }

                lastX = n.x;
                lastY = n.y;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mLineCur = new ArrayList<PT>();
            mLines.add(mLineCur);
        }
        final int N = event.getHistorySize();
        for (int i = 0; i < N; i++) {
            mLineCur.add(new PT(event.getHistoricalX(i), event
                    .getHistoricalY(i)));
        }
        mLineCur.add(new PT(event.getX(), event.getY()));
        mCurDown = action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_MOVE;
        mCurX = (int) event.getX();
        mCurY = (int) event.getY();
        judgeRectFill(mCurX, mCurY);
        invalidate();
        if (action == MotionEvent.ACTION_UP) {
            if (allRectFilled()) {
                dispatchResult(true);
            }
        }
        return true;
    }

    public void Clear() {
        for (ArrayList<PT> m : mLines) {
            m.clear();
        }
        mLines.clear();
        invalidate();
    }

    public class PT {
        public Float x;
        public Float y;

        public PT(Float x, Float y) {
            this.x = x;
            this.y = y;
        }
    }

    public class RectFill {
        public int left;
        public int top;
        public int right;
        public int bottom;
        public int direction;
        private boolean isFillFlag = false;

        public RectFill(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;

        }

        public void setRectFillFlag(boolean flag) {
            isFillFlag = flag;
        }

        public boolean getRectFillFlag() {
            return isFillFlag;
        }

        public void setDirectionFlag(int flag) {
            direction = flag;
        }

        public int getDirectionFlag() {
            return direction;
        }
    }
}
