package com.aluminate.aluminate_global_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AluminateGlobalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AluminateGlobalBackendApplication.class, args);
		System.out.println("✅ Aluminate Global Backend is running" );
	}

}
