package com.monie.xpress.auth_config.user.data.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XpressToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String accessToken;

    private String refreshToken;

    private boolean revoked;

    private boolean expired;

    private final LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private User user;
}
