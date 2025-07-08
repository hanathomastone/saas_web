package com.kaii.dentix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 스케줄링 활성화
@SpringBootApplication
public class DentixApplication {
    private static String active; // active profile

    // static 변수에는 @Value 어노테이션이 동작하지 않는다. setter 메서드를 이용하여 값이 할당되도록 하면 된다.
    @Value("${spring.profiles.active}")
    private void setActive(String value) {
        active = value;
    }

    private static final Logger log = LoggerFactory.getLogger(DentixApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DentixApplication.class, args);
        log.warn(String.format(" ::: [DTVTN Application Run] * ACTIVE PROFILE --> %s ::: ", active));
    }

}
