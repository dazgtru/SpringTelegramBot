package ru.liga.springtelegrambot.telegrambot.commands.operation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.liga.springtelegrambot.telegrambot.Bot;
import ru.liga.springtelegrambot.telegrambot.commands.service.ServiceCommand;
import ru.liga.springtelegrambot.telegrambot.data.Settings;
import ru.liga.springtelegrambot.telegrambot.data.UserStates;


@Component
public class SearchCommand extends ServiceCommand {
    public SearchCommand(@Value("search") String identifier,
                         @Value("Поиск") String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Long chatId = chat.getId();
        Settings userSettings = Bot.getUserSettings(chatId);
        userSettings.setState(UserStates.SEARCH_DATABASE);
        ResponseEntity<byte[]> responseEntity = feignServer.getImgSearchNext(chatId);
        sendProfileImage(absSender, chatId, responseEntity, this.getCommandIdentifier());
    }
}
