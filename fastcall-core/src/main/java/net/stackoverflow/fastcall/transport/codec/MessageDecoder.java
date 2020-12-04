package net.stackoverflow.fastcall.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.proto.*;
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

    private SerializeManager serializeManager;

    public MessageDecoder(SerializeManager serializeManager) {
        super(1024 * 1024, 10, 4, 0, 0);
        this.serializeManager = serializeManager;
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
                byte body = serializeManager.deserialize(bodyBytes, byte.class);
                message.setBody(body);
            } else if (header.getType() == MessageType.BUSINESS_REQUEST.value()) {
                RpcRequest request = serializeManager.deserialize(bodyBytes, RpcRequest.class);
                message.setBody(request);
            } else if (header.getType() == MessageType.BUSINESS_RESPONSE.value()) {
                RpcResponse response = serializeManager.deserialize(bodyBytes, RpcResponse.class);
                message.setBody(response);
            }
        }
        log.trace("Receive: {}", message);
        return message;
    }
}
