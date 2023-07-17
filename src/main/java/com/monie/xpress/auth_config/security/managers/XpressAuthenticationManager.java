package com.monie.xpress.auth_config.security.managers;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class XpressAuthenticationManager implements AuthenticationManager {
    //the authentication manager is not responsible for the authentication
    //of the user but stands like a router. it directs the authentication request
    //to the provider who does the authentication
     private final AuthenticationProvider authenticationProvider;
    @Override //the authentication manager passes the created authentication object to the provider
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return authenticationProvider.authenticate(authentication);
    }
}
