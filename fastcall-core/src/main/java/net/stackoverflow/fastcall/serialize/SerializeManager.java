package net.stackoverflow.fastcall.serialize;

public interface SerializeManager {

    byte[] serialize(Object object);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
