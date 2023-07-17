package com.monie.xpress.auth_config.user.services;

import com.monie.xpress.auth_config.user.data.models.XpressToken;

import java.util.Optional;

public interface XpressTokenService {
    void saveToken(XpressToken heroToken);
    Optional<XpressToken> getValidTokenByAnyToken(String anyToken);
    void revokeToken(String accessToken);
    boolean isTokenValid(String anyToken);
}
