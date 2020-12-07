package net.stackoverflow.fastcall.demo.api;

public interface SayService {

    String say(String content);

    String sayWithCheckException(String content) throws Exception;

    String sayWithUncheckException(String content);

}
