package ru.liga.springtelegrambot.telegrambot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.springtelegrambot.telegrambot.commands.buttons.ButtonKeyboard;
import ru.liga.springtelegrambot.telegrambot.commands.operation.*;
import ru.liga.springtelegrambot.telegrambot.commands.service.HelpCommand;
import ru.liga.springtelegrambot.telegrambot.commands.service.StartCommand;
import ru.liga.springtelegrambot.telegrambot.config.BotConfig;
import ru.liga.springtelegrambot.telegrambot.client.feign.FeignServer;
import ru.liga.springtelegrambot.telegrambot.data.*;
import ru.liga.springtelegrambot.telegrambot.utils.ByteToImage;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class Bot extends TelegramLongPollingCommandBot {

    private final String BOT_NAME;
    private final String BOT_TOKEN;

    @Autowired
    public FeignServer feignServer;

    @Autowired
    private RowProfile rowProfile;

    @Autowired
    private NonCommand nonCommand;


    @Getter
    private static final Settings defaultSettings = new Settings(UserStates.UNREGISTERED, RegistrationsStates.NOT_REGISTERED);

    @Getter
    private static Map<Long, Settings> userSettings;
    @Autowired
    private ByteToImage byteToImage;


    public Bot(BotConfig config,
               @Autowired RowProfile rowProfile,
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

        this.rowProfile = rowProfile;

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
            settings = new Settings(defaultSettings);
            userSettings.put(chatId, settings);
            return settings;
        }
        return settings;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage() || update.hasCallbackQuery()) {

            if (update.hasCallbackQuery()) {

                CallbackQuery callbackQuery = update.getCallbackQuery();
                Long chatId = callbackQuery.getMessage().getChatId();
                Settings userSettingsFromCallback = getUserSettings(chatId);

                switch (userSettingsFromCallback.getRegistrationsState()) {
                    case NOT_REGISTERED -> {
                        sendMessage(nonCommand.getProfileRegistrationService(chatId, userSettingsFromCallback, callbackQuery));
                    }
                    case GET_GENDER -> {
                        sendMessage(nonCommand.setProfileGender(chatId, userSettingsFromCallback, callbackQuery));
                    }
                    case GET_DESC -> {
                        sendMessage(nonCommand.setProfileGenderSearch(chatId, callbackQuery));
                        SendPhoto sendPhoto = nonCommand.finishRegister(chatId, userSettingsFromCallback);
                        ButtonKeyboard.getButtonKeyboard("menu", sendPhoto);
                        sendMessage(sendPhoto);
                        sendMessage(chatId, "Ваш профиль сохранён!\nМожем приступить.");
                    }
                }
            } else if (update.hasMessage()) {

                Long chatId = update.getMessage().getChatId();
                Settings userSettingsFromMessage = getUserSettings(chatId);
                Message message = update.getMessage();

                switch (userSettingsFromMessage.getRegistrationsState()) {
                    case GET_NAME -> {
                        sendMessage(nonCommand.setProfileName(chatId, userSettingsFromMessage, message));
                    }
                    case GET_DESC -> {
                        sendMessage(nonCommand.setProfileDescription(chatId, userSettingsFromMessage, message));
                    }
                }

            } else {
                log.info("Not supported yet");
            }
        }
    }

    public void sendMessage(Long chatId, String text) {
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

    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(String.format("Ошибка %s. Сообщение, не являющееся командой. Пользователь: %s", e.getMessage(),
                    sendMessage.getChatId()));
            e.printStackTrace();
        }
    }

    public void sendMessage(SendPhoto sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(String.format("Ошибка %s. Сообщение, не являющееся командой. Пользователь: %s", e.getMessage(),
                    sendMessage.getChatId()));
            e.printStackTrace();
        }
    }
}
