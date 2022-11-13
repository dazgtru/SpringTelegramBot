package ru.liga.springtelegrambot.telegrambot.commands.buttons;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public interface ButtonKeyboard {

    void setButtons(List<KeyboardRow> keyboardRowList);

    static void getButtonKeyboard(String commandName, SendMessage message) {
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        switch (commandName) {
            case "search", "lovers", "left", "right" ->
                new DatabaseButtons().setButtons(keyboardRowList);
            case "profile" ->
                new ProfileButtons().setButtons(keyboardRowList);
            default ->
                new DefaultButtons().setButtons(keyboardRowList);
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        message.setReplyMarkup(replyKeyboardMarkup);
    }
}
