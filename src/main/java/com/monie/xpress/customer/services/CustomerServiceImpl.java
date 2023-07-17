package com.monie.xpress.customer.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.monie.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.monie.xpress.airtime.data.dtos.AirtimeResponse;
import com.monie.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.monie.xpress.airtime.service.AirtimePurchaseService;
import com.monie.xpress.auth_config.security.auth_utils.JwtService;
import com.monie.xpress.auth_config.security.auth_utils.XpressAuthToken;
import com.monie.xpress.auth_config.user.data.enums.Role;
import com.monie.xpress.auth_config.user.data.models.User;
import com.monie.xpress.auth_config.user.data.models.XpressToken;
import com.monie.xpress.auth_config.user.services.XpressTokenService;
import com.monie.xpress.customer.data.dtos.CustomerRegisterRequest;
import com.monie.xpress.customer.data.dtos.CustomerRegistrationResponse;
import com.monie.xpress.customer.data.dtos.CustomerResponse;
import com.monie.xpress.customer.data.models.Customer;
import com.monie.xpress.customer.data.repositories.CustomerRepository;
import com.monie.xpress.notification.mail.MailService;
import com.monie.xpress.notification.mail.dto.EmailRequest;
import com.monie.xpress.notification.mail.dto.MailInfo;
import com.monie.xpress.verification_token.model.VerificationToken;
import com.monie.xpress.verification_token.service.VerificationTokenService;
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

    @Override	//the method that register user
    public CustomerResponse signUp(CustomerRegisterRequest request) {
        //customer object is created and initialized with its field parameters
        Customer savedCustomer = customerRepository.save(
                Customer.builder()
                        .user(
                                User.builder()	//user object is created inside the customer object to manage memory
                                        .fullName(request.getFullName())
                                        .emailAddress(request.getEmailAddress())
                                        .password(passwordEncoder.encode(request.getPassword()))
                                        .isEnabled(false)
                                        .roles(Collections.singleton(Role.CUSTOMER))
                                        .build())
                        .phoneNumber(request.getPhoneNumber())
                        .balance(BigDecimal.ZERO)
                        .build());
        sendVerificationMail(savedCustomer);	//verification mail is sent to user after registration

        return CustomerResponse.builder()
                .message("Successful! Check your mail to verify")
                .build();
    }

    @Override	//this method verifies the user email after sign up
    public CustomerRegistrationResponse verifyCustomerMail(String token, String email) {
        //the verification token saved in the database is retrieved, using email and string token
        VerificationToken verificationToken = verificationTokenService.findByTokenAndEmail(token, email);
        //if the token object is found, the email is also used to find user
        Customer customer = getCustomerByEmail(email);
        //if both user and verification token are found, then the user account is activated
        if (customer != null && verificationToken != null) {
            customer.getUser().setEnabled(true);	//the user account is activated
            verificationToken.setRevoked(true);		//the verification token is revoked
            //xpress token is created and saved.
            // user is also saved automatically because of the cascade relationship between them
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
    public AirtimePurchaseResponse buyAirtime(PurchaseAirtimeRequestDTO requestDTO) throws IOException {
        //this method is called when user wants to purchase airtime
        return airtimePurchaseService.buyAirtime(requestDTO);
    }

    private Customer getCustomerByEmail(String email) {	//customer retrieves by email
        return customerRepository.findByUser_EmailAddress(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private XpressToken saveXpressToken(Customer customer) {		//xpress token is created and saved
        final User user = customer.getUser();
        //this generates the access token
        final String accessToken = jwtService.generateAccessToken(
                XpressUtils.getUserAuthority(user),
                user.getEmailAddress()
        );
        //this generates the refresh token
        final String refreshToken = jwtService.generateRefreshToken(user.getEmailAddress());
        XpressToken xpressToken = XpressToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();
        xpressTokenService.saveToken(xpressToken);		//token is saved
        return xpressToken;
    }

    private void sendVerificationMail(Customer customer) {
        //this sends a verification email to user using thymeleaf template
        final String fullName = customer.getUser().getFullName();
        final String url = generateUrl(customer);	//this generates the url for the verification endpoint

        final Context context = new Context();
        context.setVariables(	//these are the placeholders in the email template
                Map.of(
                        "fullName", fullName,
                        "verifyUrl", url,
                        "token", url
                )
        );
        final String content = templateEngine.process("verify_mail", context);
       EmailRequest request = EmailRequest.builder()
                        .to(Collections.singletonList(new MailInfo(fullName, customer.getUser().getEmailAddress())))
                        .subject("Email verification")
                        .htmlContent(content)
                        .build();
        mailService.sendMail(request);
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
        return XpressUtils.getUrl(email, token);
    }
}
