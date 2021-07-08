package it.xtreamdev.gestioneattivita.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import it.xtreamdev.gestioneattivita.security.model.JwtUserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (Objects.isNull(header)) {
            chain.doFilter(request,response);
            return;
        }

        String PREFIX = "Bearer ";
        String token = header.replace(PREFIX,"");

        try {
            Jws<Claims> claimsJws = this.jwtTokenUtil.validateToken(token);

            SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(new JwtUserPrincipal(claimsJws), Collections.emptyList()));
        } catch (Exception e) {
            log.error("Error during authentication", e);

        }

        chain.doFilter(request,response);
    }
}
