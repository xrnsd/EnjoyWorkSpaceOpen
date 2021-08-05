package kuyou.sdk.jt808.base.jt808bean;

public class Header808Bean {

    /**
     * mobile : 15651821852
     * msgID : 2
     * bodyAttr : {"split":false,"encrypt":false,"bodyLength":0}
     * seqNO : 1
     */

    private String mobile;
    private int msgID;
    private BodyAttrBean bodyAttr;
    private int seqNO;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getMsgID() {
        return msgID;
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    public BodyAttrBean getBodyAttr() {
        return bodyAttr;
    }

    public void setBodyAttr(BodyAttrBean bodyAttr) {
        this.bodyAttr = bodyAttr;
    }

    public int getSeqNO() {
        return seqNO;
    }

    public void setSeqNO(int seqNO) {
        this.seqNO = seqNO;
    }

    public static class BodyAttrBean {
        /**
         * split : false
         * encrypt : false
         * bodyLength : 0
         */

        private boolean split;
        private boolean encrypt;
        private int bodyLength;

        public boolean isSplit() {
            return split;
        }

        public void setSplit(boolean split) {
            this.split = split;
        }

        public boolean isEncrypt() {
            return encrypt;
        }

        public void setEncrypt(boolean encrypt) {
            this.encrypt = encrypt;
        }

        public int getBodyLength() {
            return bodyLength;
        }

        public void setBodyLength(int bodyLength) {
            this.bodyLength = bodyLength;
        }

        @Override
        public String toString() {
            return new StringBuilder("BodyAttrBean{")
                    .append("split=").append(split)
                    .append(", encrypt=").append(encrypt)
                    .append(", bodyLength=").append(bodyLength)
                    .append("}").toString();
        }
    }

    @Override
    public String toString() {
       return new StringBuilder("Header808Bean{")
                .append("mobile=").append(mobile)
                .append(", msgID=").append(String.format("0x%04x", msgID))
                .append(", bodyAttr=").append(bodyAttr.toString())
                .append(", seqNO=").append(seqNO)
                .append("}")
                .toString();
    }
}
