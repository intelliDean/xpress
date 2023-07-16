package com.monie.xpress.auth_config.security.auth_utils;

import lombok.Getter;

@Getter
public class WhiteList {

    public static String[] authenticationNotNeeded() {
        return new String[]{
                "/api/v1/auth/**",
                "/api/v1/customer/register",
        };
    }

    public static String[] swagger() {
        return new String[]{
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**"
        };
    }
}
