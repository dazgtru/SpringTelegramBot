package ru.liga.springtelegrambot.telegrambot.commands.operation;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

abstract public class OperationCommand extends BotCommand {
    public OperationCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        //TODO Можно воткнуть Feign сли правильно понимаю
    }
}
