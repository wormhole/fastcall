package net.stackoverflow.fastcall.demo.api;

public interface SayService {

    String say(String content);

    void say(Integer content);

    String say();

}
