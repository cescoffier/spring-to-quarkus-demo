package me.escoffier.spring.todo;

import org.springframework.boot.SpringApplication;

public class TestApplication {
	public static void main(String[] args) {
		SpringApplication
			.from(TodoApplication::main)
			.with(ContainersConfig.class)
			.run(args);
	}
}
