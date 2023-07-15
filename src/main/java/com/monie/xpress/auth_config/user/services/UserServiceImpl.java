package com.monie.xpress.auth_config.user.services;

import com.monie.xpress.auth_config.security.auth_utils.JwtService;
import com.monie.xpress.auth_config.user.data.dtos.UserDTO;
import com.monie.xpress.auth_config.user.data.models.User;
import com.monie.xpress.auth_config.user.data.repositories.UserRepository;
import com.monie.xpress.xceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

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
        return userRepository.findByEmailAddress(email);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    @Override
    public User getCurrentUser() {
        return null;
    }

    @Override
    public UserDTO currentUser() {
        return null;
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}
