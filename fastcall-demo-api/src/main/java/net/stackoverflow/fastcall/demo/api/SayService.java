package net.stackoverflow.fastcall.demo.api;

public interface SayService {

    String say(String content);

    String sayWithFallback(String content);

    String sayWithException(String content);

}
