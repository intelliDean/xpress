package com.monie.xpress.notification.mail;

import com.monie.xpress.notification.mail.dto.EmailRequest;
import reactor.core.publisher.Mono;

public interface MailService {
   // String sendMail(EmailRequest emailRequest);
    void sendMail(EmailRequest request);
}
