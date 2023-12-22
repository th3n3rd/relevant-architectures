package com.example.architectures.auth;

import static org.springframework.security.core.authority.AuthorityUtils.NO_AUTHORITIES;

import com.example.architectures.common.ConsultantId;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

class ConsultantAuthToken extends AbstractAuthenticationToken {

    private final Jwt token;

    public ConsultantAuthToken(Jwt token) {
        super(NO_AUTHORITIES);
        setAuthenticated(true);
        this.token = token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return ConsultantId.of(token.getClaimAsString("consultantId"));
    }
}
