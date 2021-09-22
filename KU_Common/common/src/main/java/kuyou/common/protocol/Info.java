package kuyou.common.protocol;

import androidx.annotation.Nullable;

/**
 * action :协议解码项[抽象]
 * <p>
 * author: wuguoxian <br/>
 * date: 21-1-12 <br/>
 * <p>
 */
public abstract class Info<T>  {
    protected final String TAG = "kuyou.common.protocol > Info";

    public abstract String getTitle();

    public abstract int getFlag();

    public abstract int getCmdCode();

    public abstract boolean isSuccess();

    public abstract void reset();

    public abstract void parse(byte[] data, T listener);

    public abstract byte[] getBody();

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Info) {
            return ((Info) obj).toString().equals(Info.this.toString());
        }
        return false;
    }

}
