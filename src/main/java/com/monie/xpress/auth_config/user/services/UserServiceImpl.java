package com.monie.xpress.auth_config.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monie.xpress.auth_config.security.auth_utils.JwtService;
import com.monie.xpress.auth_config.security.auth_utils.XpressAuthToken;
import com.monie.xpress.auth_config.security.user_services.AuthenticatedUser;
import com.monie.xpress.auth_config.user.data.dtos.UserDTO;
import com.monie.xpress.auth_config.user.data.models.User;
import com.monie.xpress.auth_config.user.data.models.XpressToken;
import com.monie.xpress.auth_config.user.data.repositories.UserRepository;
import com.monie.xpress.xceptions.UserNotAuthorizedException;
import com.monie.xpress.xceptions.UserNotFoundException;
import com.monie.xpress.xceptions.XpressException;
import com.monie.xpress.xpress_utils.XpressUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.monie.xpress.xpress_utils.XpressConstants.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final XpressTokenService xpressTokenService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public User findUserByEmail(String email) {
        return findUser(email).orElseThrow(UserNotFoundException::new);
    }

    private Optional<User> findUser(String email) {
        return userRepository.findUserByEmailAddress(email);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String header = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(header) && StringUtils.startsWithIgnoreCase(header, BEARER)) {
            final String accessToken = header.substring(BEARER.length());

            if (jwtService.isValid(accessToken)) {
                xpressTokenService.revokeToken(accessToken);
                SecurityContextHolder.clearContext();

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), "User Logout successfully");
            }
        }
    }

    @Override
    public User getCurrentUser() {
        try {
            final AuthenticatedUser authenticatedUser =
                    (AuthenticatedUser) SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getPrincipal();
            return authenticatedUser.getUser();
        } catch (Exception ex) {
            throw new UserNotAuthorizedException();
        }
    }

    @Override
    public UserDTO currentUser() {
        return new ModelMapper().map(
                getCurrentUser(),
                UserDTO.class
        );
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) ||
                !StringUtils.startsWithIgnoreCase(authHeader, BEARER))
            return;

        final String refreshToken = authHeader.substring(BEARER.length());

        if (jwtService.isValid(refreshToken)) {
            final String email = jwtService.extractUsernameFromToken(refreshToken);

            if (StringUtils.hasText(email)) {
                final User user = findUserByEmail(email);

                final String accessToken = jwtService.generateAccessToken(
                        XpressUtils.getUserAuthority(user),
                        user.getEmailAddress()
                );
                final XpressAuthToken newLoginTokens =
                        XpressAuthToken.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();
                final XpressToken xpressToken =
                        xpressTokenService.getValidTokenByAnyToken(refreshToken)
                                .orElseThrow(() -> new XpressException("Token could not be found"));
                xpressToken.setAccessToken(accessToken);
                xpressTokenService.saveToken(xpressToken);

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), newLoginTokens);
            }
        }
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

//    private static Map<String, Object> getUserAuthority(User savedUser) {
//        return savedUser.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role.name()))
//                .collect(
//                        Collectors.toMap(
//                                authority -> "claim",
//                                Function.identity()
//                        )
//                );
//    }
}
