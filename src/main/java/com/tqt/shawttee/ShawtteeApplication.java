package com.tqt.shawttee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ShawtteeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShawtteeApplication.class, args);
	}

}
