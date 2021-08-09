package it.xtreamdev.gflbe.security.model;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class JwtUserPrincipal {

    private final Jws<Claims> jwsClaims;

    public JwtUserPrincipal(Jws<Claims> jwsClaims) {
        this.jwsClaims = jwsClaims;
    }


    public Jws<Claims> getJwsClaims() {
        return jwsClaims;
    }
}
