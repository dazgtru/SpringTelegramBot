package ru.liga.springtelegrambot.telegrambot.commands.buttons;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseButtons implements ButtonKeyboard {
    @Override
    public void setButtons(SendMessage message) {
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton(Commands.LEFT.command));
        keyboardFirstRow.add(new KeyboardButton(Commands.MENU.command));
        keyboardFirstRow.add(new KeyboardButton(Commands.RIGHT.command));
        keyboardRowList.add(keyboardFirstRow);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        message.setReplyMarkup(replyKeyboardMarkup);
    }
}
