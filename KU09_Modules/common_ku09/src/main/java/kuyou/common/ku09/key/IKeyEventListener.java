package kuyou.common.ku09.key;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public interface IKeyEventListener {
    public void onKeyClick(int keyCode);

    public void onKeyDoubleClick(int keyCode);

    public void onKeyLongClick(int keyCode);
}
