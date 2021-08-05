package kuyou.sdk.jt808.base;

/**
 * action :KU09
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class JT808ExtensionProtocol {
    public static interface SERVER_CMD {
        /**
         * action : 文本信息下发
         */
        public final static int TEXT_DELIVERY = 0x8300;
        /**
         * action : 拍照,上传
         */
        public final static int TAKE_PHOTO_UPLOAD = 0x8F02;
        /**
         * action : 音视频参数下发
         */
        public final static int AUDIO_VIDEO_PARAMETERS_DELIVERY = 0x8F03;
    }

    public static interface SERVER_ANSWER {
        /**
         * action : 连接回复
         */
        public final static int CONNECT_REPLY = 0x8001;
        /**
         * action : 鉴权回复
         */
        public final static int AUTHENTICATION_REPLY = 0x8fff;
        /**
         * action : 照片上传结束
         */
        public final static int PHOTO_UPLOAD_FINISH = 0x8F01;
    }

    public static interface CLIENT_REQUEST {
        /**
         * action : 请求音视频参数
         */
        public final static int PHOTO_UPLOAD = 0x0F02;
        /**
         * action : 位置汇报
         */
        public final static int LOCATION_REPORT = 0x0200;
    }

    public static interface CLIENT_ANSWER {
        /**
         * action : 照片上传
         */
        public final static int PHOTO_UPLOAD = 0x0F01;
        /**
         * action : 照片上传
         */
        public final static int AUDIO_VIDEO_PARAMETERS_DELIVERY = 0x0F03;
    }

    public static interface RESULT {
        /**
         * action : 成功
         */
        public static final int SUCCESS = 1;

        /**
         * action : 失败
         */
        public static final int FAIL = 0;
    }
}
