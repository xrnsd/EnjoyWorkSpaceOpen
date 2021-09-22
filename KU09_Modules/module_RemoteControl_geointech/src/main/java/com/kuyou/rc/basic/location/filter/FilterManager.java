package com.kuyou.rc.basic.location.filter;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.kuyou.rc.basic.location.filter.fluctuation.TrajectoryFluctuationFilter;
import com.kuyou.rc.basic.location.provider.HMLocationProvider;
import com.kuyou.rc.basic.location.provider.HMLocationProvider.IOnLocationChangeListener;
import com.kuyou.rc.basic.location.filter.kalman.TrajectoryKalmanFilter;

/**
 * action :轨迹滤波器管理器
 * <p>
 * remarks: 负债滤波器的策略配置，不同滤波器间的数据衔接，滤波后的轨迹点回调 <br/>
 * author: wuguoxian <br/>
 * date: 21-7-6 <br/>
 * </p>
 */
public abstract class FilterManager implements IFilterCallBack {
    protected final static String TAG = "com.kuyou.rc.basic.location.filter > FilterManager";

    private TrajectoryFilter mTrajectoryFluctuationFilter, mTrajectoryKalmanFilter;
    private HMLocationProvider.IOnLocationChangeListener mOnLocationChangeListener;

    private int mFilterPolicy = -1;

    private IFilterPolicyCallBack mFilterPolicyCallBack;

    protected abstract boolean isValidLocation();

    public FilterManager initFilters(Context context) {
        mTrajectoryKalmanFilter = new TrajectoryKalmanFilter(context.getApplicationContext(), new TrajectoryFilter.OnDataFilterListener() {
            @Override
            public void onDataAfterFilter(TrackPoint point) {
                if (isEnableFilterByPolicy(POLICY_FILTER_FLUCTUATION)) {
                    mTrajectoryFluctuationFilter.filter(point);
                } else if (null != mOnLocationChangeListener) {
                    mOnLocationChangeListener.onLocationChange(point.toLocation());
                }
            }
        });
        mTrajectoryFluctuationFilter = new TrajectoryFluctuationFilter(new TrajectoryFluctuationFilter.OnDataFilterControl() {
            @Override
            public void onDataAfterFilter(TrackPoint point) {
                if (null != mOnLocationChangeListener) {
                    mOnLocationChangeListener.onLocationChange(point.toLocation());
                }
            }

            @Override
            public int getDataFilterPolicy() {
                int policy = 0;
                policy |= TrajectoryFluctuationFilter.POLICY_FILTER_SPEED;
                policy |= TrajectoryFluctuationFilter.POLICY_FILTER_BEARING_SPEED;
                policy |= TrajectoryFluctuationFilter.POLICY_FILTER_ALTITUDE;
                return policy;
            }
        });
        return FilterManager.this;
    }

    public FilterManager setFilterPolicyCallBack(IFilterPolicyCallBack filterPolicyCallBack) {
        mFilterPolicyCallBack = filterPolicyCallBack;
        return FilterManager.this;
    }

    @Override
    public void filter(Location location) {
        Log.d(TAG, "filter > ");
        if (isEnableFilterByPolicy(POLICY_FILTER_KALMAN)) {
            Log.d(TAG, "filter > POLICY_FILTER_KALMAN ");
//            //第一次真实定位，跳过过滤
//            if (isValidLocation()
//                    && null != mOnLocationChangeListener) {
//                Log.d(TAG, "filter > 第一次跳过");
//                mOnLocationChangeListener.onLocationChange(location);
//            }

            mTrajectoryKalmanFilter.filter(new TrackPoint(location));

        } else if (isEnableFilterByPolicy(POLICY_FILTER_FLUCTUATION)) {
            Log.d(TAG, "filter > POLICY_FILTER_FLUCTUATION ");
//            //第一次真实定位，跳过过滤
//            if (isValidLocation()
//                    && null != mOnLocationChangeListener) {
//                Log.d(TAG, "filter > 第一次跳过");
//                mOnLocationChangeListener.onLocationChange(location);
//            }

            mTrajectoryFluctuationFilter.filter(new TrackPoint(location));

        } else if (null != mOnLocationChangeListener) {
            Log.d(TAG, "filter > none : cancel filter ");
            mOnLocationChangeListener.onLocationChange(location);
        }
    }

    @Override
    public IFilterCallBack setLocationChangeListener(IOnLocationChangeListener listener) {
        mOnLocationChangeListener = listener;
        return FilterManager.this;
    }

    @Override
    public int getFilterPolicy() {
        if (-1 == mFilterPolicy) {
            if (null != mFilterPolicyCallBack) {
                mFilterPolicy = mFilterPolicyCallBack.getFilterPolicy();
            }
        }
        return mFilterPolicy;
    }

    private boolean isEnableFilterByPolicy(final int policyFlag) {
        mFilterPolicy = getFilterPolicy();
        if (-1 != mFilterPolicy)
            return (mFilterPolicy & policyFlag) != 0;
        return false;
    }

    public static interface IFilterPolicyCallBack {
        public int getFilterPolicy();
    }
}
