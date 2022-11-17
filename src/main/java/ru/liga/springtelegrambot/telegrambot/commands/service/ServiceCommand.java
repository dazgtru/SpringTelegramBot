package ru.liga.springtelegrambot.telegrambot.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.springtelegrambot.telegrambot.client.FeignServer;
import ru.liga.springtelegrambot.telegrambot.commands.buttons.ButtonKeyboard;
import ru.liga.springtelegrambot.telegrambot.utils.ByteToImage;

@Slf4j
abstract public class ServiceCommand extends BotCommand {

    @Autowired
    protected ByteToImage byteToImage;

    @Autowired
    protected FeignServer feignServer;

    protected ServiceCommand(String identifier, String description) {
        super(identifier, description);

    }


    /**
     * Отправка ответа пользователю.
     */
    protected void sendAnswer(AbsSender absSender,
                              Long chatId,
                              String commandName,
                              String userName,
                              String text) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        ButtonKeyboard.getButtonKeyboard(commandName, message);

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            log.error(String.format("Ошибка %s. Команда %s. Пользователь: %s",
                    e.getMessage(), commandName, userName));
            e.printStackTrace();
        }
    }

    protected void sendProfileImage(AbsSender absSender, Long chatId, ResponseEntity<byte[]> responseEntity, String commandName) {
        byte[] bytes = responseEntity.getBody();
        SendPhoto sendPhoto = byteToImage.convertByteToImage(bytes, chatId);
        ButtonKeyboard.getButtonKeyboard(commandName, sendPhoto);
        try {
            absSender.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
