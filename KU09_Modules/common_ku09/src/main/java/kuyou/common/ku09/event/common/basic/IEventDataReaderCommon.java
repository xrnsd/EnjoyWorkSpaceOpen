package kuyou.common.ku09.event.common.basic;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-8 <br/>
 * </p>
 */
public interface IEventDataReaderCommon<T> {
    public T setEventType(T item, int val);

    public T setPowerStatus(T item, int val);

    public T setResult(T item, int val);

    public T setResult(T item, boolean val);
}
