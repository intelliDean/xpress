package com.monie.xpress.auth_config.user.services;

import com.monie.xpress.auth_config.user.data.dtos.UserDTO;
import com.monie.xpress.auth_config.user.data.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface UserService {

    User findUserByEmail(String email);


    void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;

    User getCurrentUser();

    UserDTO currentUser();

    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;
}
