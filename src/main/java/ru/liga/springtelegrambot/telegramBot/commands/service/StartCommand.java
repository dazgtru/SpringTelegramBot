package ru.liga.springtelegrambot.telegramBot.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.liga.springtelegrambot.telegramBot.Bot;
import ru.liga.springtelegrambot.telegramBot.controller.feign.FeignRegistry;
import ru.liga.springtelegrambot.telegramBot.data.Profile;
import ru.liga.springtelegrambot.telegramBot.utils.Settings;
import ru.liga.springtelegrambot.telegramBot.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StartCommand extends ServiceCommand {

    @Autowired
    private FeignRegistry feignRegistry;

    public StartCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Settings userSettings = Bot.getUserSettings(chat.getId());
        if (userSettings.getState().equals("-")) {
            //register
            Profile profile = new Profile(2L,
                                    "Lena",
                                    "Сударыня",
                                    "описание анкеты второго пользователя, лол",
                                    "Сударь",
                                    0L,
                                    0L);
            Long anyName = feignRegistry.setProfile(profile);
            userSettings.setState("menu");
        }
        log.info(Bot.getUserSettings(chat.getId()).toString());
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
