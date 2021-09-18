package com.kuyou.rc.basic.location.provider;

import android.content.Context;
import android.location.Location;

import com.kuyou.rc.basic.location.filter.FilterManager;
import com.kuyou.rc.basic.location.filter.IFilterCallBack;

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

    public NormalFilterLocationProvider setFilter(FilterManager.IFilterPolicyCallBack filterCallBack) {
        FilterManager fm = new FilterManager() {
            @Override
            protected boolean isValidLocation() {
                return NormalFilterLocationProvider.this.isEffectivePositioning();
            }
        };
        fm.initFilters(getContext());
        fm.setFilterPolicyCallBack(filterCallBack);
        fm.setLocationChangeListener(new IOnLocationChangeListener() {
            @Override
            public void onLocationChange(Location location) {
                NormalFilterLocationProvider.this.dispatchLocationSuper(location);
            }
        });
        mFilterCallBack = fm;
        return NormalFilterLocationProvider.this;
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

    }
}
