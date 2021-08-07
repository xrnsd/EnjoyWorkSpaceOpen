package kuyou.common.ipc.base;

import android.os.Bundle;

public interface IRemoteEventHandler extends IRemoteConfig {
    public void remoteEvent2LocalEvent(Bundle data);

    public String getLocalModulePackageName();
}
