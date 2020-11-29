package net.stackoverflow.fastcall.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

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

    public static Object json2bean(String json, Class<?> clazz) throws JsonProcessingException {
        Object obj = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            obj = mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("json2bean", e);
        }
        return obj;
    }
}
