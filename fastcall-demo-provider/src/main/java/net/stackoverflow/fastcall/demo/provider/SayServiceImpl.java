package net.stackoverflow.fastcall.demo.provider;

import net.stackoverflow.fastcall.annotation.FastcallFallback;
import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.demo.api.SayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FastcallService(group = "group-1")
public class SayServiceImpl implements SayService {

    private static final Logger log = LoggerFactory.getLogger(SayServiceImpl.class);

    @Override
    public String say(String content) {
        return "hello " + content;
    }

    @Override
    @FastcallFallback(method = "sayFallback")
    public String sayWithFallback(String content)  {
        throw new RuntimeException("fall back");
    }

    @Override
    public String sayWithException(String content) {
        throw new RuntimeException("uncheck exception");
    }

    public String sayFallback(String content) {
        return "fallback " + content;
    }
}
