package com.kuyou.ft.basic.event;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-17 <br/>
 * </p>
 */
public class EventTestItemResult {
    private boolean mResult = false;
    private int mTestId = -1;

    public EventTestItemResult(int testId, boolean result) {
        mTestId = testId;
        mResult = result;
    }

    public boolean isResult() {
        return mResult;
    }

    public int getTestId() {
        return mTestId;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("result = ").append(mResult)
                .append("test_id = ").append(mTestId)
                .toString();
    }
}
