package com.auth.service.service;

import com.auth.service.dto.LoginRequestDTO;
import com.auth.service.exception.UnAuthorizedException;
import com.auth.service.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil ){
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public String authenticate(LoginRequestDTO loginRequest){

        try{
            //generating token with the users credentials
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    );

            //initiating authentication process using spring security
            //passing the authentication token created in earlier step
            Authentication authResult = authenticationManager.authenticate(authentication);

            String role = authResult.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElseThrow();

            return jwtUtil.generateToken(authResult.getName(), role);
        }catch (AuthenticationException ex){
            throw new UnAuthorizedException("Invalid email or password");
        }
    }
}
