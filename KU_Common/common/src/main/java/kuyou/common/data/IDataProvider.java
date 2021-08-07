package kuyou.common.data;

/**
 * action :解析监听器[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-14 <br/>
 * </p>
 */
public interface IDataProvider<T> {
    public void setListener(T listener);
}
