package com.fintrack.fintrack_api;

import com.fintrack.fintrack_api.model.Users;
import com.fintrack.fintrack_api.model.enums.Role;
import com.fintrack.fintrack_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

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
					user.setFirstName("First name");
					user.setLastName("Last name");
					user.setPhoneNumber("+381637425500");
					user.setRoles(List.of(Role.USER.getValue()));
					user.setEnabled(true);
					userRepo.save(user);
				}

				if (userRepo.findByEmail("admin@example.com").isEmpty()) {
					Users user = new Users();
					user.setEmail("admin@example.com");
					user.setPassword(encoder.encode("admin"));
					user.setFirstName("Admin first name");
					user.setLastName("Admin last name");
					user.setPhoneNumber("+381631111111");
					user.setRoles(List.of(Role.USER.getValue(), Role.ADMIN.getValue()));
					user.setEnabled(true);
					userRepo.save(user);
				}
			};
		}
	}
}
