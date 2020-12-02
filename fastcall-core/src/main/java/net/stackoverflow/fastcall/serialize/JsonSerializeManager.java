package net.stackoverflow.fastcall.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonSerializeManager implements SerializeManager {

    private static final Logger log = LoggerFactory.getLogger(JsonSerializeManager.class);

    @Override
    public byte[] serialize(Object object) {
        byte[] bytes = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            bytes = mapper.writeValueAsBytes(object);
        } catch (Exception e) {
            log.error("serialize", e);
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        T obj = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            obj = mapper.readValue(bytes, clazz);
        } catch (Exception e) {
            log.error("deserialize", e);
        }
        return obj;
    }
}
