package com.kuyou.rc.protocol;

import com.kuyou.rc.protocol.item.SicAudioVideo;
import com.kuyou.rc.protocol.item.SicPhotoTake;
import com.kuyou.rc.protocol.item.SicPhotoUploadReply;
import com.kuyou.rc.protocol.item.SicTextMessage;

import kuyou.sdk.jt808.base.jt808bean.JTT808Bean;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public interface InstructionParserListener {
    public void onRemote2LocalExpandFail(Exception e);

    public void onRemote2LocalBasic(JTT808Bean bean, byte[] data);

    public void onRemote2LocalExpand(SicAudioVideo instruction);
    public void onRemote2LocalExpand(SicPhotoTake instruction);
    public void onRemote2LocalExpand(SicPhotoUploadReply instruction);
    public void onRemote2LocalExpand(SicTextMessage instruction);
}
