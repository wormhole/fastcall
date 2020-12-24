package net.stackoverflow.fastcall.transport.fastcall.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.fastcall.proto.Header;
import net.stackoverflow.fastcall.transport.fastcall.proto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 报文编码
 *
 * @author wormhole
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    public static final Logger log = LoggerFactory.getLogger(MessageEncoder.class);

    private final SerializeManager serializeManager;

    public MessageEncoder(SerializeManager serializeManager){
        this.serializeManager = serializeManager;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf buff) throws Exception {
        Header header = message.getHeader();
        buff.writeBytes(header.getMagic());
        buff.writeShort(header.getVersion());
        buff.writeInt(header.getLength());
        buff.writeByte(header.getType());
        buff.writeInt(header.getAttachment().size());

        for (Map.Entry<String, String> entry : header.getAttachment().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            byte[] keyBytes = key.getBytes("UTF-8");
            buff.writeInt(keyBytes.length);
            buff.writeBytes(keyBytes);

            byte[] valueBytes = value.getBytes("UTF-8");
            buff.writeInt(valueBytes.length);
            buff.writeBytes(valueBytes);
        }

        if (message.getBody() != null) {
            Object body = message.getBody();
            byte[] bodyBytes = serializeManager.serialize(body);
            buff.writeInt(bodyBytes.length);
            buff.writeBytes(bodyBytes);
        } else {
            buff.writeInt(0);
        }

        message.getHeader().setLength(buff.readableBytes() - 14);
        buff.setInt(10, message.getHeader().getLength());
        log.trace("Send: {}", message);
    }
}
