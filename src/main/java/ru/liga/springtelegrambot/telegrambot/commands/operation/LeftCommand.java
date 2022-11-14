package ru.liga.springtelegrambot.telegrambot.commands.operation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.liga.springtelegrambot.telegrambot.commands.service.ServiceCommand;


@Component
public class LeftCommand extends ServiceCommand {

    public LeftCommand(@Value("left") String identifier,
                       @Value("Влево") String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Long chatId = chat.getId();

//        if (settings state == search){
        ResponseEntity<byte[]> responseEntity = feignServer.getImgSearchNext(chatId);
//        }else if( state == lovers){
//            methodLovers(absSender);
//        }

        method(absSender, chatId, responseEntity);
    }
}
