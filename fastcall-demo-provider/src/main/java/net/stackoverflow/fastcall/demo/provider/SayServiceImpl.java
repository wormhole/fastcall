package net.stackoverflow.fastcall.demo.provider;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.demo.api.SayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@FastcallService(group = "group-1")
public class SayServiceImpl implements SayService {

    private static final Logger log = LoggerFactory.getLogger(SayServiceImpl.class);

    @Override
    public String say(String content) {
        return "hello " + content;
    }

    @Override
    public String sayWithCheckException(String content) throws Exception {
        throw new IOException("check exception");
    }

    @Override
    public String sayWithUncheckException(String content) {
        throw new RuntimeException("uncheck exception");
    }
}
