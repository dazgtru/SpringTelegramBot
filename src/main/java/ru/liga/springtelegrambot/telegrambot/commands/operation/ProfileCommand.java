package ru.liga.springtelegrambot.telegrambot.commands.operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.liga.springtelegrambot.telegrambot.commands.service.ServiceCommand;

@Slf4j
@Component
public class ProfileCommand extends ServiceCommand {


    public ProfileCommand(@Value("profile") String identifier,
                          @Value("Анкета") String description) {

        super(identifier, description);

    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Long chatId = chat.getId();

        ResponseEntity<byte[]> responseEntity = feignServer.getImgMyProfile(chatId);

        method(absSender, chatId, responseEntity);
    }
}
