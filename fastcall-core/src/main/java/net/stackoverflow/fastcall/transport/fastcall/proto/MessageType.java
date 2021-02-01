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

    private final byte value;

    MessageType(byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public static MessageType valueOf(byte value) {
        MessageType messageType = null;
        for (MessageType type : values()) {
            if (type.value == value) {
                messageType = type;
            }
        }
        return messageType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MessageType{");
        sb.append("value=").append(value);
        sb.append(", name=").append(name());
        sb.append('}');
        return sb.toString();
    }
}
