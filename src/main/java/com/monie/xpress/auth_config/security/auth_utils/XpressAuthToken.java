package com.monie.xpress.auth_config.security.auth_utils;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XpressAuthToken {

    private String accessToken;

    private String refreshToken;
}
