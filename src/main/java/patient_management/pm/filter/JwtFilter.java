package patient_management.pm.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import patient_management.pm.utility.JwtUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        //Getting the bearer token
        String authHeader = request.getHeader("Authorization");

        //If the bearer token is not present or does not start with Bearer
        // the job of this filter is over so directly handing over the execution to spring security
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        //extracting token from header passing 7 as begin index because first 7 chars will be "Bearer "
        String token = authHeader.substring(7);

        try{
            //Calling method to verify jwt token
            Claims claims = jwtUtil.getAllClaims(token);

            //getting all required fields after verification
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            //creating authentication object so that we can store this in spring security context
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );

            //setting security context so that we can use this in our controller
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }catch(JwtException ex){
            log.info("JWT validation exception occurred {}", ex.getMessage());
            throw new BadCredentialsException("Invalid JWT");
        }catch(IllegalArgumentException ex){
            log.info("Invalid JWT token {}", ex.getMessage());
            throw new BadCredentialsException("Invalid JWT");
        }
    }
}
