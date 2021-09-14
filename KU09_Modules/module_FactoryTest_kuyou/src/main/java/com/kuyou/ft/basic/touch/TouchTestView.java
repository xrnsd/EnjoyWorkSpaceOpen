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
 * action :触屏测试[画田字测试]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-10 <br/>
 * </p>
 */
public class TouchTestView extends View {

    protected final String TAG = "com.kuyou.ft.basic.touch > TouchTestView";

    protected int mRectSize = 0,
            mRectWidth = 0,
            mRectHeight = 0,
            mScreenWidth = 1080,
            mScreenHeight = 1920,
            mCurX,
            mCurY;

    protected ArrayList<ArrayList<PT>> mLines = new ArrayList<ArrayList<PT>>();
    protected ArrayList<RectFill> mRect = new ArrayList<RectFill>();
    protected ArrayList<PT> mLineCur;

    protected Paint mPaint, mTargetPaint, mRectPaint;

    protected ICallBackTouchest mCallBack;

    public TouchTestView(Context c) {
        super(c);
        init();
    }

    public TouchTestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TouchTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setARGB(255, 80, 80, 80);

        mRectPaint = new Paint();
        mRectPaint.setARGB(255, 0, 255, 0);

        mTargetPaint = new Paint();
        mTargetPaint.setAntiAlias(false);
        mTargetPaint.setARGB(255, 0, 0, 0);
        mTargetPaint.setStyle(Paint.Style.STROKE);
        mTargetPaint.setStrokeWidth(4);

        initWindowParams();
        initRectFill();
        mRectSize = mRect.size();
    }

    protected void initWindowParams() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        switch (mScreenWidth) {
            case 480:
                mRectWidth = 40;
                mRectHeight = 40;
                break;
            case 540:
                mRectWidth = 45;
                mRectHeight = 45;
                break;
            case 720:
            case 768:
                mRectWidth = 70;
                mRectHeight = 70;
                break;
            case 1080:
                mRectWidth = 100;
                mRectHeight = 100;
                break;
            case 1200:
                mRectWidth = 90;
                mRectHeight = 90;
                break;
            default:
                break;
        }
        Log.d(TAG, new StringBuilder("initWindowParams > ")
                .append("\n mScreenWidth = ").append(mScreenWidth)
                .append("\n mScreenHeight = ").append(mScreenHeight)
                .append("\n mRectWidthTouch = ").append(mRectWidth)
                .append("\n mRectHeightTouch = ").append(mRectHeight)
                .toString());
    }

    private void initRectFill() {
        int i = 0;

        //720 *1280
        for (i = 0; i < (mScreenWidth - 1 - mRectWidth); i = i + mRectWidth) {
            RectFill rect = new RectFill(i, 0);
            rect.setRectFillFlag(false);
            mRect.add(rect);
        }
        for (i = 0; i < (mScreenWidth - 1 - mRectWidth); i = i + mRectWidth) {
            RectFill rect = new RectFill(i, (mScreenHeight / 2 - mRectHeight / 2));
            rect.setRectFillFlag(false);
            mRect.add(rect);
        }
        for (i = 0; i < (mScreenWidth - 1 - mRectWidth); i = i + mRectWidth) {
            RectFill rect = new RectFill(i, (mScreenHeight - mRectHeight));
            rect.setRectFillFlag(false);
            mRect.add(rect);
        }

        for (i = mRectWidth; i < (mScreenHeight / 2 - mRectHeight / 2); i = i + mRectHeight) {
            RectFill rect = new RectFill(0, i);
            rect.setRectFillFlag(false);
            mRect.add(rect);

            RectFill rect1 = new RectFill((mScreenWidth / 2 - mRectWidth / 2), i);
            rect.setRectFillFlag(false);
            mRect.add(rect1);

            RectFill rect2 = new RectFill((mScreenWidth - mRectWidth), i);
            rect.setRectFillFlag(false);
            mRect.add(rect2);
        }

        for (i = (mScreenHeight / 2 + mRectWidth / 2); i < (mScreenHeight - mRectHeight); i = i + mRectHeight) {
            RectFill rect = new RectFill(0, i);
            rect.setRectFillFlag(false);
            mRect.add(rect);

            RectFill rect1 = new RectFill((mScreenWidth / 2 - mRectWidth / 2), i);
            rect.setRectFillFlag(false);
            mRect.add(rect1);

            RectFill rect2 = new RectFill((mScreenWidth - mRectWidth), i);
            rect.setRectFillFlag(false);
            mRect.add(rect2);
        }
        mRectSize = mRect.size();

        Log.d(TAG, new StringBuilder("initRectFill > ")
                .append("\n mRectSize = ").append(mRectSize)
                .toString());
    }

    public void setCallBack(ICallBackTouchest callBack) {
        mCallBack = callBack;
    }

    protected void dispatchResult(boolean val) {
        if (null == mCallBack) {
            Log.e(TAG, "dispatchResult > process fail : mCallBack is null");
            return;
        }
        mCallBack.onResultTouchTest(TouchTestView.this, val);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (RectFill rect : mRect) {
            if (!rect.getRectFillFlag()) {
                continue;
            }
            int rect_right = rect.x + mRectWidth;
            int rect_bottom = rect.y + mRectHeight;
            if (rect_right > mScreenWidth - mRectWidth) {
                rect_right = mScreenWidth;
            }
            if (rect_bottom > mScreenHeight - mRectHeight) {
                rect_bottom = mScreenHeight;
            }

            canvas.drawRect(rect.x, rect.y, rect_right, rect_bottom, mRectPaint);
        }

        canvas.drawLine(0, mRectWidth, (mScreenWidth - 1), mRectWidth, mPaint);
        canvas.drawLine(0, (mScreenHeight / 2 - mRectHeight / 2), (mScreenWidth - 1), (mScreenHeight / 2 - mRectHeight / 2), mPaint);
        canvas.drawLine(0, (mScreenHeight / 2 + mRectHeight / 2), (mScreenWidth - 1), (mScreenHeight / 2 + mRectHeight / 2), mPaint);
        canvas.drawLine(0, (mScreenHeight - mRectHeight), (mScreenWidth - 1), (mScreenHeight - mRectHeight), mPaint);

        canvas.drawLine(mRectWidth, 0, mRectWidth, (mScreenHeight - 1), mPaint);
        canvas.drawLine((mScreenWidth / 2 - mRectWidth / 2), 0, (mScreenWidth / 2 - mRectWidth / 2), (mScreenHeight - 1), mPaint);
        canvas.drawLine((mScreenWidth / 2 + mRectWidth / 2), 0, (mScreenWidth / 2 + mRectWidth / 2), (mScreenHeight - 1), mPaint);
        canvas.drawLine((mScreenWidth - mRectWidth), 0, (mScreenWidth - mRectWidth), (mScreenHeight - 1), mPaint);

        for (int i = mRectWidth; i < (mScreenHeight / 2 - mRectHeight / 2); i = i + mRectWidth) {
            canvas.drawLine(0, i, mRectWidth, i, mPaint);
            canvas.drawLine((mScreenWidth / 2 - mRectWidth / 2), i, (mScreenWidth / 2 + mRectWidth / 2), i, mPaint);
            canvas.drawLine((mScreenWidth - mRectWidth), i, (mScreenWidth - 1), i, mPaint);
        }

        for (int i = (mScreenHeight / 2 + mRectHeight / 2); i < (mScreenHeight - mRectHeight); i = i + mRectWidth) {
            canvas.drawLine(0, i, mRectWidth, i, mPaint);
            canvas.drawLine((mScreenWidth / 2 - mRectWidth / 2), i, (mScreenWidth / 2 + mRectWidth / 2), i, mPaint);
            canvas.drawLine((mScreenWidth - mRectWidth), i, (mScreenWidth - 1), i, mPaint);
        }

        for (int j = mRectWidth; j < (mScreenWidth / 2 - mRectWidth / 2); j = j + mRectWidth) {
            canvas.drawLine(j, 0, j, mRectWidth, mPaint);
            canvas.drawLine(j, (mScreenHeight / 2 - mRectHeight / 2), j, (mScreenHeight / 2 + mRectHeight / 2), mPaint);
            canvas.drawLine(j, (mScreenHeight - mRectHeight), j, (mScreenHeight - 1), mPaint);
        }

        for (int j = (mScreenWidth / 2 + mRectWidth / 2); j < (mScreenWidth - mRectWidth); j = j + mRectWidth) {
            canvas.drawLine(j, 0, j, mRectWidth, mPaint);
            canvas.drawLine(j, (mScreenHeight / 2 - mRectHeight / 2), j, (mScreenHeight / 2 + mRectHeight / 2), mPaint);
            canvas.drawLine(j, (mScreenHeight - mRectHeight), j, (mScreenHeight - 1), mPaint);
        }
        PT n;
        for (ArrayList<PT> m : mLines) {
            float lastX = 0, lastY = 0;
            for (int i = 0, sz = m.size(); i < sz; i++) {
                n = m.get(i);
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

    public void clear() {
        for (ArrayList<PT> m : mLines) {
            m.clear();
        }
        mLines.clear();
        invalidate();
    }

    protected void judgeRectFill(int x, int y) {
        for (RectFill rect : mRect) {
            if ((x > rect.x) && (x < (rect.x + mRectWidth)) && (y > rect.y) && (y < (rect.y + mRectHeight))) {
                rect.setRectFillFlag(true);
            }
        }
    }

    protected boolean allRectFilled() {
        for (RectFill rect : mRect) {
            if (!rect.getRectFillFlag()) {
                return false;
            }
        }
        return true;
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
        public int x;
        public int y;
        private boolean isFillFlag = false;

        public RectFill(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setRectFillFlag(boolean flag) {
            isFillFlag = flag;
        }

        public boolean getRectFillFlag() {
            return isFillFlag;
        }
    }
}
