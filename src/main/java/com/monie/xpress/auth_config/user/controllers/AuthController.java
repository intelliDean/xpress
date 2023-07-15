package com.monie.xpress.auth_config.user.controllers;

import com.monie.xpress.auth_config.user.data.dtos.LoginRequest;
import com.monie.xpress.auth_config.user.data.dtos.UserDTO;
import com.monie.xpress.auth_config.user.services.UserService;
import com.monie.xpress.xceptions.XpressException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@AllArgsConstructor
@Tag(name = "Auth Controller")
@RequestMapping("/api/v1/auth")
public class AuthController {
     private final UserService userService;


    @GetMapping("current")
    @Operation(summary = "Get current user logged in")
    public ResponseEntity<UserDTO> currentUser() {
        return ResponseEntity.ok(
                userService.currentUser()
        );
    }
    @PostMapping("/login")
    @Operation(summary = "Login")
    public void login(@RequestBody @Valid LoginRequest request) {
        throw new XpressException("Authentication failed");
    }


    @PostMapping("/logout")
    @Operation(summary = "Logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        userService.logout(request, response);
    }

    @GetMapping("refresh")
    @Operation(summary = "Get refresh token when access token expires")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        userService.refreshToken(request, response);
    }
}
