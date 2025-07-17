package com.aluminate.aluminate_global_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
public class AluminateGlobalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AluminateGlobalBackendApplication.class, args);
		System.out.println("✅ Aluminate Global Backend is running" );
	}

	@RestController
    static
    class PingController {
		@GetMapping("/ping")
		public Map<String, String> ping() {
			return Map.of("status", "ok");
		}
	}
}
