package com.fintrack.fintrack_api;

import com.fintrack.fintrack_api.model.Users;
import com.fintrack.fintrack_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class FinTrackApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinTrackApiApplication.class, args);
	}

	@Configuration
	@RequiredArgsConstructor
	public class DataInitializer {
		private final UserRepository userRepo;
		private final PasswordEncoder encoder;

		@Bean
		CommandLineRunner initTestUser() {
			return args -> {
				if (userRepo.findByEmail("test@example.com").isEmpty()) {
					Users user = new Users();
					user.setEmail("test@example.com");
					user.setPassword(encoder.encode("password"));
					user.setName("User name");
					user.setEnabled(true);
					userRepo.save(user);
				}
			};
		}
	}
}
