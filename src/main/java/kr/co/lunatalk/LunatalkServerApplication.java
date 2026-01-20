package kr.co.lunatalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LunatalkServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LunatalkServerApplication.class, args);
	}

}
