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

    @GetMapping("/say")
    public String say(@RequestParam("content") String content) {
        return sayService.say(content);
    }

    @GetMapping("/say_with_check_exception")
    public String sayWithCheckException(@RequestParam("content") String content) throws Exception {
        return sayService.sayWithCheckException(content);
    }

    @GetMapping("/say_with_uncheck_exception")
    public String sayWithUncheckException(@RequestParam("content") String content) {
        return sayService.sayWithUncheckException(content);
    }
}
