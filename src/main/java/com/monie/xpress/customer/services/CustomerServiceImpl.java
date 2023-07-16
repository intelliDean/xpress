package com.monie.xpress.customer.services;

import com.monie.xpress.auth_config.security.auth_utils.JwtService;
import com.monie.xpress.auth_config.security.auth_utils.XpressAuthToken;
import com.monie.xpress.auth_config.user.data.enums.Role;
import com.monie.xpress.auth_config.user.data.models.User;
import com.monie.xpress.auth_config.user.data.models.XpressToken;
import com.monie.xpress.auth_config.user.services.XpressTokenService;
import com.monie.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.monie.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.monie.xpress.airtime.service.AirtimePurchaseService;
import com.monie.xpress.customer.data.dtos.CustomerRegisterRequest;
import com.monie.xpress.customer.data.dtos.CustomerRegistrationResponse;
import com.monie.xpress.customer.data.dtos.CustomerResponse;
import com.monie.xpress.customer.data.models.Customer;
import com.monie.xpress.customer.data.repositories.CustomerRepository;
import com.monie.xpress.notification.mail.MailService;
import com.monie.xpress.notification.mail.dto.EmailRequest;
import com.monie.xpress.notification.mail.dto.MailInfo;
import com.monie.xpress.verification_token.VerificationToken;
import com.monie.xpress.verification_token.VerificationTokenService;
import com.monie.xpress.xceptions.UserNotFoundException;
import com.monie.xpress.xceptions.XpressException;
import com.monie.xpress.xpress_utils.XpressUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final VerificationTokenService verificationTokenService;
    private final XpressTokenService xpressTokenService;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private final AirtimePurchaseService airtimePurchaseService;
    private final MailService mailService;
    private final JwtService jwtService;

    @Override
    public CustomerResponse signUp(CustomerRegisterRequest request) {
        Customer savedCustomer = customerRepository.save(
                Customer.builder()
                        .user(
                                User.builder()
                                        .fullName(request.getFullName())
                                        .emailAddress(request.getEmailAddress())

                                        .password(passwordEncoder.encode(request.getPassword()))
                                        .isEnabled(false)
                                        .roles(Collections.singleton(Role.CUSTOMER))
                                        .build())
                        .phoneNumber(request.getPhoneNumber())
                        .balance(BigDecimal.ZERO)
                        .build());
        sendVerificationMail(savedCustomer);

        return CustomerResponse.builder()
                .message("Successful! Check your mail to verify")
                .build();
    }

    @Override
    public CustomerRegistrationResponse verifyCustomerMail(String token, String email) {
        VerificationToken verificationToken = verificationTokenService.findByTokenAndEmail(token, email);
        Customer customer = getCustomerByEmail(email);
        if (customer != null && verificationToken != null) {
            customer.getUser().setEnabled(true);
            verificationToken.setRevoked(true);
            XpressToken xpressToken = saveXpressToken(customer);
            return CustomerRegistrationResponse.builder()
                .message("Registration successful")
                .xpressAuthToken(
                        XpressAuthToken.builder()
                                .accessToken(xpressToken.getAccessToken())
                                .refreshToken(xpressToken.getRefreshToken())
                                .build()
                ).build();
        }
        throw new XpressException("Verification failed");
    }

    @Override
    public CompletableFuture<AirtimePurchaseResponse> buyAirtime(PurchaseAirtimeRequestDTO requestDTO) throws IOException {
        return airtimePurchaseService.buyAirtime(requestDTO);
    }

    private Customer getCustomerByEmail(String email) {
        return customerRepository.findByUser_EmailAddress(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private XpressToken saveXpressToken(Customer customer) {
        final User user = customer.getUser();
        final String accessToken = jwtService.generateAccessToken(
                XpressUtils.getUserAuthority(user),
                user.getEmailAddress()
        );
        final String refreshToken =
                jwtService.generateRefreshToken(user.getEmailAddress());
        XpressToken xpressToken = XpressToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();
        xpressTokenService.saveToken(xpressToken);
        return xpressToken;
    }

    private void sendVerificationMail(Customer customer) {
        final String fullName = customer.getUser().getFullName();
        final String url = generateUrl(customer);

        final Context context = new Context();
        context.setVariables(
                Map.of(
                        "fullName", fullName,
                        "verifyUrl", url,
                        "token", url
                )
        );
        final String content = templateEngine.process("verify_mail", context);
        mailService.sendMail(
                EmailRequest.builder()
                        .to(Collections.singletonList(new MailInfo(fullName, customer.getUser().getEmailAddress())))
                        .subject("Email verification")
                        .htmlContent(content)
                        .build()
        );
    }

    private String generateUrl(Customer customer) {
        final String token = XpressUtils.generateToken(10);
        final String email = customer.getUser().getEmailAddress();
        verificationTokenService.saveToken(
                VerificationToken.builder()
                        .token(token)
                        .email(email)
                        .revoked(false)
                        .expired(false)
                        .build()
        );
        return getUrl(email, token);
    }

    private static String getUrl(String email, String token) {
        return "http://localhost:9090/api/v1/auth/verify" + "?token=" + token + "&email=" + email;
    }

//    public static String generateToken() {
//        byte[] bytes = new byte[10];
//        new SecureRandom().nextBytes(bytes);
//        return Base64.getUrlEncoder()
//                .withoutPadding()
//                .encodeToString(bytes);
//    }
}
