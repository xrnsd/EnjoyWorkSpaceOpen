package com.kuyou.rc.handler;

import android.util.Log;

import com.kuyou.rc.handler.photo.UploadUtil;
import com.kuyou.rc.protocol.jt808extend.Jt808ExtendProtocolCodec;
import com.kuyou.rc.protocol.jt808extend.basic.SicBasic;
import com.kuyou.rc.protocol.jt808extend.item.SicPhotoTake;
import com.kuyou.rc.protocol.jt808extend.item.SicPhotoUploadReply;

import java.io.File;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventPhotoTakeResult;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.event.rc.EventLocalDeviceStatus;
import kuyou.common.ku09.event.rc.EventPhotoUploadRequest;
import kuyou.common.ku09.event.rc.EventPhotoUploadResult;
import kuyou.common.ku09.event.rc.EventSendToRemoteControlPlatformRequest;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.handler.BasicEventHandler;

/**
 * action :协处理器[照片上传]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-12 <br/>
 * </p>
 */
public class PhotoUploadHandler extends BasicEventHandler {

    protected final String TAG = "com.kuyou.rc.handler > PhotoUploadHandler";

    protected Jt808ExtendProtocolCodec mJt808ExtendProtocolCodec;

    private boolean isRemoteControlPlatformConnected = false;

    protected Jt808ExtendProtocolCodec getJt808ExtendProtocolCodec() {
        if (null == mJt808ExtendProtocolCodec) {
            mJt808ExtendProtocolCodec = Jt808ExtendProtocolCodec.getInstance(getContext());
        }
        return mJt808ExtendProtocolCodec;
    }

    protected SicBasic getSingleInstructionParserByEventCode(RemoteEvent event) {
        if (null == event) {
            Log.e(TAG, "getSicByEventCode > process fail : event is null");
            return null;
        }
        final int eventCode = event.getCode();
        for (SicBasic singleInstructionParse : getJt808ExtendProtocolCodec().getSicBasicList()) {
            if (singleInstructionParse.isMatchEventCode(eventCode)) {
                return singleInstructionParse;
            }
        }
        Log.e(TAG, "getSicByEventCode > process fail : event is invalid =" + eventCode);
        return null;
    }

    protected void sendToRemoteControlPlatform(byte[] body) {
        dispatchEvent(new EventSendToRemoteControlPlatformRequest()
                .setMsg(body)
                .setRemote(false));
    }


    @Override
    protected void initHandleEventCodeList() {
        registerHandleEvent(EventRemoteControl.Code.LOCAL_DEVICE_STATUS, false);
        registerHandleEvent(EventRemoteControl.Code.PHOTO_UPLOAD_RESULT, false);

        registerHandleEvent(EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT, true);
        registerHandleEvent(EventRemoteControl.Code.PHOTO_UPLOAD_REQUEST, true);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
            case EventRemoteControl.Code.LOCAL_DEVICE_STATUS:
                isRemoteControlPlatformConnected =
                        EventLocalDeviceStatus.Status.ON_LINE == EventLocalDeviceStatus.getDeviceStatus(event);
                break;

            case EventRemoteControl.Code.PHOTO_UPLOAD_RESULT:
                boolean isUploadSuccess = EventPhotoUploadResult.isResultSuccess(event);
                if (isUploadSuccess) {
                    Log.i(TAG, "onReceiveEventNotice > 照片上传成功");
                } else {
                    Log.w(TAG, "onReceiveEventNotice > 照片上传失败");
                }
                if (!isRemoteControlPlatformConnected) {
                    break;
                }
                SicBasic singleInstructionParserPUR = getSingleInstructionParserByEventCode(event);
                if (null == singleInstructionParserPUR) {
                    break;
                }
                byte[] msgPUR = ((SicPhotoUploadReply) singleInstructionParserPUR)
                        .setEventType(EventPhotoUploadRequest.getEventType(event))
                        .setResult(isUploadSuccess ? SicPhotoUploadReply.ResultCode.SUCCESS :
                                SicPhotoUploadReply.ResultCode.LOCAL_DEVICE_UPLOAD_FAIL)
                        .getBody(SicBasic.BodyConfig.RESULT);
                sendToRemoteControlPlatform(msgPUR);

                break;

            case EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT:
                Log.i(TAG, "onReceiveEventNotice > 拍照状态上传");
                if (!EventPhotoTakeResult.isResultSuccess(event)) {
                    SicBasic singleInstructionParserPTR = getSingleInstructionParserByEventCode(event);
                    if (null == singleInstructionParserPTR) {
                        break;
                    }
                    byte[] msgPTR = ((SicPhotoTake) singleInstructionParserPTR)
                            .setEventType(EventPhotoTakeResult.getEventType(event))
                            .setResult(SicPhotoTake.ResultCode.LOCAL_DEVICE_SHOOT_FAIL)
                            .getBody(SicBasic.BodyConfig.RESULT);
                    sendToRemoteControlPlatform(msgPTR);
                }
                break;

            case EventRemoteControl.Code.PHOTO_UPLOAD_REQUEST:
                Log.i(TAG, "onReceiveEventNotice > 开始上传照片");
                final String filePath = EventPhotoUploadRequest.getImgFilePath(event);
                File imgFile = new File(filePath);

                boolean isUploadReady = true;
                if (!imgFile.exists()) {
                    isUploadReady = false;
                    Log.e(TAG, "onReceiveEventNotice > 开始上传照片 > process fail : 照片不存在 = " + filePath);
                }
                if (!isRemoteControlPlatformConnected) {
                    Log.w(TAG, "onReceiveEventNotice > 开始上传照片 > process fail : 未联网");
                    play("上传失败，请检查网络链接");
                }
                if (!isUploadReady) {
                    dispatchEvent(new EventPhotoUploadResult()
                            .setResult(false)
                            .setEventType(EventPhotoUploadRequest.getEventType(event))
                            .setRemote(false));
                    break;
                }
                UploadUtil.getInstance()
                        .setOnUploadCallBack(new UploadUtil.OnUploadCallBack() {
                            @Override
                            public UploadUtil.UploadConfig getConfig() {
                                return new UploadUtil.UploadConfig()
                                        .setStrDeviceId(PhotoUploadHandler.this.getDeviceConfig().getDevId())
                                        .setStrServerUrl(PhotoUploadHandler.this.getDeviceConfig().getRemotePhotoServerAddress())
                                        .setFileImageLocal(imgFile);
                            }

                            @Override
                            public void onUploadFinish(int resultCode) {
                                boolean isUploadSuccess = UploadUtil.ResultCode.UPLOAD_SUCCESS == resultCode;
                                Log.i(TAG, "onReceiveEventNotice > onUploadFinish > " + (isUploadSuccess ? "上传成功" : "上传失败"));
                                dispatchEvent(new EventPhotoUploadResult()
                                        .setResult(isUploadSuccess)
                                        .setEventType(EventPhotoUploadRequest.getEventType(event))
                                        .setRemote(false));
                            }
                        })
                        .uploadImageBySubThread();
                break;

            default:
                return false;
        }
        return true;
    }
}
