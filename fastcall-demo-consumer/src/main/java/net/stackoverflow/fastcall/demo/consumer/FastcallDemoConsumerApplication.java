package net.stackoverflow.fastcall.demo.consumer;

import net.stackoverflow.fastcall.annotation.EnableFastcall;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFastcall
public class FastcallDemoConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FastcallDemoConsumerApplication.class, args);
	}

}
