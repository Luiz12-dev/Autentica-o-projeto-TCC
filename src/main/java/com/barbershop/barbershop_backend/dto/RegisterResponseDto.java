package com.barbershop.barbershop_backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.barbershop.barbershop_backend.Enum.Role;
import com.barbershop.barbershop_backend.models.User;

public record RegisterResponseDto(
      UUID id,
      String username,
      String email,
      Role role,
      LocalDateTime createdAt
) {

      public static RegisterResponseDto formatResponse (User user){
            return new RegisterResponseDto(
                  user.getId(),
                  user.getUsername(),
                  user.getEmail(),
                  user.getRole(),
                  user.getCreatedAt()
            );
      }

  

}
