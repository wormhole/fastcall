package net.stackoverflow.fastcall.demo.provider;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.demo.api.ContentDTO;
import net.stackoverflow.fastcall.demo.api.SayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FastcallService(group = "group-1", version = "0.0.1")
public class SayServiceImpl implements SayService {

    private static final Logger log = LoggerFactory.getLogger(SayServiceImpl.class);

    @Override
    public String say(String content) {
        return "hello " + content;
    }

    @Override
    public String sayWithFallback(String content) {
        throw new RuntimeException("fallback");
    }

    @Override
    public ContentDTO sayWithDTO(String content) {
        return new ContentDTO(content, -1);
    }

    @Override
    public String sayWithTimeout(String content) {
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            log.error("", e);
        }
        return "hello " + content;
    }
}
