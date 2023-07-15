package com.monie.xpress.auth_config.user.services;

import com.monie.xpress.auth_config.user.data.models.XpressToken;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class XpressTokenServiceImpl implements XpressTokenService {

    @Override
    public void saveToken(XpressToken heroToken) {

    }

    @Override
    public Optional<XpressToken> getValidTokenByAnyToken(String anyToken) {
        return Optional.empty();
    }

    @Override
    public void revokeToken(String accessToken) {

    }

    @Override
    public boolean isTokenValid(String anyToken) {
        return false;
    }
}
