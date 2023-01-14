package it.xtreamdev.gflbe.security;

import io.jsonwebtoken.*;
import it.xtreamdev.gflbe.model.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenUtil {

    private static final String authTokenSecret = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private static final Long expiration = 2592000L;


    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + (expiration * 1000)))
                .signWith(SignatureAlgorithm.HS256, authTokenSecret)
                .compact();
    }

    public Jws<Claims> validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(authTokenSecret)
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new RuntimeException("Token not valid", e);
        }
    }


}
