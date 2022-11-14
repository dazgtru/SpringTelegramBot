package ru.liga.springtelegrambot.telegrambot.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.liga.springtelegrambot.telegrambot.Bot;
import ru.liga.springtelegrambot.telegrambot.data.ProfileService;
import ru.liga.springtelegrambot.telegrambot.data.Settings;
import ru.liga.springtelegrambot.telegrambot.data.UserStates;
import ru.liga.springtelegrambot.telegrambot.utils.Utils;

@Slf4j
@Component
public class StartCommand extends ServiceCommand {


    public StartCommand(@Value("start") String identifier,
                        @Value("Старт") String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Settings userSettings = Bot.getUserSettings(chat.getId());
        ProfileService profileService = new ProfileService();
        userSettings.setState(UserStates.UNREGISTERED);
        String userName = Utils.getUserName(user);
        if (userSettings.getState().equals(UserStates.UNREGISTERED)) {
            SendMessage message = profileService.registration(userSettings, chat.getId());
            sendInlineAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, message);
        } else {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                    "Давайте начнём! Если Вам нужна помощь, нажмите /help");
        }
    }
}
