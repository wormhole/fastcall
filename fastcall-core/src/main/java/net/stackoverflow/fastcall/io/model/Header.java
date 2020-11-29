package net.stackoverflow.fastcall.io.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用层协议报文头部
 *
 * @author wormhole
 */
public class Header {

    /**
     * 固定魔术字 fastcall
     */
    private byte[] magic = {'f', 'a', 's', 't', 'c', 'a', 'l', 'l'};

    /**
     * 协议版本号 0.1
     */
    private short version = 0x0001;

    /**
     * 协议长度(除去magic,version,length)
     */
    private int length;

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 附加信息
     */
    private Map<String, String> attachment = new HashMap<>();

    public byte[] getMagic() {
        return magic;
    }

    public void setMagic(byte[] magic) {
        this.magic = magic;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Map<String, String> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, String> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        StringBuilder msb = new StringBuilder();
        for (byte b : magic) {
            msb.append((char) b);
        }
        int majorVersion = (version & 0xff00) >> 8;
        int subVersion = (version & 0x00ff);
        String version = Integer.toHexString(majorVersion) + "." + Integer.toHexString(subVersion);

        final StringBuffer sb = new StringBuffer("Header{");
        sb.append("magic=");
        sb.append(msb.toString());
        sb.append(", version=").append(version);
        sb.append(", length=").append(length);
        sb.append(", type=").append(MessageType.valueOf(type));
        sb.append(", attachment=").append(attachment);
        sb.append('}');
        return sb.toString();
    }
}
