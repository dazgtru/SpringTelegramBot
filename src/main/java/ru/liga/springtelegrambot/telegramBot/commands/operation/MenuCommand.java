package ru.liga.springtelegrambot.telegramBot.commands.operation;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.liga.springtelegrambot.telegramBot.commands.service.ServiceCommand;
import ru.liga.springtelegrambot.telegramBot.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MenuCommand extends ServiceCommand {
    public MenuCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/search"));
        keyboardFirstRow.add(new KeyboardButton("/profile"));
        keyboardFirstRow.add(new KeyboardButton("/lovers"));
        keyboardRowList.add(keyboardFirstRow);

        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Command /menu", keyboardRowList);
    }
}
