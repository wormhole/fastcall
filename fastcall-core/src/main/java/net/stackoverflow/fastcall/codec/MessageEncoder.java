package net.stackoverflow.fastcall.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.stackoverflow.fastcall.model.Message;
import org.msgpack.MessagePack;
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

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf buff) throws Exception {
        buff.writeBytes(message.getHeader().getMagic());
        buff.writeShort(message.getHeader().getVersion());
        buff.writeInt(message.getHeader().getLength());
        buff.writeByte(message.getHeader().getType());
        buff.writeInt(message.getHeader().getAttachment().size());

        MessagePack messagePack = new MessagePack();

        for (Map.Entry<String, String> entry : message.getHeader().getAttachment().entrySet()) {
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
            byte[] bodyBytes = messagePack.write(body);
            buff.writeInt(bodyBytes.length);
            buff.writeBytes(bodyBytes);
        } else {
            buff.writeInt(0);
        }

        message.getHeader().setLength(buff.readableBytes() - 14);
        buff.setInt(10, message.getHeader().getLength());
        log.debug("Send: {}", message);
    }
}
