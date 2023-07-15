package com.monie.xpress;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@Slf4j
@SpringBootApplication
public class XpressMonie {

    public static void main(String[] args) {
        SpringApplication.run(XpressMonie.class, args);
        log.info(":<:>: XpressMonie Server Running :<:>:");
    }
}