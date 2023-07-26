package com.monie.xpress.notification.mail;

import com.monie.xpress.notification.mail.dto.EmailRequest;

public interface MailService {
    // String sendMail(EmailRequest emailRequest);
    void sendMail(EmailRequest request);

}


//sendVerificationMail(customer)
//    .thenAccept(response -> {
//        if (response != null) {
//            customerRepository.save(customer);
//            return CustomerRegistrationResponse.builder()
//                    .data("Registration successful! Please check your email")
//                    .build();
//        } else {
//            throw new TaskHubException("Registration failed");
//        }
//    }).
//
//    exceptionally(ex ->
//
//    {
//        throw new TaskHubException("Registration failed");
//    });