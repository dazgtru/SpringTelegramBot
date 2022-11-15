package ru.liga.springtelegrambot.telegrambot.commands.operation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.liga.springtelegrambot.telegrambot.Bot;
import ru.liga.springtelegrambot.telegrambot.commands.service.ServiceCommand;
import ru.liga.springtelegrambot.telegrambot.data.Settings;
import ru.liga.springtelegrambot.telegrambot.data.UserStates;
import ru.liga.springtelegrambot.telegrambot.utils.Utils;


@Component
public class MenuCommand extends ServiceCommand {
    public MenuCommand(@Value("menu") String identifier,
                       @Value("Меню") String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Long chatId = chat.getId();
        String userName = Utils.getUserName(user);
        Settings userSettings = Bot.getUserSettings(chatId);
        userSettings.setState(UserStates.MENU);
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                "Вы перешли в меню.");
    }
}
