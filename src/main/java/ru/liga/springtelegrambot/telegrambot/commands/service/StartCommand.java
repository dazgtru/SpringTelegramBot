package ru.liga.springtelegrambot.telegrambot.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.springtelegrambot.telegrambot.Bot;
import ru.liga.springtelegrambot.telegrambot.data.ProfileService;
import ru.liga.springtelegrambot.telegrambot.data.RegistrationsStates;
import ru.liga.springtelegrambot.telegrambot.data.Settings;
import ru.liga.springtelegrambot.telegrambot.data.UserStates;
import ru.liga.springtelegrambot.telegrambot.utils.Utils;

@Slf4j
@Component
public class StartCommand extends ServiceCommand {

    private final ProfileService profileService;

    public StartCommand(@Value("start") String identifier,
                        @Value("Старт") String description,
                        @Autowired ProfileService profileService) {
        super(identifier, description);
        this.profileService = profileService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Settings userSettings = Bot.getUserSettings(chat.getId());
        userSettings.setState(UserStates.UNREGISTERED);
        userSettings.setRegistrationsState(RegistrationsStates.NOT_REGISTERED);
        if (userSettings.getState().equals(UserStates.UNREGISTERED)) {
            SendMessage message = profileService.registration(userSettings, chat.getId());
            try {
                absSender.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), Utils.getUserName(user),
                    "Давайте начнём! Если Вам нужна помощь, нажмите /help");
        }
    }
}
