package kuyou.common.ku09.event.rc.basic;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-8 <br/>
 * </p>
 */
public interface IRemoteControl<T> extends IEventCodeGroupRemoteControl {
    public Class<T> setMediaType(int val);

    public Class<T> setPlatformType(int val);
}
