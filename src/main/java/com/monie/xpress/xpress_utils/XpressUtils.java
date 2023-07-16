package com.monie.xpress.xpress_utils;

import com.monie.xpress.auth_config.user.data.models.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class XpressUtils {

    public static Map<String, Object> getUserAuthority(User savedUser) {
        return savedUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(
                        Collectors.toMap(
                                authority -> "claim",
                                Function.identity()
                        )
                );
    }

    public static String generateToken(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
