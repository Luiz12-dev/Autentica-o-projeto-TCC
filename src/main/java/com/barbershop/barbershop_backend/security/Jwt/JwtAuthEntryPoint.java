package com.barbershop.barbershop_backend.security.Jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint{

    public static final Logger logger = LoggerFactory.getLogger(JwtAuthEntryPoint.class);
    //Porteiro da API
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

                logger.error("Unauthorized error: {}", authException.getMessage());


                response.setContentType(MediaType.APPLICATION_JSON_VALUE);// formato de resposta
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);// Error:401

                //bilhete de resposta
                final Map<String, Object> bodyError = new HashMap<>();
                bodyError.put("status", HttpServletResponse.SC_UNAUTHORIZED);
                bodyError.put("error", "Unauthorized");
                bodyError.put("message", authException.getMessage());
                bodyError.put("path", request.getServletPath());

                final ObjectMapper mapper = new ObjectMapper();

                mapper.writeValue(response.getOutputStream(),bodyError);
    }

}
