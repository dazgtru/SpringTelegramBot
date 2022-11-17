package ru.liga.springtelegrambot.telegrambot.commands.buttons.replybuttons;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class DefaultButtons implements ButtonKeyboard {

    @Override
    public void setButtons(List<KeyboardRow> keyboardRowList) {
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton(Commands.SEARCH.command));
        keyboardFirstRow.add(new KeyboardButton(Commands.PROFILE.command));
        keyboardFirstRow.add(new KeyboardButton(Commands.LOVERS.command));
        keyboardRowList.add(keyboardFirstRow);
    }
}
