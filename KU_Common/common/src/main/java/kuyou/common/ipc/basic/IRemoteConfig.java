package kuyou.common.ipc.basic;

/**
 * action :IPC框架的通用配置
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-24 <br/>
 * </p>
 */
public interface IRemoteConfig {

    public final static String ACTION_FLAG_FRAME_EVENT = "action.remote.event.frame";
    public final static String ACTION_FLAG_EVENT = "action.remote.event.public";

    public final static String PERMISSION_FLAG = "remote.event.permission";

    //0~2047
    public final static int FLAG_CODE = 0;

    //FLAG_CODE+0 ~ FLAG_CODE+2047
    public static interface Code {
        /**
         * action:IPC服务连接成功
         */
        public final static int BIND_IPC_SERVICE_SUCCESS = FLAG_CODE + 0;
        /**
         * action:IPC服务连接超时
         */
        public final static int BIND_IPC_SERVICE_TIME_OUT = FLAG_CODE + 1;
        /**
         * action:IPC服务连接断开
         */
        public final static int UNBIND_IPC_SERVICE = FLAG_CODE + 2;

        /**
         * action:模块远程注册成功
         */
        public final static int MODULE_REMOTE_REGISTER_SUCCESS = FLAG_CODE + 11;
        /**
         * action:模块远程注册失败
         */
        public final static int MODULE_REMOTE_REGISTER_FAIL = FLAG_CODE + 12;


        /**
         * action:框架事件标识[保留]
         */
        public final static int FRAME_FLAG = FLAG_CODE + 2047;
    }

}
