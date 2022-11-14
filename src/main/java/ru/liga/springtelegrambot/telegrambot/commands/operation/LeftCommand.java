package ru.liga.springtelegrambot.telegrambot.commands.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.springtelegrambot.telegrambot.client.feign.FeignServer;
import ru.liga.springtelegrambot.telegrambot.commands.service.ServiceCommand;
import ru.liga.springtelegrambot.telegrambot.utils.ByteToImage;
import ru.liga.springtelegrambot.telegrambot.utils.Utils;


@Component
public class LeftCommand extends ServiceCommand {

    private final FeignServer feignServer;
    private final ByteToImage byteToImage;

    public LeftCommand(@Value("left") String identifier,
                       @Value("Влево") String description,
                       @Autowired FeignServer feignServer,
                       @Autowired ByteToImage byteToImage) {
        super(identifier, description);
        this.feignServer = feignServer;
        this.byteToImage = byteToImage;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);
        Long chatId = chat.getId();

//        if (settings state == search){
            ResponseEntity<byte[]> responseEntity = feignServer.getImgSearchLikeAndNext(chatId);
//        }else if( state == lovers){
//            methodLovers(absSender);
//        }

        byte[] bytes = responseEntity.getBody();
        SendPhoto sendPhoto = byteToImage.convertByteToImage(bytes, chatId);
        try {
            absSender.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Command /left");
    }
}
