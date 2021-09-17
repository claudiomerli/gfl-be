package it.xtreamdev.gflbe.security;

import io.jsonwebtoken.*;
import it.xtreamdev.gflbe.model.Content;
import it.xtreamdev.gflbe.model.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenUtil {

    private static final String authTokenSecret = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private static final Long expiration = 3600L;


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

    private static final String customerKey = "6SCSj39TF74qAgL4SYM2BnLJkuLZeyBg";

    public String createJwtCustomerCodeFromContentId() {
        return Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + (3154000000L * 1000)))
                .signWith(SignatureAlgorithm.HS256, customerKey)
                .compact();
    }

    public void verifyCustomerJwt(String jwt, Content content) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(customerKey)
                    .parseClaimsJws(jwt);

            if (!content.getCustomerToken().equals(jwt)) {
                throw new RuntimeException("Token not valid");
            }

        } catch (Exception e) {
            throw new RuntimeException("Token not valid");
        }
    }
}
