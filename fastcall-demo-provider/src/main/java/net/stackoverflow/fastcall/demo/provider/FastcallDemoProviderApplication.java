package net.stackoverflow.fastcall.demo.provider;

import net.stackoverflow.fastcall.autoconfigure.annotation.EnableFastcall;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFastcall(basePackages = {"net.stackoverflow.fastcall.demo.provider"})
public class FastcallDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastcallDemoProviderApplication.class, args);
    }

}
