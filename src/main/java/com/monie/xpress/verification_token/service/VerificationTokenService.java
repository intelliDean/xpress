package com.monie.xpress.verification_token.service;


import com.monie.xpress.verification_token.model.VerificationToken;

public interface VerificationTokenService {

    void saveToken(VerificationToken verificationToken);
    boolean isValid(VerificationToken verificationToken);
    VerificationToken findByTokenAndEmail(String token, String email);
}
