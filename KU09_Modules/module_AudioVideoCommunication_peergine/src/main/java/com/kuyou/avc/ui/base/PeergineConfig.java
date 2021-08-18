package com.kuyou.avc.ui.base;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class PeergineConfig {
    private String mServerAddress;
    private String mDevCollectingEndId;

    public String getServerAddress() {
        return mServerAddress;
    }

    public PeergineConfig setServerAddress(String serverAddress) {
        mServerAddress = serverAddress;
        return PeergineConfig.this;
    }

    public String getDevCollectingEndId() {
        return mDevCollectingEndId;
    }

    public PeergineConfig setDevCollectingEndId(String devCollectingEndId) {
        mDevCollectingEndId = devCollectingEndId;
        return PeergineConfig.this;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("\nmServerAddress='").append(mServerAddress)
                .append("\nmDevCollectingEndId='").append(mDevCollectingEndId)
                .toString();
    }
}
