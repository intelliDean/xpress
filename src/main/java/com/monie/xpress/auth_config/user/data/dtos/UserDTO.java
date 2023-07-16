package com.monie.xpress.auth_config.user.data.dtos;

import com.monie.xpress.auth_config.user.data.enums.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String fullName;

    private String emailAddress;

    private Set<Role> roles;

    private LocalDateTime registeredAt;

    private boolean isEnabled;
}
