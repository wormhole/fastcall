package net.stackoverflow.fastcall.serialize;

/**
 * 序列化接口
 *
 * @author wormhole
 */
public interface SerializeManager {

    byte[] serialize(Object object);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
