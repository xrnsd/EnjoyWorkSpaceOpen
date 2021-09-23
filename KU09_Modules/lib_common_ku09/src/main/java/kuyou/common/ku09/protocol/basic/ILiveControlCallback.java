package kuyou.common.ku09.protocol.basic;

/**
 * action :接口[安全帽模块生命管理]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public interface ILiveControlCallback {
    /**
     * action: 重启模块
     */
    public void rebootModule(int delayedMillisecond);
    
    /**
     * action: 重启设备
     * 
     * @param isAutoBoot ,默认为true，true表示为关机后自动启动
     */
    public void rebootDevice(boolean isAutoBoot);
}
