package kuyou.sdk.jt808.base.jt808coding;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.bytes.BitOperator;
import kuyou.common.bytes.ByteUtils;
import kuyou.sdk.jt808.base.RemoteControlDeviceConfig;
import kuyou.sdk.jt808.base.jt808bean.Jt808MapLocation;
import kuyou.sdk.jt808.base.jt808utils.TimeUtils;

public class JT808Directive {

    /**
     * 终端注册
     *
     * @param manufacturerId 制造商 ID
     * @param terminalModel  终端型号
     * @param terminalId     终端 ID
     * @return
     */
    public static byte[] register(String manufacturerId, String terminalModel, String terminalId) {
        //省域 ID
        byte[] p = BitOperator.numToByteArray(31, 2);
        //省域 市县域 ID
        byte[] c = BitOperator.numToByteArray(72, 2);
        //制造商 ID
        byte[] mId = manufacturerId.getBytes();
        //终端型号
        byte[] tmId = terminalModel.getBytes();
        //终端 ID
        byte[] tId = terminalId.getBytes();
        //车牌颜色
        byte[] s = {0};
        // 车辆标识
        byte[] vin = "LSFAM63".getBytes();

        return ByteUtils.byteMergerAll(p, c, mId, tmId, tId, s, vin);
    }

    /**
     * 位置信息汇报 ***废弃
     *
     * @param alarmType  报警/事件类型 0x03：车距过近报警 0x08：自动刹车报警
     * @param terminalId 终端id
     * @param lat        纬度
     * @param lng        经度
     * @param speed      速度
     * @return
     */
    public static byte[] locationReport(byte alarmType, String terminalId, long lat, long lng, int alarmId, double speed) {
        byte[] alarm = {0, 0, 0, 0};
        //32 位二进制 从高到低位
        String radix2State = "00000000000000000000000000000010";
        //2进制转int 在装4个字节的byte
        byte[] state = ByteUtils.int32ToBytes(Integer.parseInt(radix2State, 2));
        //DWORD经纬度
        byte[] latb = ByteUtils.longToDword(lat);
        byte[] lngb = ByteUtils.longToDword(lng);
        byte[] gaoChen = {0, 0};
        byte[] speedb = BitOperator.numToByteArray((int) (speed * 10), 2);
        byte[] orientation = {0, 0};
        //bcd时间
        byte[] bcdTime = TimeUtils.getBcdTime();
        //位置信息附加项
        byte[] gjfzjsData = advancedDriverAssistance(alarmType, terminalId, latb, lngb, bcdTime, alarmId, speed);
        return ByteUtils.byteMergerAll(alarm, state, latb, lngb, gaoChen, speedb, orientation, bcdTime, gjfzjsData);
    }

    /**
     * 参考文档《江苏道路运输车辆主动安全智能防控系统通讯协议规范》
     * 表 4‑15高级驾驶辅助报警信息数据格式
     * 生成位置附加项数据 ***废弃
     *
     * @param alarmType  报警/事件类型 (0x03,0x08)
     * @param terminalId 终端id
     * @param lat        纬度
     * @param lng        经度
     * @param alarmId    报警id
     * @param speed      速度
     */
    public static byte[] advancedDriverAssistance(byte alarmType, String terminalId, byte[] lat, byte[] lng,
                                                  byte[] bcdTime, int alarmId, double speed) {
        //报警ID转4个字节的byte
        byte[] id = ByteUtils.int32ToBytes(alarmId);
        //速度转byte
        byte sb = ByteUtils.int2Byte((int) (speed * 10));
        byte[] gjjsfz = {0x01, alarmType, 0x04, 0, 0, 0, 0, 0, sb, 0, 0};
        //车辆状态
        byte[] state = {0, 0};
        byte[] alarmData = ByteUtils.byteMergerAll(id, gjjsfz, lat, lng, bcdTime, state);
        //报警标识号格式
        byte[] terminal = terminalId.getBytes();
        byte[] end = {0, 0, 0};
        byte[] bytes = ByteUtils.byteMergerAll(alarmData, terminal, bcdTime, end);
        //附加信息id 长度
        byte[] extensionId = {0x64, (byte) bytes.length};
        return ByteUtils.byteMergerAll(extensionId, bytes);
    }

    /**
     * 单独上报经纬度 ***废弃
     *
     * @param lat 纬度
     * @param lng 经度
     * @return
     */
    public static byte[] reportLatLng(double lat, double lng) {
        byte[] alarm = {0, 0, 0, 0};
        //32 位二进制 从高到低位
        String radix2State = "00000000000000000000000000000010";
        //2进制转int 在装4个字节的byte
        byte[] state = ByteUtils.int32ToBytes(Integer.parseInt(radix2State, 2));
        //DWORD经纬度
        double pow106 = Math.pow(10, 6);
        double lat106 = lat * pow106;
        double lng106 = lng * pow106;
        byte[] latb = ByteUtils.longToDword(Math.round(lat106));
        byte[] lngb = ByteUtils.longToDword(Math.round(lng106));
        byte[] gaoChen = {0, 0};
        byte[] speedb = {0, 0};
        byte[] orientation = {0, 0};
        //bcd时间
        byte[] bcdTime = TimeUtils.getBcdTime();
        //位置信息附加项
        return ByteUtils.byteMergerAll(alarm, state, latb, lngb, gaoChen, speedb, orientation, bcdTime);
    }

    /**
     * 位置信息汇报
     *
     * @param lat      纬度
     * @param lng      经度
     * @param altitude 高度
     * @param speed    速度
     * @param bearing  角度/方向
     * @param accuracy 精度
     * @param time     时间
     * @return
     */
    public static byte[] reportLocation(RemoteControlDeviceConfig config, double lat, double lng, double altitude, float speed, float bearing, float accuracy, String time) {
        byte[] alarm = {0, 0, 0, 0};
//        byte[] state = {0, 0, 0, 0};
        byte[] state = stateLocationData(lat, lng);
        //DWORD经纬度
        double pow106 = Math.pow(10, 6);
        double lat106 = lat * pow106;
        double lng106 = lng * pow106;
        byte[] latb = ByteUtils.longToDword(Math.round(lat106));
        byte[] lngb = ByteUtils.longToDword(Math.round(lng106));
        // WORD 高度 速度 方向
        byte[] gaoChen = BitOperator.numToByteArray((int) altitude, 2);
        byte[] speedb = BitOperator.numToByteArray((int) (speed * 3.6), 2);
        byte[] orientation = BitOperator.numToByteArray((int) bearing, 2);
        //bcd时间
        byte[] bcdTime = ByteUtils.str2Bcd(time);
        //位置信息附加项
        byte[] additionLocation = additionLocationData(config.getOrderId(), (int) altitude, String.valueOf(accuracy));
        return ByteUtils.byteMergerAll(alarm, state, latb, lngb, gaoChen, speedb, orientation, bcdTime, additionLocation);
    }

    public static byte[] reportLocation(RemoteControlDeviceConfig config, Jt808MapLocation jtData) {
        return reportLocation(config, jtData.getLat()
                , jtData.getLng()
                , jtData.getAccuracy()
                , jtData.getSpeed()
                , jtData.getBearing()
                , jtData.getAccuracy()
                , jtData.getTime());
    }

    /**
     * 位置信息状态项
     *
     * @param
     * @return
     */
    private static byte[] stateLocationData(double lat, double lng) {
        String state = "00";
        state = state + (lat < 0 ? "1" : "0");
        state = state + (lng < 0 ? "1" : "0");
        state = state + "0000000000000000000000000000";
        byte[] stateByte = ByteUtils.int2DWord(Integer.parseInt(state, 2));
        return stateByte;
    }

    /**
     * 位置信息附加项
     *
     * @param order    订单号
     * @param altitude 高度
     * @param accuracy 精度
     * @return
     */
    private static byte[] additionLocationData(String order, int altitude, String accuracy) {
        byte[] orderType = new byte[2]; //订单号
        orderType[0] = (byte) (0xE1);
        orderType[1] = ByteUtils.int2Byte(order.getBytes().length);
        byte[] orderMsg = ByteUtils.byteMergerAll(orderType, order.getBytes());

        byte[] altitudeType = new byte[3]; //高度的正负
        altitudeType[0] = (byte) (0xE2);
        altitudeType[1] = (byte) (0x01);
        altitudeType[2] = (byte) (altitude < 0 ? 0x31 : 0x30);

        byte[] accuracyType = new byte[2]; //订单号
        accuracyType[0] = (byte) (0xE3);
        accuracyType[1] = ByteUtils.int2Byte(accuracy.getBytes().length);
        byte[] accuracyMsg = ByteUtils.byteMergerAll(accuracyType, accuracy.getBytes());

        return ByteUtils.byteMergerAll(orderMsg, altitudeType, accuracyMsg);
    }

    /**
     * 批量位置信息汇报
     *
     * @return
     */
    public static byte[] batchReportLocation(List<byte[]> locations) {
        byte[] counts = ByteUtils.int2Word(locations.size()); //数据项个数
        byte[] batchType = {0}; //位置数据类型 0：正常位置批量汇报，1：盲区补报

        List<byte[]> formatLocations = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            //取出每一条的位置数据，然后拼接（位置汇报数据体长度+位置汇报数据体）
            byte[] cLocation = locations.get(i);
            byte[] clocationLength = ByteUtils.int2Word(cLocation.length); //位置汇报数据体长度
            formatLocations.add(ByteUtils.byteMergerAll(clocationLength, cLocation));
        }

        byte[] batchLocations = ByteUtils.byteMergerAll(formatLocations); //位置汇报数据体
        return ByteUtils.byteMergerAll(counts, batchType, batchLocations);
    }

    /**
     * 发送心跳
     */
    public static byte[] heartPkg(RemoteControlDeviceConfig config) {
        return JTT808Coding.generate808(0x0002, config, new byte[]{});
    }
}
