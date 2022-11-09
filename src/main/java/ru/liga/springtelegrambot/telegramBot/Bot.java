package ru.liga.springtelegrambot.telegramBot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.springtelegrambot.telegramBot.commands.operation.*;
import ru.liga.springtelegrambot.telegramBot.commands.service.HelpCommand;
import ru.liga.springtelegrambot.telegramBot.commands.service.StartCommand;
import ru.liga.springtelegrambot.telegramBot.config.BotConfig;
import ru.liga.springtelegrambot.telegramBot.utils.Settings;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class Bot extends TelegramLongPollingCommandBot {

    private final String BOT_NAME;
    private final String BOT_TOKEN;

    @Getter
    private static final Settings defaultSettings = new Settings();

    @Getter
    private static Map<Long, Settings> userSettings;

    public Bot(BotConfig config) {
        super();
        BOT_NAME = config.getBotName();
        BOT_TOKEN = config.getBotToken();

        register(new StartCommand("start", "Старт"));
        register(new HelpCommand("help", "Помощь"));
        register(new MenuCommand("menu", "Меню"));
        register(new ProfileCommand("profile", "Анкета"));
        register(new EditCommand("edit", "Редактировать"));
        register(new SearchCommand("search", "Поиск"));
        register(new LoversCommand("lovers", "Любимцы"));
        register(new LeftCommand("left", "Влево"));
        register(new RightCommand("right", "Вправо"));

        userSettings  = new HashMap<>();

    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    public static Settings getUserSettings(Long chatId) {
        Map<Long, Settings> userSettings = getUserSettings();
        Settings settings = userSettings.get(chatId);
        if (settings == null) {
            return defaultSettings;
        }
        return settings;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();

        String answer = "Напишите или выберите команду из кнопок.";
        setAnswer(chatId, answer);
    }

    private void setAnswer(Long chatId, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            log.error(String.format("Ошибка %s. Сообщение, не являющееся командой. Пользователь: %s", e.getMessage(),
                    chatId));
            e.printStackTrace();
        }
    }
}
