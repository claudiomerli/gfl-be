package it.xtreamdev.gflbe.security;

import it.xtreamdev.gflbe.security.model.JwtUserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final JwtUserPrincipal jwtUserPrincipal;

    public JwtAuthentication(JwtUserPrincipal jwtUserPrincipal) {
        super(Collections.singletonList(new SimpleGrantedAuthority(jwtUserPrincipal.getUser().getRole().name())));
        this.jwtUserPrincipal = jwtUserPrincipal;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public JwtUserPrincipal getPrincipal() {
        return jwtUserPrincipal;
    }
}
