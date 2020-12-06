package net.stackoverflow.fastcall.demo.provider;

import net.stackoverflow.fastcall.autoconfigure.annotation.EnableFastcall;
import net.stackoverflow.fastcall.demo.api.SayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFastcall(basePackages = {"net.stackoverflow.fastcall.demo.provider"})
public class FastcallDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastcallDemoProviderApplication.class, args);
    }

}
