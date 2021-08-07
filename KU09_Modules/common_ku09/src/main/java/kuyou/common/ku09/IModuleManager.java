package kuyou.common.ku09;

/**
 * action :接口[安全帽模块生命管理]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public interface IModuleManager {
    /**
     * action: 重启模块
     */
    public void reboot(int delayedMillisecond);
}
