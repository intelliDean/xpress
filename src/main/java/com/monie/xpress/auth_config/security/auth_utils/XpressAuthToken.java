package com.monie.xpress.auth_config.security.auth_utils;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XpressAuthToken {
    //this is the class that access and refresh token are being mapped to at the point of logging in
    private String accessToken;

    private String refreshToken;
}
