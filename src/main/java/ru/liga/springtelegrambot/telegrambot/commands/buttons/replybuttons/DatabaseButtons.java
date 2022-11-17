package ru.liga.springtelegrambot.telegrambot.commands.buttons.replybuttons;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class DatabaseButtons implements ButtonKeyboard {
    @Override
    public void setButtons(List<KeyboardRow> keyboardRowList) {
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton(Commands.LEFT.command));
        keyboardFirstRow.add(new KeyboardButton(Commands.MENU.command));
        keyboardFirstRow.add(new KeyboardButton(Commands.RIGHT.command));
        keyboardRowList.add(keyboardFirstRow);
    }
}
