package com.payment.xpress.auth_config.data.models;

import com.payment.xpress.auth_config.data.enums.Role;
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

    private String emailAddress;

    private String password;

    private String phoneNumber;

    private Set<Role> roles;

    private LocalDateTime registeredAt;
}
