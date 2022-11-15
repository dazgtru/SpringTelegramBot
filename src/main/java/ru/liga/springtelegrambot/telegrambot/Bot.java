package ru.liga.springtelegrambot.telegrambot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    private RowProfile rowProfile;

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
                callbackQuery(update.getCallbackQuery());
            } else if (update.hasMessage()){
                Message message = update.getMessage();
                Long chatId = message.getChatId();
                Settings userSettings = getUserSettings(chatId);

                switch (userSettings.getRegistrationsState()) {
                    case GET_NAME -> {
                        rowProfile.setProfileName(chatId, message.getText());
                        setAnswer(chatId, "Вас зовут - " + message.getText());
                        userSettings.setRegistrationsState(RegistrationsStates.GET_DESC);
                        setAnswer(chatId, "Опишите себя(при этом первая строка - заголовок, последующие - описание)");
                    }

                    case GET_DESC -> {
                        rowProfile.setProfileDescription(chatId, message.getText());
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

    private void callbackQuery(CallbackQuery callback) {
        Long chatId = callback.getMessage().getChatId();
        Settings userSettings = getUserSettings(chatId);
        ProfileService profileService = new ProfileService();

        switch (userSettings.getRegistrationsState()) {
            case NOT_REGISTERED -> {
                if (callback.getData().equals("YES_BUTTON")) {
                    log.info("Создаем профиль и присваиваем chatId");
                    rowProfile.addNewProfile(chatId);
                    userSettings.setRegistrationsState(RegistrationsStates.GET_GENDER);
                    setAnswer(profileService.registration(userSettings, chatId));
                } else {
                    setAnswer(chatId, "Когда надумаете зарегистрироваться напишите /start");
                }
            }
            case GET_GENDER -> {
                if (callback.getData().equals("MALE_BUTTON")) {
                    rowProfile.setProfileGender(chatId, "сударъ");
                    setAnswer(chatId, "Вы выбрали сударъ");
                } else {
                    rowProfile.setProfileGender(chatId, "сударыня");
                    setAnswer(chatId, "Вы выбрали сударыня");
                }
                userSettings.setRegistrationsState(RegistrationsStates.GET_NAME);
                setAnswer(chatId, "Как вас величать?");
            }
            case GET_DESC -> {
                switch (callback.getData()) {
                    case "MALES_BUTTON" -> {
                        rowProfile.setProfileGenderSearch(chatId, "сударъ");
                        setAnswer(chatId, "Вы выбрали сударей");
                    }
                    case "FEMALES_BUTTON" -> {
                        rowProfile.setProfileGenderSearch(chatId, "сударыня");
                        setAnswer(chatId, "Вы выбрали сударынь");
                    }
                    case "ALL_BUTTON" -> {
                        rowProfile.setProfileGenderSearch(chatId, "все");
                        setAnswer(chatId, "Вы выбрали всех");
                    }
                }

                ResponseEntity<byte[]> responseEntity = feignServer.setProfile(rowProfile.getProfile(chatId));
                byte[] bytes = responseEntity.getBody();
                SendPhoto sendPhoto = byteToImage.convertByteToImage(bytes, chatId);
                ButtonKeyboard.getButtonKeyboard("menu", sendPhoto);
                try {
                    execute(sendPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                userSettings.setRegistrationsState(RegistrationsStates.REGISTERED);
                userSettings.setState(UserStates.MENU);
                setAnswer(chatId, "Ваш профиль сохранён!\nМожем приступить.");
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
}
