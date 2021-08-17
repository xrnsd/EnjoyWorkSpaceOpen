package kuyou.common.ku09.key;

/**
 * action :终端自定义按键事件监听器
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
