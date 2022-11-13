package ru.liga.springtelegrambot.telegrambot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.springtelegrambot.telegrambot.commands.operation.*;
import ru.liga.springtelegrambot.telegrambot.commands.service.HelpCommand;
import ru.liga.springtelegrambot.telegrambot.commands.service.StartCommand;
import ru.liga.springtelegrambot.telegrambot.config.BotConfig;
import ru.liga.springtelegrambot.telegrambot.client.feign.FeignRegistry;
import ru.liga.springtelegrambot.telegrambot.data.ProfileService;
import ru.liga.springtelegrambot.telegrambot.utils.RegistrationsStates;
import ru.liga.springtelegrambot.telegrambot.utils.Settings;
import ru.liga.springtelegrambot.telegrambot.utils.UserStates;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class Bot extends TelegramLongPollingCommandBot {

    private final String BOT_NAME;
    private final String BOT_TOKEN;

    @Getter
    private static final Settings defaultSettings = new Settings(UserStates.UNREGISTERED, RegistrationsStates.NOT_REGISTERED);

    @Getter
    private static Map<Long, Settings> userSettings;

    public Bot(BotConfig config, FeignRegistry feignRegistry,
               @Autowired StartCommand startCommand,
               @Autowired HelpCommand helpCommand,
               @Autowired MenuCommand menuCommand,
               @Autowired ProfileCommand profileCommand,
               @Autowired EditCommand editCommand,
               @Autowired SearchCommand searchCommand,
               @Autowired LoversCommand loversCommand,
               @Autowired LeftCommand leftCommand,
               @Autowired RightCommand rightCommand) {
        super();
        BOT_NAME = config.getBotName();
        BOT_TOKEN = config.getBotToken();

        registerAll(startCommand,
                helpCommand,
                menuCommand,
                profileCommand,
                editCommand,
                searchCommand,
                loversCommand,
                leftCommand,
                rightCommand);
        userSettings = new HashMap<>();
        log.info("Бот успешно запущен.");
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
        if (update.hasMessage() || update.hasCallbackQuery()) {
            if (update.hasCallbackQuery()) {
                callbackQuery(update.getCallbackQuery());
            } else if (update.hasMessage()){
                Message message = update.getMessage();
                Long chatId = message.getChatId();
                Settings userSettings = getUserSettings(chatId);
                switch (userSettings.getRegistrationsState()) {
                    case GET_NAME -> {
                        setAnswer(chatId, "Вас зовут - " + message.getText());
                        userSettings.setRegistrationsState(RegistrationsStates.GET_DESC);
                        setAnswer(chatId, "Опишите себя(при этом первая строка - заголовок, последующие - описание)");
                    }

                    case GET_DESC -> {
                        setAnswer(chatId, "Ваше описание: \n" + message.getText());
                        userSettings.setRegistrationsState(RegistrationsStates.GET_DESC);
                        ProfileService profileService = new ProfileService();
                        setAnswer(profileService.registration(userSettings, chatId));
                    }

                    default -> {
                        String answer = "Напишите или выберите команду из кнопок.";
                        setAnswer(chatId, answer);
                    }
                }
            } else {
                log.info("Not supported yet");
            }
        }
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

    private void setAnswer(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(String.format("Ошибка %s. Сообщение, не являющееся командой. Пользователь: %s", e.getMessage(),
                    sendMessage.getChatId()));
            e.printStackTrace();
        }
    }

    private void callbackQuery(CallbackQuery callback) {
        Long chatId = callback.getMessage().getChatId();
        Settings userSettings = getUserSettings(chatId);
        ProfileService profileService = new ProfileService();

        switch (userSettings.getRegistrationsState()) {
            case NOT_REGISTERED -> {
                if (callback.getData().equals("YES_BUTTON")) {
                    log.info("Создаем профиль и присваиваем chatId");
                    userSettings.setRegistrationsState(RegistrationsStates.GET_GENDER);
                    setAnswer(profileService.registration(userSettings, chatId));
                } else {
                    setAnswer(chatId, "Когда надумаете зарегистрироваться напишите /start");
                }
            }
            case GET_GENDER -> {
                if (callback.getData().equals("MALE_BUTTON")) {
                    setAnswer(chatId, "Вы выбрали сударъ");
                    userSettings.setRegistrationsState(RegistrationsStates.GET_NAME);
                    setAnswer(chatId, "Как вас величать?");
                } else {
                    setAnswer(chatId, "Вы выбрали сударыня");
                    userSettings.setRegistrationsState(RegistrationsStates.GET_NAME);
                    setAnswer(chatId, "Как вас величать?");
                }
            }
            case GET_DESC -> {
                switch (callback.getData()) {
                    case "MALES_BUTTON" -> {
                        setAnswer(chatId, "Вы выбрали сударей");
                        userSettings.setRegistrationsState(RegistrationsStates.REGISTERED);
                        setAnswer(chatId, "Вы зарегистрированы!");
                    }
                    case "FEMALES_BUTTON" -> {
                        setAnswer(chatId, "Вы выбрали сударынь");
                        userSettings.setRegistrationsState(RegistrationsStates.REGISTERED);
                        setAnswer(chatId, "Вы зарегистрированы!");
                    }
                    case "ALL_BUTTON" -> {
                        setAnswer(chatId, "Вы выбрали всех");
                        userSettings.setRegistrationsState(RegistrationsStates.REGISTERED);
                        setAnswer(chatId, "Вы зарегистрированы!");
                    }
                }
            }
        }
    }
}
