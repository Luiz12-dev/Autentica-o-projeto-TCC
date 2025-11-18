package com.barbershop.barbershop_backend.security.services;

       

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.barbershop.barbershop_backend.dto.AuthResponseDto;
import com.barbershop.barbershop_backend.dto.LoginRequestDto;
import com.barbershop.barbershop_backend.dto.RegisterRequestDto;
import com.barbershop.barbershop_backend.dto.RegisterResponseDto;
import com.barbershop.barbershop_backend.exceptions.DuplicatedEmailException;
import com.barbershop.barbershop_backend.models.User;
import com.barbershop.barbershop_backend.repositorys.UserRepository;
import com.barbershop.barbershop_backend.security.Jwt.JwtUtils;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,AuthenticationManager authenticationManager, JwtUtils jwtUtils){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public RegisterResponseDto register(RegisterRequestDto req){

        //Verificação email pré-existente
        if(userRepository.findByEmail(req.email()).isPresent()){
           throw new DuplicatedEmailException(req.email());
        }

        //Criação novo User com base no req
        User user = User.builder()
        .fullName(req.username())
        .email(req.email())
        //Hashing
        .password(passwordEncoder.encode(req.password()))
        .role(req.role())
        .build();

        User savedUser = userRepository.save(user);

        return RegisterResponseDto.formatResponse(savedUser);
    }

    
    public AuthResponseDto login(LoginRequestDto loginReq){
        //Entrada do Jwt Generate Token precisa de um parâmtro Authentication;


        Authentication authentication =
         authenticationManager
         .authenticate(new UsernamePasswordAuthenticationToken(loginReq.email(), loginReq.password()));
        //SecurityContextHolder salvamento temporárário de Usuário


        SecurityContextHolder.getContext().setAuthentication(authentication);
        

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtUtils.generateToken(authentication);
        
        return new AuthResponseDto(accessToken);
    }

}
