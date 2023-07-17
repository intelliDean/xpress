package com.monie.xpress.admin;

import com.monie.xpress.auth_config.user.data.enums.Role;
import com.monie.xpress.auth_config.user.data.models.User;
import com.monie.xpress.auth_config.user.services.UserService;
import com.monie.xpress.xceptions.XpressException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SuperAdminService {
    @Value("${admin.full_name}")
    private String fullName;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

//    @PostConstruct
//    private void createSuperAdmin() {
//      User superAdmin = User.builder()
//                        .fullName(fullName)
//                        .emailAddress(email)
//                        .password(passwordEncoder.encode(password))
//                        .isEnabled(true)
//                        .roles(Collections.singleton(Role.SUPER_ADMIN))
//                        .build();
//      if (doesAdminExist()) {
//          throw new XpressException("There cannot be more than one super admin logged in");
//      }
//        userService.saveUser(superAdmin);
//    }
    @PreDestroy
    public void deleteSuperAdmin() {
        User user = userService.findUserByEmail(email);
        userService.deleteUser(user);
    }
    private boolean doesAdminExist() {
       return userService.getAllUsers().stream()
                .map(user -> isExist(user.getRoles())).isParallel();
    }
    private boolean isExist(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.compareTo(Role.SUPER_ADMIN))
                .isParallel();

    }
}
