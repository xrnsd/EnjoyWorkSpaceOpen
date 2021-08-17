package com.kuyou.rc.protocol.item;

import com.kuyou.rc.protocol.InstructionParserListener;

import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import com.kuyou.rc.protocol.base.JT808ExtensionProtocol;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public class SicPhotoTake extends SicPhoto {
    protected final String TAG = "com.kuyou.rc.protocol > SicPhotoTake";

    @Override
    public String getTitle() {
        return "拍照";
    }

    @Override
    public int getFlag() {
        return JT808ExtensionProtocol.S2C_REQUEST_PHOTO_TAKE_AND_PHOTO_UPLOAD;
    }

    @Override
    public int getMatchEventCode() {
        return EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT;
    }

    @Override
    public void parse(byte[] data, InstructionParserListener listener) {
        super.parse(data,listener);
        if (null != listener)
            listener.onRemote2LocalExpand(SicPhotoTake.this);
    }
}
