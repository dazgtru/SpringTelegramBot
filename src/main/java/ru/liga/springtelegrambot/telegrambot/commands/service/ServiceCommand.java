package ru.liga.springtelegrambot.telegrambot.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.springtelegrambot.telegrambot.commands.buttons.ButtonKeyboard;

@Slf4j
abstract public class ServiceCommand extends BotCommand {

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
}
