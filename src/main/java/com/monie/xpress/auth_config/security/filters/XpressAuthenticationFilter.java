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
    //this is the first filter that intercepts the authentication request from the user
    private final AuthenticationManager authenticationManager;
    private final XpressTokenService xpressTokenService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;

    @Override		//this method attempts to authenticate the user
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            //user credentials are gotten from the request
            final User user = objectMapper.readValue(request.getInputStream(), User.class);
            //an authentication object is created with the user credentials but not authenticated
            final Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmailAddress(),
                            user.getPassword()
                    );
            //the authentication object is sent to the authentication manager, who then sends it to a provider
            final Authentication authenticationResult =
                    authenticationManager.authenticate(authentication);
            if (authenticationResult != null) {
                //if the authentication is successful then the object will not be null
                //the result of the authentication is then set into the security context holder
                SecurityContextHolder.getContext().setAuthentication(authenticationResult);
                return SecurityContextHolder.getContext().getAuthentication();
            }
        } catch (IOException ex) {
            throw new XpressException("Authentication failed");
        }
        throw new XpressException("Authentication failed");
    }

    @Override	//if the authentication is successful, this method is called
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        //the authentication result carries the user details
        final Map<String, Object> claims = new HashMap<>();
        //user authority/role is gotten from the authentication result
        authResult.getAuthorities().forEach(
                role -> claims.put("claim", role)
        );
        //principal is the user username
        final String email = authResult.getPrincipal().toString();
        //user is loaded from the database using the email
        final AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) userDetailsService.loadUserByUsername(email);
        //aaccess and refresh token are generated after the authentication is successful
        final String accessToken = jwtService.generateAccessToken(claims, email);
        final String refreshToken = jwtService.generateRefreshToken(email);

        //xpress token is an in house token created to internally validate the jwt token
        //against logging out from the backend
        final XpressToken xpressToken = XpressToken.builder()
                .user(authenticatedUser.getUser())
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .revoked(false)
                .expired(false)
                .build();
        xpressTokenService.saveToken(xpressToken);

        //the token is return to the user
        final XpressAuthToken xpressAuthToken = XpressAuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), xpressAuthToken);
    }
}
