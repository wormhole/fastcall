package net.stackoverflow.fastcall.demo.consumer;

import net.stackoverflow.fastcall.annotation.FastcallReference;
import net.stackoverflow.fastcall.demo.api.ContentDTO;
import net.stackoverflow.fastcall.demo.api.SayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FastcallController {

    @FastcallReference(group = "group-1", version = "0.0.1")
    private SayService sayService;

    @GetMapping("/say")
    public String say(@RequestParam("content") String content) {
        return sayService.say(content);
    }

    @GetMapping("/say_with_fallback")
    public String sayWithFallback(@RequestParam("content") String content) {
        return sayService.sayWithFallback(content);
    }

    @GetMapping("/say_with_dto")
    public ContentDTO sayWithDTO(@RequestParam("content") String content) {
        return sayService.sayWithDTO(content);
    }
}
