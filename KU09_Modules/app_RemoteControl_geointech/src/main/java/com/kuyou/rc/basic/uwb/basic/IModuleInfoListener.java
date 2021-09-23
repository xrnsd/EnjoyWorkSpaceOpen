package com.kuyou.rc.basic.uwb.basic;

/**
 * action :接口[模块状态]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-1 <br/>
 * </p>
 */
public interface IModuleInfoListener {
    public void onGetModuleId(int devId);

    public void onSetModuleIdFinish(int devId, boolean result);
}
