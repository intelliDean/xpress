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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fullName;

    @Column(unique = true,  nullable = false)
    private String emailAddress;

    private String password;

    private String phoneNumber;

    private Set<Role> roles;

    private LocalDateTime registeredAt = LocalDateTime.now();

    private boolean isEnabled;
}