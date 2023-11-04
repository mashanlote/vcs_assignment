package com.mashanlote.secuirty;

import org.springframework.security.core.GrantedAuthority;

public class SecurityAuthority implements GrantedAuthority {

    private final String authority;

    public SecurityAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
