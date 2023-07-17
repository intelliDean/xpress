package com.monie.xpress;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
        info = @Info(
                title = "Xpress Monie API",
                version = "v1",
                description = "This app provides REST APIs documentation for Xpress Monie",
                contact = @Contact(
                        name = "Xpress Monie Support",
                        email = "info@xpressmonie.com"
                )
        ),
        servers = {
                @Server(
                        description = "current",
                        url = "/"
                ),
        },

        security = {
                @SecurityRequirement(
                        name = "Bearer Auth"
                )
        }
)
@SecurityScheme(
        name = "Bearer Auth",
        description = "JWT Authentication",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@Slf4j
@EnableScheduling
@SpringBootApplication
public class XpressMonie {

    public static void main(String[] args) {
        SpringApplication.run(XpressMonie.class, args);
        log.info(":<:>: XpressMonie Server Running :<:>:");
    }
}