package kuyou.common.protocol;

import androidx.annotation.Nullable;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 21-1-12 <br/>
 * <p>
 */
public abstract class Info<T>  {
    protected final String TAG = this.getClass().getSimpleName() + "_123456";

    public abstract String geTitle();

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
