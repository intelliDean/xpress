package com.monie.xpress.auth_config.security.user_services;

import com.monie.xpress.auth_config.user.data.models.User;
import com.monie.xpress.auth_config.user.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class XpressUserDetailsService implements UserDetailsService {

    private final UserService userService;
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = userService.findUserByEmail(username);
        return new AuthenticatedUser(user);
    }
}
