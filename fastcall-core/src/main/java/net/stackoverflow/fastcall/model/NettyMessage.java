package net.stackoverflow.fastcall.model;

/**
 * 应用层协议报文
 *
 * @author wormhole
 */
public class NettyMessage {

    /**
     * 协议头
     */
    private Header header;

    /**
     * 消息体
     */
    private Object body;

    public NettyMessage() {

    }

    public NettyMessage(MessageType type) {
        header = new Header();
        header.setType(type.value());
    }

    public NettyMessage(MessageType type, Object body) {
        this.header = new Header();
        this.header.setType(type.value());
        this.body = body;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NettyMessage{");
        sb.append("header=").append(header);
        sb.append(", body=").append(body);
        sb.append('}');
        return sb.toString();
    }
}
