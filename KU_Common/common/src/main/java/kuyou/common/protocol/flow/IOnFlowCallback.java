package kuyou.common.protocol.flow;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 21-1-6 <br/>
 * <p>
 */
public interface IOnFlowCallback {
    public void onStart();

    public void onStop();

    public boolean onStep(byte[] cmd);

    public void onStepResult(final String result);

    public void onReady();

    public void onFail();
}
