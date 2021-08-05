package kuyou.common.serialport.base;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-14 <br/>
 * </p>
 */
public interface IDataProviderListener {
    public void onReceiveData(byte[] data);
}
