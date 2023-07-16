package com.monie.xpress.verification_token;


public interface VerificationTokenService {

    void saveToken(VerificationToken verificationToken);
    boolean isValid(VerificationToken verificationToken);
    VerificationToken findByTokenAndEmail(String token, String email);
}
