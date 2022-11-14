package ru.liga.springtelegrambot.telegrambot.data;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ProfileImage {

    private String name;
    private String gender;
    private String relation;
    private byte[] bytesArray;

}
