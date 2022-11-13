package ru.liga.springtelegrambot.telegrambot.utils;

import lombok.Data;

@Data
public class Settings {
    private UserStates state;
    private RegistrationsStates registrationsState;

    public Settings(UserStates state, RegistrationsStates registrationsState) {
        this.state = state;
        this.registrationsState = registrationsState;
    }
}
