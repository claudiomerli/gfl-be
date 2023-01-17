package it.xtreamdev.gflbe.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.security.model.JwtUserPrincipal;
import it.xtreamdev.gflbe.service.UserService;
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
import java.util.Objects;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

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
            String username = claimsJws.getBody().getSubject();
            User userLoaded = this.userService.findByUsername(username);

            SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(new JwtUserPrincipal(claimsJws,userLoaded)));
        } catch (Exception e) {
            log.error("Error during authentication", e);

        }

        chain.doFilter(request,response);
    }
}
