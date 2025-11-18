package com.barbershop.barbershop_backend.security.Jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.barbershop.barbershop_backend.security.services.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{


    private final JwtUtils jwtUtils;
   
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Lista de rotas públicas que não precisam de autenticação
   private static final List<String> PUBLIC_PATHS = Arrays.asList(
        // Swagger/OpenAPI
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-resources/**",
        
        // Autenticação
        "/api/auth/**",
        
        // Recursos estáticos
        "/error",
        "/favicon.ico"
    );
    
    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtUtils = jwtUtils;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

     @Override
protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();

    return PUBLIC_PATHS.stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, path));
    
    
}
    
    @Override
    protected void doFilterInternal(HttpServletRequest req,
     HttpServletResponse res, 
     FilterChain filterChain) throws IOException, ServletException{
        try {
            String token = parseJwt(req);

            if(token != null && jwtUtils.validateToken(token)){

                String username = jwtUtils.getUsernameFromToken(token);

                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth = 
                new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                SecurityContextHolder.getContext().setAuthentication(auth);
                
            }
            
        } catch (Exception e) {
            logger.error("Error in set Usernane from token", e);
        }
        filterChain.doFilter(req, res);
    }

    public String parseJwt(HttpServletRequest req){
        String head = req.getHeader("Authorization");

        if(StringUtils.hasText(head) && head.startsWith("Bearer ")){
            return head.substring(7);
        }
        return null;

    }
}
