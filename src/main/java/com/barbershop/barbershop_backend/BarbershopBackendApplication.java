package com.barbershop.barbershop_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.barbershop.barbershop_backend.Enum.Role;
import com.barbershop.barbershop_backend.models.User;
import com.barbershop.barbershop_backend.repositorys.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BarbershopBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarbershopBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner seedUsuario(UserRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (usuarioRepository.count() == 0) {
				User usuario = User.builder()
						.fullName("luiz")
						.email("admin2@email.com")
						.password(passwordEncoder.encode("123456"))
						.role(Role.OWNER)
						.isActive(true)
						.build();

				usuarioRepository.save(usuario);
				log.info(">>> Usuário seed criado: login='admin2@email.com', senha='123456'");
			} else {
				log.info(">>> Usuário seed já existe. Pulando criação.");
			}
		};
	}

}
