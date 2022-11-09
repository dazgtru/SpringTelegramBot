package ru.liga.springtelegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpringTelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringTelegramBotApplication.class, args);
    }

}
