package com.hotnerds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HotnerdsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotnerdsApplication.class, args);
	}

}
