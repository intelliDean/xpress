package com.monie.xpress.auth_config.security.provider;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@AllArgsConstructor
public class XpressAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String requestEmail = authentication.getPrincipal().toString();
        final String requestPassword = authentication.getCredentials().toString();

        final UserDetails userDetails = userDetailsService.loadUserByUsername(requestEmail);
        final String email = userDetails.getUsername();
        final String password = userDetails.getPassword();
        if (passwordEncoder.matches(requestPassword, password)) {
            return new UsernamePasswordAuthenticationToken(
                    email,
                    password,
                    userDetails.getAuthorities()
            );
        }
        throw new BadCredentialsException("Incorrect username or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
