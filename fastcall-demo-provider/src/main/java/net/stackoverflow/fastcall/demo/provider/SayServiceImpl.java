package net.stackoverflow.fastcall.demo.provider;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.demo.api.SayService;

@FastcallService(group = "group-1")
public class SayServiceImpl implements SayService {
    @Override
    public String say(String content) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello " + content;
    }
}
