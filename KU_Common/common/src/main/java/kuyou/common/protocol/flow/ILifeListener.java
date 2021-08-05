package kuyou.common.protocol.flow;

import android.os.Bundle;

/**
 * action :生命周期回调接口
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-13 <br/>
 * </p>
 */
public interface ILifeListener {

    void onCreate(Bundle bundle);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}