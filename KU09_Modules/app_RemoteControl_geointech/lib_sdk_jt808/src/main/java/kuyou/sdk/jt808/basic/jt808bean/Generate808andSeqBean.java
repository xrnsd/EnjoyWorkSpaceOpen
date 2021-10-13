package kuyou.sdk.jt808.basic.jt808bean;

public class Generate808andSeqBean {

    public Generate808andSeqBean(){}
    public Generate808andSeqBean(int seqNo, byte[] bytes) {
        this.seqNo = seqNo;
        this.bytes = bytes;
    }

    public Generate808andSeqBean(long seqNo, byte[] bytes, long timestamp) {
        this.seqNo = seqNo;
        this.bytes = bytes;
        this.timestamp = timestamp;
    }

    private long seqNo;
    private byte[] bytes;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
