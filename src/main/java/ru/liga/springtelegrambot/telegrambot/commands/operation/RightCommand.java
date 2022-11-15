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
import ru.liga.springtelegrambot.telegrambot.utils.Utils;


@Component
public class RightCommand extends ServiceCommand {
    public RightCommand(@Value("right") String identifier,
                        @Value("Вправо") String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Long chatId = chat.getId();
        Settings userSettings = Bot.getUserSettings(chatId);


        ResponseEntity<byte[]> responseEntity;
        switch (userSettings.getState()) {
            case SEARCH_DATABASE -> {
                responseEntity = feignServer.getImgSearchLikeAndNext(chatId);
                sendProfileImage(absSender, chatId, responseEntity, this.getCommandIdentifier());
            }
            case LOVERS_DATABASE -> {
                responseEntity = feignServer.getImgLoversNext(chatId);
                sendProfileImage(absSender, chatId, responseEntity, this.getCommandIdentifier());
            }
            default -> {
                sendAnswer(absSender, chatId, this.getCommandIdentifier(), Utils.getUserName(user), "Выберите /search или /lovers.");
            }
        }
    }
}
