package com.monie.xpress.auth_config.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monie.xpress.auth_config.security.auth_utils.JwtService;
import com.monie.xpress.auth_config.security.auth_utils.XpressAuthToken;
import com.monie.xpress.auth_config.security.user_services.AuthenticatedUser;
import com.monie.xpress.auth_config.user.data.models.User;
import com.monie.xpress.auth_config.user.data.models.XpressToken;
import com.monie.xpress.auth_config.user.services.XpressTokenService;
import com.monie.xpress.xceptions.XpressException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class XpressAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final XpressTokenService xpressTokenService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            final User user = objectMapper.readValue(request.getInputStream(), User.class);
            final Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmailAddress(),
                            user.getPassword()
                    );
            final Authentication authenticationResult =
                    authenticationManager.authenticate(authentication);
            if (authenticationResult != null) {
                SecurityContextHolder.getContext().setAuthentication(authenticationResult);
                return SecurityContextHolder.getContext().getAuthentication();
            }
        } catch (IOException ex) {
            throw new XpressException("Authentication failed");
        }
        throw new XpressException("Authentication failed");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        final Map<String, Object> claims = new HashMap<>();
        authResult.getAuthorities().forEach(
                role -> claims.put("claim", role)
        );
        final String email = authResult.getPrincipal().toString();

        final String accessToken = jwtService.generateAccessToken(claims, email);
        final String refreshToken = jwtService.generateRefreshToken(email);

       final AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) userDetailsService.loadUserByUsername(email);

        final XpressToken heroToken = XpressToken.builder()
                .user(authenticatedUser.getUser())
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .revoked(false)
                .expired(false)
                .build();
        xpressTokenService .saveToken(heroToken);

        final XpressAuthToken authenticationToken =
                XpressAuthToken.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), authenticationToken);
    }
}
