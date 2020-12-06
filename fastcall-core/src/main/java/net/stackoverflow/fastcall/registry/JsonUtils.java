package net.stackoverflow.fastcall.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Json工具类
 *
 * @author wormhole
 */
public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * bean转json
     *
     * @param obj
     * @return
     */
    public static String bean2json(Object obj) {
        String json = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("bean2json", e);
        }
        return json;
    }

    /**
     * json转bean
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T json2bean(String json, Class<T> clazz) {
        T obj = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            obj = mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("json2bean", e);
        }
        return obj;
    }
}
