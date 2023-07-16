package com.monie.xpress.notification.mail;

import com.monie.xpress.notification.mail.dto.EmailRequest;
import com.monie.xpress.notification.mail.dto.MailInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrevoMailImpl implements MailService {
    private final WebClient.Builder webClient;

    @Value("${sendinblue.mail.api_key}")
    private String apiKey;

    @Value("${sendinblue.mail.url}")
    private String mailUrl;

    @Value("${app.name}")
    private String appName;

    @Value("${app.email}")
    private String appEmail;


    @Override
    @Async
    public void sendMail(EmailRequest emailRequest) {
        emailRequest.setSender(new MailInfo(appName, appEmail));

        webClient
                .baseUrl(mailUrl)
                .defaultHeader("api-key", apiKey)
                .build()
                .post()
                .bodyValue(emailRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
