package com.monie.xpress.auth_config.user.data.models;

import com.monie.xpress.auth_config.user.data.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;

    @Column(unique = true,  nullable = false)
    private String emailAddress;

    private String password;

    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    private final LocalDateTime registeredAt = LocalDateTime.now();

    private boolean isEnabled;
}