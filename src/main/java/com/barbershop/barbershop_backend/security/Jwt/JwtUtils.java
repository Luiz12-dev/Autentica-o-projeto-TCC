package com.barbershop.barbershop_backend.security.Jwt;


import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt-secret-key}")
    private String secretKey;

    @Value("${jwt-expiration-ms}")
    private Long jwtExpiration;

    @Value("${jwt-secret-refresh-token}")
    private String secretRefreshToken;

    @Value("${jwt-expiration-refresh-token}")
    private Long jwtExpirationRefreshToken;

    private Key getSignedKey(String token){ //Converte senha (String) para o formato(Key)
        return Keys.hmacShaKeyFor(token.getBytes());
    }


   public String generateToken(Authentication authentication){

    UserDetails user = (UserDetails) authentication.getPrincipal();

    List<String> roles = user.getAuthorities()
    .stream().map(GrantedAuthority::getAuthority)
    .collect(Collectors.toList());

    return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("roles", roles)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
            .signWith(getSignedKey(secretKey), SignatureAlgorithm.HS512)
            .compact();
   }


   public String generateRefreshToken(Authentication authentication){
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    List<String> roles = userDetails.getAuthorities()
    .stream().map(GrantedAuthority::getAuthority)
    .collect(Collectors.toList());


    return Jwts.builder() //Construtor
    .setSubject(userDetails.getUsername())
    .claim("roles", roles)
    .setIssuedAt(new Date())
    .setExpiration(new Date((new Date()).getTime() + jwtExpirationRefreshToken))
    .signWith(getSignedKey(secretRefreshToken), SignatureAlgorithm.HS512)
    .compact();
   }


   public String getUsernameFromToken(String token){
    return Jwts.parserBuilder()
    .setSigningKey(getSignedKey(secretKey))
    .build()
    .parseClaimsJws(token).getBody().getSubject();
   }

   
   public String getUsernameFromRefreshToken(String token){
    return Jwts.parserBuilder()
    .setSigningKey(getSignedKey(secretRefreshToken))
    .build()
    .parseClaimsJws(token).getBody().getSubject();
   }


   public boolean validateToken(String token){
    try{
        Jwts.parserBuilder()
        .setSigningKey(getSignedKey(secretKey))
        .build()
        .parseClaimsJws(token);

        return true;
    }catch(Exception e){
        logger.error("Jwt token validation failed: {}", e);
    }
    return false;

}

public boolean validateRefreshToken(String token){
    try{
        Jwts.parserBuilder()// Costrutor de Analisador!
        .setSigningKey(getSignedKey(secretRefreshToken))
        .build()
        .parseClaimsJws(token);
        return true;
    }catch(Exception e){
        logger.error("JWT refresh token validation failed: {}", e);
    }
    return false;
}

}
