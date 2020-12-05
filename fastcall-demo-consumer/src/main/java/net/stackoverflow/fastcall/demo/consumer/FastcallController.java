package net.stackoverflow.fastcall.demo.consumer;

import net.stackoverflow.fastcall.annotation.FastcallReference;
import net.stackoverflow.fastcall.demo.api.SayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FastcallController {

    @FastcallReference(group = "group-1")
    private SayService sayService;

    @GetMapping("/say1")
    public String say(@RequestParam("content") String content) {
        return sayService.say(content);
    }

    @GetMapping("/say2")
    public String say(@RequestParam("num") Integer num) {
        sayService.say(num);
        return "success";
    }

    @GetMapping("/say3")
    public String say() {
        return sayService.say();
    }
}
