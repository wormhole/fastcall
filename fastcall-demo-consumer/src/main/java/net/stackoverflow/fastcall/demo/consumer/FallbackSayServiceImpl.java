package net.stackoverflow.fastcall.demo.consumer;

import net.stackoverflow.fastcall.demo.api.ContentDTO;
import net.stackoverflow.fastcall.demo.api.SayService;

public class FallbackSayServiceImpl implements SayService {
    @Override
    public String say(String content) {
        return null;
    }

    @Override
    public String sayWithFallback(String content) {
        return "fallback " + content;
    }

    @Override
    public ContentDTO sayWithDTO(String content) {
        return null;
    }

    @Override
    public String sayWithTimeout(String content) {
        return null;
    }
}
