package ru.liga.springtelegrambot.telegrambot.commands.buttons;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class ProfileButtons implements ButtonKeyboard {
    @Override
    public void setButtons(List<KeyboardRow> keyboardRowList) {
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton(Commands.EDIT.command));
        keyboardFirstRow.add(new KeyboardButton(Commands.MENU.command));
        keyboardRowList.add(keyboardFirstRow);
    }
}
