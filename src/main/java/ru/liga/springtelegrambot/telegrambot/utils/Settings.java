package ru.liga.springtelegrambot.telegrambot.utils;

import lombok.Data;

@Data
public class Settings {
    private String state;

    public Settings(String state) {
        this.state = state;
    }
}
