package ru.liga.springtelegrambot.telegrambot.commands.operation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.liga.springtelegrambot.telegrambot.commands.service.ServiceCommand;
import ru.liga.springtelegrambot.telegrambot.utils.Utils;


@Component
public class RightCommand extends ServiceCommand {
    public RightCommand(@Value("right") String identifier,
                        @Value("Вправо") String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);
        //TODO Запрос в БД на следующую запись Search или Lovers
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Command /right");
    }
}
