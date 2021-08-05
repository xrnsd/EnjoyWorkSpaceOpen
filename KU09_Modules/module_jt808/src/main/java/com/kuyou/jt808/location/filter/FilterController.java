package com.kuyou.jt808.location.filter;

import android.content.Context;
import android.location.Location;

import com.kuyou.jt808.location.base.HMLocationProvider.IOnLocationChangeListener;
import com.kuyou.jt808.location.filter.base.IFilterCallBack;
import com.kuyou.jt808.location.filter.base.TrackPoint;
import com.kuyou.jt808.location.filter.base.TrajectoryFilter;
import com.kuyou.jt808.location.filter.fluctuation.TrajectoryFluctuationFilter;
import com.kuyou.jt808.location.filter.kalman.TrajectoryKalmanFilter;

/**
 * action :轨迹滤波器管理器
 * <p>
 * remarks: 负债滤波器的策略配置，不同滤波器间的数据衔接，滤波后的轨迹点回调 <br/>
 * author: wuguoxian <br/>
 * date: 21-7-6 <br/>
 * </p>
 */
public class FilterController implements IFilterCallBack {
    protected final String TAG = "com.kuyou.jt808.location.filter > " + this.getClass().getSimpleName();

    private TrajectoryFilter mTrajectoryFluctuationFilter, mTrajectoryKalmanFilter;
    private IOnLocationChangeListener mOnLocationChangeListener;

    private static FilterController sMain;
    private int mFilterPolicy = -1;

    private IFilterCallBack mFilterCallBack;

    private FilterController() {

    }

    public static FilterController getInstance(Context context) {
        if (null == sMain) {
            sMain = new FilterController();
            sMain.initFilters(context);
        }
        return sMain;
    }

    private void initFilters(Context context) {
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
                //policy |= TrajectoryFluctuationFilter.POLICY_FILTER_BEARING_SPEED;
                //policy |= TrajectoryFluctuationFilter.POLICY_FILTER_ALTITUDE;
                return policy;
            }
        });
    }

    public FilterController setFilterCallBack(IFilterCallBack filterCallBack) {
        mFilterCallBack = filterCallBack;
        return FilterController.this;
    }

    @Override
    public void filter(Location location) {
        if (isEnableFilterByPolicy(POLICY_FILTER_KALMAN)) {
            //第一次真实定位，跳过过滤
            if(null!=mFilterCallBack
                    && !mFilterCallBack.isValidLocation()
                    && null != mOnLocationChangeListener){
                mOnLocationChangeListener.onLocationChange(location);
            }
            
            mTrajectoryKalmanFilter.filter(new TrackPoint(location));
            
        } else if (isEnableFilterByPolicy(POLICY_FILTER_FLUCTUATION)) {
            //第一次真实定位，跳过过滤
            if(null!=mFilterCallBack
                    && !mFilterCallBack.isValidLocation()
                    && null != mOnLocationChangeListener){
                mOnLocationChangeListener.onLocationChange(location);
            }
            
            mTrajectoryFluctuationFilter.filter(new TrackPoint(location));
            
        } else if (null != mOnLocationChangeListener) {
            mOnLocationChangeListener.onLocationChange(location);
        }
    }

    @Override
    public void setLocationChangeListener(IOnLocationChangeListener listener) {
        mOnLocationChangeListener = listener;
    }

    @Override
    public int getFilterPolicy() {
        if (-1 == mFilterPolicy){
            if(null != mFilterCallBack){
                mFilterPolicy = mFilterCallBack.getFilterPolicy();
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

    public static interface IFilterCallBack {
        public int getFilterPolicy();

        public boolean isValidLocation();
    }
}
