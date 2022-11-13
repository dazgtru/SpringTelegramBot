package ru.liga.springtelegrambot.telegrambot.commands.buttons;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public interface ButtonKeyboard {

    void setButtons(SendMessage message);

    static void getButtonKeyboard(String commandName, SendMessage message) {
        switch (commandName) {
            case "search", "lovers", "left", "right" -> {
                new DatabaseButtons().setButtons(message);
            }
            case "profile" -> {
                new ProfileButtons().setButtons(message);
            }
            default -> {
                new DefaultButtons().setButtons(message);
            }
        }
    }
}
