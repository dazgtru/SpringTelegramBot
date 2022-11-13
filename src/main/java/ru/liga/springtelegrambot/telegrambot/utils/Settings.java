package ru.liga.springtelegrambot.telegrambot.utils;

import lombok.Data;

@Data
public class Settings {
    private UserStates state;

    public Settings(UserStates state) {
        this.state = state;
    }
}
