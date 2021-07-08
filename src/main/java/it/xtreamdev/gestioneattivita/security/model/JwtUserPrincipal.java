package it.xtreamdev.gestioneattivita.security.model;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import it.xtreamdev.gestioneattivita.model.User;

public class JwtUserPrincipal {

    private final Jws<Claims> jwsClaims;

    public JwtUserPrincipal(Jws<Claims> jwsClaims) {
        this.jwsClaims = jwsClaims;
    }


    public Jws<Claims> getJwsClaims() {
        return jwsClaims;
    }
}
