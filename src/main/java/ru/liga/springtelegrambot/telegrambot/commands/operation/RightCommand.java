package ru.liga.springtelegrambot.telegrambot.commands.operation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.liga.springtelegrambot.telegrambot.commands.service.ServiceCommand;
import ru.liga.springtelegrambot.telegrambot.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@Component
public class RightCommand extends ServiceCommand {
    public RightCommand(@Value("right") String identifier,
                        @Value("Вправо") String description) {
        super(identifier, description);
    }

//    new RightCommand("right", "Вправо")

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);

        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Command /right");
    }
}
