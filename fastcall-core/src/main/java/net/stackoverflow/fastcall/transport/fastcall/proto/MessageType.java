package net.stackoverflow.fastcall.transport.fastcall.proto;

/**
 * 消息类型
 *
 * @author wormhole
 */
public enum MessageType {

    /**
     * 认证请求
     */
    AUTH_REQUEST((byte) 0),

    /**
     * 认证响应
     */
    AUTH_RESPONSE((byte) 1),

    /**
     * 心跳检测请求
     */
    HEARTBEAT_PING((byte) 2),

    /**
     * 心跳检测响应
     */
    HEARTBEAT_PONG((byte) 3),

    /**
     * 业务请求
     */
    BUSINESS_REQUEST((byte) 4),

    /**
     * 业务响应
     */
    BUSINESS_RESPONSE((byte) 5);

    private byte type;

    MessageType(byte type) {
        this.type = type;
    }

    public byte value() {
        return type;
    }

    public static MessageType valueOf(byte type) {
        MessageType messageType = null;
        for (MessageType value : values()) {
            if (value.value() == type) {
                messageType = value;
            }
        }
        return messageType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MessageType{");
        sb.append("type=").append(type);
        sb.append(", name=").append(name());
        sb.append('}');
        return sb.toString();
    }
}
