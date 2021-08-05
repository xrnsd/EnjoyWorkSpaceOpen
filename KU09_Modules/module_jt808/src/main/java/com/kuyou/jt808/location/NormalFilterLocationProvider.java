package com.kuyou.jt808.location;

import android.content.Context;
import android.location.Location;

import com.kuyou.jt808.location.base.HMLocationProvider;
import com.kuyou.jt808.location.filter.FilterController;
import com.kuyou.jt808.location.filter.base.IFilterCallBack;

/**
 * action :默认使用滤波器的位置提供器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-6 <br/>
 * </p>
 */
public class NormalFilterLocationProvider extends HMLocationProvider {
    protected final String TAG = "com.kuyou.jt808.location > NormalFilterLocationProvider";

    private static NormalFilterLocationProvider sMain;

    protected IFilterCallBack mFilterCallBack;

    private IOnLocationChangeListener mLocationChangePublicListener = null, //外部模块的位置
            mLocationChangeFilterListener; //滤波器的处理之后的位置监听器

    private NormalFilterLocationProvider(Context context) {
        super(context);
    }

    public static NormalFilterLocationProvider getInstance(Context context) {
        if (null == sMain) {
            sMain = new NormalFilterLocationProvider(context);
        }
        return sMain;
    }

    @Override
    public void setLocationChangeListener(IOnLocationChangeListener locationChangeListener) {
        super.setLocationChangeListener(locationChangeListener);
        if (null != mFilterCallBack) {
            initLocationChangeFilterListener();
            mFilterCallBack.setLocationChangeListener(mLocationChangeFilterListener);
        }
    }

    private void initLocationChangeFilterListener() {
        if (null != mLocationChangeFilterListener)
            return;
        mLocationChangeFilterListener = new IOnLocationChangeListener() {
            @Override
            public void onLocationChange(Location location) {
                mLocation = location;
                if(null!=mLocationChangePublicListener)
                    mLocationChangePublicListener.onLocationChange(location);
            }
        };
    }

    public IFilterCallBack getFilter() {
        return mFilterCallBack;
    }

    public void setFilter(IFilterCallBack filterCallBack) {
        mFilterCallBack = filterCallBack;
        initLocationChangeFilterListener();
        mFilterCallBack.setLocationChangeListener(mLocationChangeFilterListener);
    }

    @Override
    public void dispatchLocation(Location location) {
        if (null != mFilterCallBack) {
            mFilterCallBack.filter(location);
            return;
        }
        super.dispatchLocation(location);
    }

    @Override
    protected void init() {
        setFilter(FilterController
                .getInstance(mContext)
                .setFilterCallBack(new FilterController.IFilterCallBack() {
                    @Override
                    public int getFilterPolicy() {
                        int policy = 0;
                        //policy |= IFilterCallBack.POLICY_FILTER_FLUCTUATION;
                        //policy |= IFilterCallBack.POLICY_FILTER_KALMAN;
                        return policy;
                    }

                    @Override
                    public boolean isValidLocation() {
                        return NormalFilterLocationProvider.this.isValidLocation();
                    }
                }));
    }
}
