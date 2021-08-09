package it.xtreamdev.gflbe.security;

import it.xtreamdev.gflbe.security.model.JwtUserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private JwtUserPrincipal jwtUserPrincipal;

    public JwtAuthentication(JwtUserPrincipal jwtUserPrincipal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwtUserPrincipal = jwtUserPrincipal;
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
