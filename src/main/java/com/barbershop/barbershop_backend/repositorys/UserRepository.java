package com.barbershop.barbershop_backend.repositorys;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbershop.barbershop_backend.models.User;


public interface UserRepository extends JpaRepository<User, UUID>{

    Optional<User> findByEmail(String email);
}
