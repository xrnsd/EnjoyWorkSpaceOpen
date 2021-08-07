package kuyou.common.ku09;

/**
 * action :接口[安全帽电源状态监听器]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-26 <br/>
 * </p>
 */
public interface IPowerStatusListener {
    public void onPowerStatus(int status);
}
