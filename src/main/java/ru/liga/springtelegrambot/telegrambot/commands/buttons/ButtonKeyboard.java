package ru.liga.springtelegrambot.telegrambot.commands.buttons;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public interface ButtonKeyboard {

    void setButtons(List<KeyboardRow> keyboardRowList);

    static void getButtonKeyboard(String commandName, SendMessage message) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup(commandName);
        message.setReplyMarkup(replyKeyboardMarkup);
    }

    static void getButtonKeyboard(String commandName, SendPhoto message) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup(commandName);
        message.setReplyMarkup(replyKeyboardMarkup);
    }

    private static ReplyKeyboardMarkup getReplyKeyboardMarkup(String commandName) {
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
        return replyKeyboardMarkup;
    }
}
