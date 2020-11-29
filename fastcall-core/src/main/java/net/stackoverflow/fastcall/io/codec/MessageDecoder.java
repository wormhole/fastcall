package net.stackoverflow.fastcall.io.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import net.stackoverflow.fastcall.io.model.*;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 报文解码
 *
 * @author wormhole
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    public static final Logger log = LoggerFactory.getLogger(MessageDecoder.class);

    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        Message message = new Message();
        Header header = new Header();
        frame.readBytes(header.getMagic(), 0, 8);
        header.setVersion(frame.readShort());
        header.setLength(frame.readInt());
        header.setType(frame.readByte());

        int size = frame.readInt();
        Map<String, String> attachment = new HashMap<>();

        MessagePack messagePack = new MessagePack();
        for (int i = 0; i < size; i++) {
            int keySize = frame.readInt();
            byte[] keyBytes = new byte[keySize];
            frame.readBytes(keyBytes, 0, keySize);
            String key = new String(keyBytes, "UTF-8");

            int valueSize = frame.readInt();
            byte[] valueBytes = new byte[valueSize];
            frame.readBytes(valueBytes, 0, valueSize);
            String value = new String(keyBytes, "UTF-8");
            attachment.put(key, value);
        }
        header.setAttachment(attachment);
        message.setHeader(header);

        int bodySize = frame.readInt();
        if (bodySize > 0) {
            byte[] bodyBytes = new byte[bodySize];
            frame.readBytes(bodyBytes, 0, bodySize);
            if (header.getType() == MessageType.AUTH_RESPONSE.value()) {
                byte body = bodyBytes[0];
                message.setBody(body);
            } else if (header.getType() == MessageType.BUSINESS_REQUEST.value()) {
                CallRequest request = messagePack.read(bodyBytes, CallRequest.class);
                message.setBody(request);
            } else if (header.getType() == MessageType.BUSINESS_RESPONSE.value()) {
                CallResponse response = messagePack.read(bodyBytes, CallResponse.class);
                message.setBody(response);
            }
        }
        log.debug("Receive: {}", message);
        return message;
    }
}
