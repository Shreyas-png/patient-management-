package com.auth.service.filter;

import com.auth.service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    final private JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer")){
            log.info("blank header or token");
            filterChain.doFilter(request, response);
            return;
        }

        try{

            String token = authHeader.substring(7);
            Claims claims = jwtUtil.verifyAndGetAllClaims(token);

            String role = claims.get("role", String.class);
            String username = claims.getSubject();

            Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Setting authentication: {}", authentication);

            filterChain.doFilter(request, response);

        }catch (JwtException ex){
            log.info("Jwt Validation Failed {}", ex.getMessage());
            throw new BadCredentialsException("Invalid JWT");
        }catch(IllegalArgumentException ex){
            log.info("Invalid Jwt token {}", ex.getMessage());
            throw new BadCredentialsException("Invalid JWT");
        }

    }
}
