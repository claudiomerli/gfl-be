package it.xtreamdev.gflbe.security.model;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import it.xtreamdev.gflbe.model.User;

public class JwtUserPrincipal {

    private final Jws<Claims> jwsClaims;
    private final User user;

    public JwtUserPrincipal(Jws<Claims> jwsClaims, User user) {
        this.jwsClaims = jwsClaims;
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
