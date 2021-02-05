package net.stackoverflow.fastcall.demo.api;

public interface SayService {

    String say(String content);

    String sayWithFallback(String content);

    ContentDTO sayWithDTO(String content);

    String sayWithTimeout(String content);

    String[] sayWithArray(String[] content);

}
