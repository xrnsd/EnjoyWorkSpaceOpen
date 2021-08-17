package com.kuyou.rc.protocol.base;

/**
 * action :KU09
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * 部分扩展协议指令名称项定义说明
 *  S2C : 服务器发送客户端接收
 *  C2S : 客户端发送服务器接收
 * </p>
 */
public interface JT808ExtensionProtocol {
    
    // ========================  SERVER_CMD ================================
    /**
     * action : 文本信息
     * flow：server > client
     */
    public final static int S2C_REQUEST_TEXT_MESSAGE = 0x8300;
    /**
     * action : 拍照和拍照后上传
     * flow：server > client
     */
    public final static int S2C_REQUEST_PHOTO_TAKE_AND_PHOTO_UPLOAD = 0x8F02;
    /**
     * action : 音视频操作
     * flow：server > client
     */
    public final static int S2C_REQUEST_AUDIO_VIDEO_PARAMETERS = 0x8F03;

    // ========================  S2C_RESULT ================================
    /**
     * action : 连接回复
     * flow：server > client
     */
    public final static int S2C_RESULT_CONNECT_REPLY = 0x8001;
    /**
     * action : 鉴权回复
     * flow：server > client
     */
    public final static int S2C_RESULT_AUTHENTICATION_REPLY = 0x8fff;
    /**
     * action : 上传照片结果回复
     * flow：server > client
     */
    public final static int S2C_RESULT_PHOTO_UPLOAD_REPLY = 0x8F01;

    // ========================  C2S_REQUEST ================================
    /**
     * action : 请求音视频参数
     * flow：client > server
     */
    public final static int C2S_REQUEST_PHOTO_UPLOAD = 0x0F02;
    /**
     * action : 位置汇报
     * flow：client > server
     */
    public final static int C2S_REQUEST_LOCATION_REPORT = 0x0200;
    /**
     * action : 位置汇报[批量]
     * flow：client > server
     */
    public final static int C2S_REQUEST_LOCATION_BATCH_REPORT = 0x0704;


    // ========================  C2S_RESULT ================================
    /**
     * action : 终端对拍照和拍照后上传处理回复
     * flow：client > server
     */
    public final static int C2S_RESULT_PHOTO_TAKE_AND_PHOTO_UPLOAD = 0x0F01;
    /**
     * action : 终端对服务器音视频请求处理回复
     * flow：client > server
     */
    public final static int C2S_RESULT_AUDIO_VIDEO_PARAMETERS = 0x0F03;
}
