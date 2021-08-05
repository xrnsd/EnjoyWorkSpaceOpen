package com.kuyou.rc.location;

import android.content.Context;
import android.location.Location;

import com.kuyou.rc.location.filter.FilterController;
import com.kuyou.rc.location.filter.base.IFilterCallBack;

/**
 * action :默认使用滤波器的位置提供器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-6 <br/>
 * </p>
 */
public class NormalFilterLocationProvider extends HMLocationProvider {
    protected final String TAG = "com.kuyou.rc.location > NormalFilterLocationProvider";

    private static NormalFilterLocationProvider sMain;

    protected IFilterCallBack mFilterCallBack;

    private NormalFilterLocationProvider(Context context) {
        super(context);
    }

    public static NormalFilterLocationProvider getInstance(Context context) {
        if (null == sMain) {
            sMain = new NormalFilterLocationProvider(context);
        }
        return sMain;
    }

    public IFilterCallBack getFilter() {
        return mFilterCallBack;
    }

    public void setFilter(IFilterCallBack filterCallBack) {
        mFilterCallBack = filterCallBack;
        mFilterCallBack.setLocationChangeListener(new IOnLocationChangeListener() {
            @Override
            public void onLocationChange(Location location) {
               NormalFilterLocationProvider.this.dispatchLocationSuper(location);
            }
        });
    }

    @Override
    public void dispatchLocation(Location location) {
        if (null != mFilterCallBack) {
            mFilterCallBack.filter(location);
            return;
        }
        super.dispatchLocation(location);
    }
    
    private void dispatchLocationSuper(Location location) {
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