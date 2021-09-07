package com.kuyou.rc.protocol.jt808extend.basic;

import com.kuyou.rc.protocol.jt808extend.item.SicAudioVideo;
import com.kuyou.rc.protocol.jt808extend.item.SicPhotoTake;
import com.kuyou.rc.protocol.jt808extend.item.SicTextMessage;

import kuyou.sdk.jt808.basic.jt808bean.JTT808Bean;

/**
 * action :JT808扩展指令编解码器解码结果监听器
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
    public void onRemote2LocalExpand(SicTextMessage instruction);
}
