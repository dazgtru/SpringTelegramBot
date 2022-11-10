package ru.liga.springtelegrambot.telegrambot.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.liga.springtelegrambot.telegrambot.Bot;
import ru.liga.springtelegrambot.telegrambot.client.feign.FeignRegistry;
import ru.liga.springtelegrambot.telegrambot.data.Profile;
import ru.liga.springtelegrambot.telegrambot.utils.Settings;
import ru.liga.springtelegrambot.telegrambot.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class StartCommand extends ServiceCommand {

    private final FeignRegistry feignRegistry;

    public StartCommand(@Value("start") String identifier,
                        @Value("Старт") String description,
                        @Autowired FeignRegistry feignRegistry) {
        super(identifier, description);
        this.feignRegistry = feignRegistry;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Settings userSettings = Bot.getUserSettings(chat.getId());
        if (userSettings.getState().equals("-")) {
            //register
            //Сервис по работе с профилями.
            Profile profile = new Profile(2L,
                    "Lena",
                    "Сударыня",
                    "Анкета пользователя зарегистрированного в телеграмме.",
                    "Сударь",
                    0L,
                    0L);
            profile.setChatId(chat.getId());
            Long anyName = feignRegistry.setProfile(profile);
            if (!anyName.equals(chat.getId())) {
                log.error(String.format("Чат id - {%s}, результат обращения к бд - {%s}", chat.getId(), anyName));
            }
            userSettings.setState("menu");
        }


        String userName = Utils.getUserName(user);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/search"));
        keyboardFirstRow.add(new KeyboardButton("/profile"));
        keyboardFirstRow.add(new KeyboardButton("/lovers"));
        keyboardRowList.add(keyboardFirstRow);

        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Давайте начнём! Если Вам нужна помощь, нажмите /help", keyboardRowList);
    }
}
