package com.people.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PeopleHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeopleHubApplication.class, args);
	}

}
